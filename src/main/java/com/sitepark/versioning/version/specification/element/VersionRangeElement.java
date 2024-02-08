package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.VersionsSpecification;
import com.sitepark.versioning.version.specification.element.boundary.Boundaries;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
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
 *
 * @see Boundaries
 */
public final class VersionRangeElement extends SpecificationElement.BoundariesBased {
  private static final long serialVersionUID = -6318393237749829323L;

  private final Boundaries<?, ?> boundaries;

  /**
   * Class constructor specifiying the {@link Boundaries} of this instance.
   *
   * @param boundaries the {@code Boundaries} of this instance
   */
  public VersionRangeElement(final Boundaries<?, ?> boundaries) {
    this.boundaries = Objects.requireNonNull(boundaries);
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
    return this.boundaries.containsVersion(version);
  }

  @Override
  public Boundaries<?, ?> getBoundaries() {
    return this.boundaries;
  }

  @Override
  public Optional<SpecificationElement> getIntersectionWithVersionBased(
      final SpecificationElement.VersionBased other) {
    return this.boundaries.containsVersion(other.getVersion())
        ? Optional.of(other)
        : Optional.empty();
  }

  @Override
  public Optional<SpecificationElement> getIntersectionWithBoundariesBased(
      final SpecificationElement.BoundariesBased other) {
    return this.boundaries.getIntersection(other.getBoundaries()).map(VersionRangeElement::new);
  }

  @Override
  public ComparisonResult compareToVersionBased(final SpecificationElement.VersionBased other) {
    final int cmpLower = this.boundaries.getLower().compareTo(other.getVersion());
    if (cmpLower > 0) {
      return ComparisonResult.HIGHER;
    }
    final int cmpUpper = this.boundaries.getUpper().compareTo(other.getVersion());
    if (cmpUpper < 0) {
      return ComparisonResult.LOWER;
    }
    return cmpLower == 0 && cmpUpper == 0
        ? ComparisonResult.INTERSECTS_EQUALY
        : ComparisonResult.INTERSECTS_COMPLETELY;
  }

  @Override
  public ComparisonResult compareToBoundariesBased(
      final SpecificationElement.BoundariesBased other) {
    int cmp = this.boundaries.getLower().compareTo(other.getBoundaries().getLower());
    if (cmp > 0) {
      if (this.boundaries.getLower().compareTo(other.getBoundaries().getUpper()) > 0) {
        return ComparisonResult.HIGHER;
      }
      if (this.boundaries.getUpper().compareTo(other.getBoundaries().getUpper()) > 0) {
        return ComparisonResult.INTERSECTS_HIGHER;
      }
      return ComparisonResult.INTERSECTS_PARTIALLY;
    }
    if (cmp < 0) {
      if (this.boundaries.getUpper().compareTo(other.getBoundaries().getLower()) < 0) {
        return ComparisonResult.LOWER;
      }
      if (this.boundaries.getUpper().compareTo(other.getBoundaries().getUpper()) < 0) {
        return ComparisonResult.INTERSECTS_LOWER;
      }
      return ComparisonResult.INTERSECTS_COMPLETELY;
    }
    cmp = this.boundaries.getUpper().compareTo(other.getBoundaries().getUpper());
    if (cmp > 0) {
      return ComparisonResult.INTERSECTS_COMPLETELY;
    }
    if (cmp < 0) {
      return ComparisonResult.INTERSECTS_PARTIALLY;
    }
    return ComparisonResult.INTERSECTS_EQUALY;
  }

  @Override
  public Branch getBranch() {
    return this.boundaries.getBranch();
  }

  @Override
  public String toString() {
    return this.boundaries.toString();
  }

  @Override
  public int hashCode() {
    return this.boundaries.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof VersionRangeElement)) {
      return false;
    }
    return this.boundaries.equals(((VersionRangeElement) other).boundaries);
  }
}
