package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;

/**
 * A {@link Boundary.Lower} for {@link VersionRangeElement}s.
 *
 * This {@link Boundary} includes all {@link Version}s, that are less than or
 * equal to the {@link BaseVersion} of this instance.
 * More formally, this {@code Boundary} {@code B} with the {@code BaseVersion}
 * <code>v<sub>B</sub></code> contains all {@code Version}s {@code v} such that
 * <code>B = {v | v ≤ v<sub>B</sub>}</code>.
 */
public final class InclusiveLowerBoundary extends Boundary.WithVersion implements Boundary.Lower {
  private static final long serialVersionUID = 4078113850284238058L;

  /**
   * Class Constructor specifying the {@link BaseVersion} to compare other
   * {@link Version}s to when determining wether or not it is included by
   * this instance.
   *
   * @param version the {@code BaseVersion} for this instance
   */
  public InclusiveLowerBoundary(final BaseVersion version) {
    super(version);
  }

  @Override
  public int compareTo(final Boundary boundary) {
    return switch (boundary) {
      case UnlimitedUpperBoundary o -> -1;
      case ExclusiveUpperBoundary o -> this.compareToOr(o.getVersion(), 1);
      case InclusiveUpperBoundary o -> this.version.compareTo(o.getVersion());
      case UnlimitedLowerBoundary o -> 1;
      case ExclusiveLowerBoundary o -> this.compareToOr(o.getVersion(), 1);
      case InclusiveLowerBoundary o -> this.version.compareTo(o.getVersion());
    };
  }

  @Override
  public int compareTo(final Version version) {
    return this.version.compareTo(version);
  }

  @Override
  public String toString() {
    return "[" + this.version.toString();
  }

  @Override
  public int hashCode() {
    return 37 + this.version.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final InclusiveLowerBoundary that && this.version.equals(that.version);
  }
}
