package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;

/**
 * A {@link Boundary.Upper} for {@link VersionRangeElement}s.
 *
 * This {@link Boundary} does not exclude any {@link Version}s.
 * More formally, this {@code Boundary} {@code B} contains all {@code Version}s
 * {@code v} such that {@code B = {v | v}}.
 */
public final class UnlimitedUpperBoundary implements Boundary.Upper {
  private static final long serialVersionUID = -7219879790612151080L;

  /**
   * Class Constructor.
   */
  public UnlimitedUpperBoundary() {}

  @Override
  public int compareTo(final Boundary boundary) {
    return switch (boundary) {
      case UnlimitedUpperBoundary o -> 0;
      case ExclusiveUpperBoundary o -> 1;
      case InclusiveUpperBoundary o -> 1;
      case UnlimitedLowerBoundary o -> 1;
      case ExclusiveLowerBoundary o -> 1;
      case InclusiveLowerBoundary o -> 1;
    };
  }

  @Override
  public int compareTo(final Version version) {
    return 1;
  }

  @Override
  public boolean includesVersion(final Version version) {
    return Boundary.Upper.super.includesVersion(version);
  }

  @Override
  public String toString() {
    return ")";
  }

  @Override
  public int hashCode() {
    return -1742678920;
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof UnlimitedUpperBoundary;
  }
}
