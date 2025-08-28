package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import java.io.Serializable;

/**
 * A class to represent one of two boundaries of a {@link VersionRangeElement}.
 *
 * This also defines the {@link Lower} and {@link Upper} interfaces for the
 * respective ends and a {@link WithVersion} class for boundaries that base on a
 * {@link BaseVersion}.
 *
 * <p>
 * <strong>This class is not meant to be extended!</strong><br>
 * All {@code Boundary} sub classes have to be comparable with each other, which
 * can only be guaranteed if all of these are known beforehand.
 *
 * <p>
 * The following sub classes exist and should suffice:
 * <ul>
 *   <li>{@link UnlimitedUpperBoundary}</li>
 *   <li>{@link ExclusiveUpperBoundary}</li>
 *   <li>{@link InclusiveUpperBoundary}</li>
 *   <li>{@link UnlimitedLowerBoundary}</li>
 *   <li>{@link ExclusiveLowerBoundary}</li>
 *   <li>{@link InclusiveLowerBoundary}</li>
 * </ul>
 */
public sealed interface Boundary extends Comparable<Boundary>, Serializable
    permits Boundary.Lower, Boundary.Upper, Boundary.WithVersion {

  /**
   * Lower {@link Boundary}s of {@link VersionRangeElement}s.
   */
  @SuppressWarnings("PMD.ImplicitFunctionalInterface")
  public sealed interface Lower extends Boundary
      permits ExclusiveLowerBoundary, InclusiveLowerBoundary, UnlimitedLowerBoundary {

    /**
     * Compares this instance with the specified {@link Version} for order.
     *
     * Returns a negative {@code int}, zero ({@code 0}) or a positive
     * {@code int} as this {@code Boundary} is smaller, equal to or greater
     * than the {@code Version} compared to.
     *
     * @param version the {@code Version} to check compare to
     * @return a negative {@code int}, zero ({@code 0}) or a positive
     *         {@code int} as this {@code Boundary} is smaller, equal to or
     *         greater than the {@code Version} compared to
     */
    int compareTo(Version version);

    /**
     * Returns {@code true} if the specified {@link Version} is considered
     * included by this {@link Boundary}.
     *
     * This is Synonymous to {@code this.compareTo(version) <= 0}.
     *
     * @param version the {@code Version} to check for inclusion
     * @return {@code true} if this {@code Boundary} includes the
     *         {@code Version}; {@code false} otherwise
     */
    default boolean includesVersion(final Version version) {
      return this.compareTo(version) <= 0;
    }
  }

  /**
   * Upper {@link Boundary}s of {@link VersionRangeElement}s.
   */
  @SuppressWarnings("PMD.ImplicitFunctionalInterface")
  public sealed interface Upper extends Boundary
      permits ExclusiveUpperBoundary, InclusiveUpperBoundary, UnlimitedUpperBoundary {

    /**
     * Compares this instance with the specified {@link Version} for order.
     *
     * Returns a negative {@code int}, zero ({@code 0}) or a positive
     * {@code int} as this {@code Boundary} is smaller, equal to or greater
     * than the {@code Version} compared to.
     *
     * @param version the {@code Version} to check compare to
     * @return a negative {@code int}, zero ({@code 0}) or a positive
     *         {@code int} as this {@code Boundary} is smaller, equal to or
     *         greater than the {@code Version} compared to
     */
    int compareTo(Version version);

    /**
     * /**
     * Returns {@code true} if the specified {@link Version} is considered
     * included by this {@link Boundary}.
     *
     * This is Synonymous to {@code this.compareTo(version) >= 0}.
     *
     * @param version the {@code Version} to check for inclusion
     * @return {@code true} if this {@code Boundary} includes the
     *         {@code Version}; {@code false} otherwise
     */
    default boolean includesVersion(final Version version) {
      return this.compareTo(version) >= 0;
    }
  }

  /**
   * A {@link Boundary} that is based on a {@link BaseVersion}.
   */
  public abstract static sealed class WithVersion implements Boundary
      permits ExclusiveLowerBoundary,
          InclusiveLowerBoundary,
          ExclusiveUpperBoundary,
          InclusiveUpperBoundary {

    protected final BaseVersion version;

    WithVersion(final BaseVersion version) {
      this.version = version;
    }

    /**
     * Returns the {@link BaseVersion} this {@link Boundary} is based on.
     *
     * @return the version
     */
    public final BaseVersion getVersion() {
      return this.version;
    }

    protected final int compareToOr(final Version version, final int ifEqual) {
      final int cmp = this.version.compareTo(version);
      return cmp != 0 ? cmp : ifEqual;
    }
  }

  /**
   * Compares this instance with the specified {@link Version} for order.
   *
   * Returns a negative {@code int}, zero ({@code 0}) or a positive
   * {@code int} as this {@code Boundary} is smaller, equal to or greater
   * than the {@code Version} compared to.
   *
   * @param version the {@code Version} to compare to
   * @return a negative {@code int}, zero ({@code 0}) or a positive
   *         {@code int} as this {@code Boundary} is smaller, equal to or
   *         greater than the {@code Version} compared to
   */
  public int compareTo(Version version);

  /**
   * Returns {@code true} if the specified {@link Version} is considered
   * included by this {@link Boundary}.
   *
   * @param version the {@code Version} to check for inclusion
   * @return {@code true} if this {@code Boundary} includes the
   *         {@code Version}; {@code false} otherwise
   */
  public boolean includesVersion(Version version);

  public boolean equals(Object other);
}
