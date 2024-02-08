package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;

/**
 * A {@link Boundary.Upper} for {@link Boundaries} instances.
 *
 * This {@link Boundary} includes all {@link Version}s, that are equal to or
 * greater than the {@link BaseVersion} of this instance.
 * More formally, this {@code Boundary} {@code B} with the {@code BaseVersion}
 * <code>v<sub>B</sub></code> contains all {@code Version}s {@code v} such that
 * <code>B = {v | v ≥ v<sub>B</sub>}</code>.
 */
public final class InclusiveUpperBoundary extends Boundary.WithVersion implements Boundary.Upper {
  private static final long serialVersionUID = 869243337344077790L;

  /**
   * Class Constructor specifying the {@link BaseVersion} to compare other
   * {@link Version}s to when determining wether or not it is included by
   * this instance.
   *
   * @param version the {@code BaseVersion} for this instance
   */
  public InclusiveUpperBoundary(final BaseVersion version) {
    super(version);
  }

  @Override
  public int compareTo(final Version version) {
    return this.version.compareTo(version);
  }

  @Override
  public boolean includesVersion(final Version version) {
    return Boundary.Upper.super.includesVersion(version);
  }

  @Override
  public String toString() {
    return this.version.toString() + "]";
  }

  @Override
  public int hashCode() {
    return 53 + this.version.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof InclusiveUpperBoundary)) {
      return false;
    }
    return this.version.equals(((InclusiveUpperBoundary) other).version);
  }
}
