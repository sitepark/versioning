package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.Version;

/**
 * A {@link Boundary.Lower} for {@link Boundaries} instances.
 *
 * This {@link Boundary} does not exclude any {@link Version}s.
 * More formally, this {@code Boundary} {@code B} contains all {@code Version}s
 * {@code v} such that {@code B = {v | v}}.
 */
public final class UnlimitedLowerBoundary extends Boundary implements Boundary.Lower {
  private static final long serialVersionUID = -9159986392093270475L;

  /**
   * Class Constructor.
   */
  public UnlimitedLowerBoundary() {}

  @Override
  public int compareTo(final Version version) {
    return -1;
  }

  @Override
  public boolean includesVersion(final Version version) {
    return Boundary.Lower.super.includesVersion(version);
  }

  @Override
  public String toString() {
    return "(";
  }

  @Override
  public int hashCode() {
    return -1303378734;
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof UnlimitedLowerBoundary;
  }
}
