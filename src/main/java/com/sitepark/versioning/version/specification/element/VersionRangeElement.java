package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.VersionsSpecification;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
import com.sitepark.versioning.version.specification.element.boundary.InvalidBoundariesException;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpecificationElement} of a {@link VersionsSpecification}
 * representing a subset of {@link Version}s that logically lie in between two
 * {@link Boundary}s.
 *
 * More formally this class depicts a subset {@code R} with the
 * {@link Boundary.Lower} <code>b<sub>l</sub></code> and the
 * {@link Boundary.Upper} <code>b<sub>u</sub></code> containing all
 * {@code Version}s {@code v} such that
 * <code>R = {v | b<sub>l</sub> ≤ v ≤ b<sub>u</sub>}</code>.
 *
 * <p>
 * Each instance has to define a {@code Branch} to limit it's contained
 * {@code Version}s to.  If a {@code Version} does not have an equal
 * {@code Branch} it may not be considered contained by the
 * {@link VersionRangeElement}.
 */
public final class VersionRangeElement implements SpecificationElement {
  private static final long serialVersionUID = -6318393237749829323L;

  private final Boundary.Lower lower;
  private final Boundary.Upper upper;
  private final Branch branch;

  /**
   * Class Constructor specifying the {@link Boundary.Lower} and
   * {@link Boundary.Upper} instances to define a subset of {@link Version}s.
   *
   * @param lower the lower {@code Boundary}
   * @param upper the upper {@code Boundary}
   * @throws InvalidBoundariesException if the lower {@code Boundary} is
   *                                    considered larger than the upper
   *                                    {@code Boundary}, both are
   *                                    {@link UnlimitedLowerBoundary} and
   *                                    {@link UnlimitedUpperBoundary} instances
   *                                    or both are based on
   *                                    {@link BaseVersion}s with different
   *                                    {@link Branch}es.
   */
  public VersionRangeElement(final Boundary.Lower lower, final Boundary.Upper upper) {
    this.lower = Objects.requireNonNull(lower);
    this.upper = Objects.requireNonNull(upper);
    if (!(this.lower instanceof final Boundary.WithVersion lowerWithVersion)) {
      if (!(this.upper instanceof final Boundary.WithVersion upperWithVersion)) {
        throw new InvalidBoundariesException("atleast one Boundary has to be limited");
      }
      this.branch = upperWithVersion.getVersion().getBranch();
    } else {
      this.branch = lowerWithVersion.getVersion().getBranch();
      if (this.upper instanceof final Boundary.WithVersion upperWithVersion) {
        if (this.lower.compareTo(this.upper) >= 0) {
          throw new InvalidBoundariesException(
              "the lower Boundary has to be smaller than the upper Boundary");
        }
        if (!Objects.equals(
            upperWithVersion.getVersion().getBranch(), lowerWithVersion.getVersion().getBranch())) {
          throw new InvalidBoundariesException("boundaries cannot have different branches");
        }
      }
    }
  }

  /**
   * Returns the {@link Boundary.Lower Lower} {@link Boundary} of this range.
   *
   * @return the boundary
   */
  public Boundary.Lower getLowerBoundary() {
    return this.lower;
  }

  /**
   * Returns the {@link Boundary.Upper Upper} {@link Boundary} of this range.
   *
   * @return the boundary
   */
  public Boundary.Upper getUpperBoundary() {
    return this.upper;
  }

  @Override
  public Branch getBranch() {
    return this.branch;
  }

  /**
   * Returns wether a {@link Version} is contained in the subset represented
   * by this instance.
   *
   * More formally, returns {@code true} if a {@code Version} is specified,
   * such that
   * {@code versionRangeElement.getBoundaries().containsVersion(version)}.
   *
   * @param version the {@code Version} to check
   * @return {@code true} if the {@code Version} is contained in this instance
   */
  @Override
  public boolean containsVersion(final Version version) {
    return this.branch.equals(version.getBranch())
        && this.lower.includesVersion(version)
        && this.upper.includesVersion(version);
  }

  @Override
  public Optional<SpecificationElement> getIntersection(final SpecificationElement element) {
    return switch (element) {
      case ExplicitVersionElement version ->
          this.containsVersion(version.getVersion()) ? Optional.of(version) : Optional.empty();
      case VersionRangeElement range -> {
        final int lowerCmp = this.lower.compareTo(range.lower);
        final int upperCmp = this.upper.compareTo(range.upper);
        if (lowerCmp <= 0 && upperCmp >= 0) {
          yield Optional.of(range);
        }
        if (lowerCmp >= 0 && upperCmp <= 0) {
          yield Optional.of(this);
        }
        try {
          yield Optional.of(
              new VersionRangeElement(
                  lowerCmp < 0 ? range.lower : this.lower,
                  upperCmp > 0 ? range.upper : this.upper));
        } catch (final InvalidBoundariesException exception) {
          yield Optional.empty();
        }
      }
    };
  }

  @Override
  public final ComparisonResult compareTo(final SpecificationElement element) {
    return switch (element) {
      case ExplicitVersionElement version -> {
        final int cmpLower = this.lower.compareTo(version.getVersion());
        if (cmpLower > 0) {
          yield ComparisonResult.HIGHER;
        }
        final int cmpUpper = this.upper.compareTo(version.getVersion());
        if (cmpUpper < 0) {
          yield ComparisonResult.LOWER;
        }
        yield cmpLower == 0 && cmpUpper == 0
            ? ComparisonResult.INTERSECTS_EQUALY
            : ComparisonResult.INTERSECTS_COMPLETELY;
      }
      case VersionRangeElement range -> {
        int cmp = this.lower.compareTo(range.lower);
        if (cmp > 0) {
          if (this.lower.compareTo(range.upper) > 0) {
            yield ComparisonResult.HIGHER;
          }
          if (this.upper.compareTo(range.upper) > 0) {
            yield ComparisonResult.INTERSECTS_HIGHER;
          }
          yield ComparisonResult.INTERSECTS_PARTIALLY;
        }
        if (cmp < 0) {
          if (this.upper.compareTo(range.lower) < 0) {
            yield ComparisonResult.LOWER;
          }
          if (this.upper.compareTo(range.upper) < 0) {
            yield ComparisonResult.INTERSECTS_LOWER;
          }
          yield ComparisonResult.INTERSECTS_COMPLETELY;
        }
        cmp = this.upper.compareTo(range.upper);
        if (cmp > 0) {
          yield ComparisonResult.INTERSECTS_COMPLETELY;
        }
        if (cmp < 0) {
          yield ComparisonResult.INTERSECTS_PARTIALLY;
        }
        yield ComparisonResult.INTERSECTS_EQUALY;
      }
    };
  }

  /**
   * Returns a String representation of this instance.
   *
   * The resulting String are the {@link Boundary.Lower} and
   * {@link Boundary.Upper} instances separated by a comma ({@code ,}).
   *
   * <p>
   * Examples may look like this:<br>
   * {@code [1.0.0,1.5.0)}<br>
   * {@code (1.0.0,1.5.0]}<br>
   * {@code (1.0.0,)}<br>
   * {@code (,1.5.0)}
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    return this.lower.toString() + "," + this.upper.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.upper, this.lower);
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final VersionRangeElement that
        && this.upper.equals(that.upper)
        && this.lower.equals(that.lower);
  }
}
