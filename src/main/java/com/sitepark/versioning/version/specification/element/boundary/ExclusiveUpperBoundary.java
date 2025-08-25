package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;

/**
 * A {@link Boundary.Upper} for {@link VersionRangeElement}s.
 *
 * This {@link Boundary} includes all {@link Version}s, that are greater than
 * the {@link BaseVersion} of this instance.
 * More formally, this {@code Boundary} {@code B} with the {@code BaseVersion}
 * <code>v<sub>B</sub></code> contains all {@code Version}s {@code v} such that
 * <code>B = {v | v &gt; v<sub>B</sub>}</code>.
 */
public final class ExclusiveUpperBoundary extends Boundary.WithVersion implements Boundary.Upper {
  private static final long serialVersionUID = -6978467266255230468L;

  /**
   * Class Constructor specifying the {@link BaseVersion} to compare other
   * {@link Version}s to when determining wether or not it is included by
   * this instance.
   *
   * @param version the {@code BaseVersion} for this instance
   */
  public ExclusiveUpperBoundary(final BaseVersion version) {
    super(version);
  }

  @Override
  public int compareTo(final Boundary boundary) {
    return switch (boundary) {
      case UnlimitedUpperBoundary o -> -1;
      case ExclusiveUpperBoundary o -> this.version.compareTo(o.getVersion());
      case InclusiveUpperBoundary o -> this.compareToOr(o.getVersion(), -1);
      case UnlimitedLowerBoundary o -> 1;
      case ExclusiveLowerBoundary o -> this.compareToOr(o.getVersion(), -1);
      case InclusiveLowerBoundary o -> this.compareToOr(o.getVersion(), -1);
    };
  }

  @Override
  public int compareTo(final Version version) {
    return this.compareToOr(version, -1);
  }

  @Override
  public String toString() {
    return this.version.toString() + ")";
  }

  @Override
  public int hashCode() {
    return 23 + this.version.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final ExclusiveUpperBoundary that && this.version.equals(that.version);
  }
}
