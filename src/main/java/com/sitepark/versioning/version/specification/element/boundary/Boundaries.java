package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.boundary.Boundary.WithVersion;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A subset of {@link Version}s that logically lie in between two
 * {@link Boundary}s.
 *
 * More formally this class depicts a subset {@code R} with the
 * {@link Boundary.Lower} <code>b<sub>l</sub></code> and the
 * {@link Boundary.Upper} <code>b<sub>u</sub></code> containing all
 * {@code Version}s {@code v} such that
 * <code>R = {v | b<sub>l</sub> ≤ v ≤ b<sub>u</sub>}</code>.
 *
 * <p>
 * Each instance defines a {@code Branch} by having at least one
 * {@link Boundary.WithVersion}, which does have a {@link Version} that in turn
 * has a {@code Branch}.  If both {@link Boundary}s are instances of
 * {@code Boundary.WithVersion} they have to have {@link Version}s with the same
 * {@code Branch}.  Attempting to create a instance without any
 * {@code Boundary.WithVersion}s will result in a
 * {@link InvalidBoundariesException}.
 * If a {@code Version} does not have an equal
 * {@code Branch} it may not be considered contained by the {@link Boundaries}
 * instance.
 *
 * @param <L> the class of the {@link Boundary.Lower}
 * @param <U> the class of the {@link Boundary.Upper}
 */
public final class Boundaries<
        L extends Boundary & Boundary.Lower, U extends Boundary & Boundary.Upper>
    implements Serializable {
  private static final long serialVersionUID = -7784160732508272264L;

  private final L lower;
  private final U upper;
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
   *                                    {@link UnlimitedUpperBoundary}
   *                                    instances or they both extend
   *                                    {@link WithVersion} and have
   *                                    {@link BaseVersion}s with different
   *                                    {@link Branch}es.
   */
  public Boundaries(final L lower, final U upper) {
    this.lower = Objects.requireNonNull(lower);
    this.upper = Objects.requireNonNull(upper);
    if (!(this.lower instanceof WithVersion)) {
      if (!(this.upper instanceof WithVersion)) {
        throw new InvalidBoundariesException("atleast one Boundary has to be limited");
      }
      this.branch = ((WithVersion) this.upper).version.getBranch();
    } else {
      this.branch = ((WithVersion) this.lower).version.getBranch();
      if (this.upper instanceof WithVersion) {
        if (this.lower.compareTo(this.upper) >= 0) {
          throw new InvalidBoundariesException(
              "the lower Boundary has to be smaller" + " than the upper Boundary");
        }
        if (!Objects.equals(((WithVersion) this.upper).version.getBranch(), this.branch)) {
          throw new InvalidBoundariesException("boundaries cannot have different branches");
        }
      }
    }
  }

  /**
   * Returns wether a {@link Version} is contained in the subset represented
   * by this instance.
   *
   * More formally, returns {@code true} if a {@code Version} is specified,
   * such that {@code lowerBoundary ≤ version ≤ upperBoundary}.  If a
   * supplied {@code Version}'s {@code Branch} is not equal to the one of this
   * instance such a comparison cannot be made and therefore {@code false} is
   * returned.
   *
   * @param version the {@code Version} to check
   * @return {@code true} if the {@code Version} is contained in this instance
   * @see Boundary#includesVersion(Version)
   * @see Branch#equals(Object)
   */
  public boolean containsVersion(final Version version) {
    return this.branch.equals(version.getBranch())
        && this.lower.includesVersion(version)
        && this.upper.includesVersion(version);
  }

  /**
   * Returns a {@code Boundaries} instance depicting the intersection between
   * this instance and the specified one.
   *
   * In other words the larger {@link Boundary.Lower} and the smaller
   * {@link Boundary.Upper} are combined.
   *
   * <p>
   * {@code Boundaries} instances can only ever intersect if they both have
   * equal {@link Branch}es.
   *
   * @param other another {@code Boundaries} instance to calculate an
   *              intersection with
   * @return a {@link Optional} containing the intersection or an empty one if
   *         the {@code Boundaries} instances do not intersect
   * @param <A> class of the {@link Boundary.Lower} of the other instance
   * @param <B> class of the {@link Boundary.Upper} of the other instance
   */
  public <A extends Boundary & Boundary.Lower, B extends Boundary & Boundary.Upper>
      Optional<Boundaries<?, ?>> getIntersection(final Boundaries<A, B> other) {
    final int lowerCmp = this.lower.compareTo(other.lower);
    final int upperCmp = this.upper.compareTo(other.upper);
    if (lowerCmp <= 0 && upperCmp >= 0) {
      return Optional.of(other);
    }
    if (lowerCmp >= 0 && upperCmp <= 0) {
      return Optional.of(this);
    }
    try {
      return Optional.of(
          new Boundaries<>(
              lowerCmp < 0 ? other.lower : this.lower, upperCmp > 0 ? other.upper : this.upper));
    } catch (final InvalidBoundariesException exception) {
      return Optional.empty();
    }
  }

  /**
   * Returns this instances {@link Boundary.Lower}.
   *
   * @return the lower {@link Boundary} of this instance.
   * @see #getUpper()
   */
  public L getLower() {
    return this.lower;
  }

  /**
   * Returns this instances {@link Boundary.Upper}.
   *
   * @return the upper {@link Boundary} of this instance.
   * @see #getLower()
   */
  public U getUpper() {
    return this.upper;
  }

  /**
   * Returns the {@link Branch} this instance is limited to.
   *
   * Only {@link Version}s with a equal {@code Branch} may be considered
   * contained by this instance.
   *
   * @return the {@code Branch} of this instance
   * @see #containsVersion(Version)
   */
  public Branch getBranch() {
    return this.branch;
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
    return Objects.hash(this.branch, this.upper, this.lower);
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof Boundaries)) {
      return false;
    }
    final Boundaries<?, ?> that = (Boundaries<?, ?>) other;
    return this.branch.equals(that.branch)
        && this.upper.equals(that.upper)
        && this.lower.equals(that.lower);
  }
}
