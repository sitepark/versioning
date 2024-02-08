package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.ConcreteVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.VersionsSpecification;
import java.util.Optional;

/**
 * A {@link SpecificationElement} of a {@link VersionsSpecification}
 * representing a subset of {@link Version}s containing a single
 * {@link BaseVersion} (and all {@link ConcreteVersion}s that represent that
 * it).
 *
 * More formally this class depicts a subset {@code R} with the
 * {@code BaseVersion} {@code b} and the containing all {@code Version}s
 * {@code v} such that <code>R = {v | b = v}</code>.
 */
public final class ExplicitVersionElement extends SpecificationElement.VersionBased {
  private static final long serialVersionUID = 8443472070104712200L;

  private final BaseVersion version;

  /**
   * Class constructor specifiying the {@link BaseVersion} of this instance.
   *
   * @param version the {@code BaseVersion} of this instance
   */
  public ExplicitVersionElement(final BaseVersion version) {
    this.version = version;
  }

  @Override
  public Branch getBranch() {
    return this.version.getBranch();
  }

  /**
   * Returns wether a {@link Version} is contained in the subset represented
   * by this instance.
   *
   * More formally, returns {@code true} if a {@code Version} is specified,
   * such that
   * {@code explicitVersionElement.getVersion().compareTo(version) == 0}.
   *
   * @param version the {@code Version} to check
   * @return {@code true} if the {@code Version} is contained in this instance
   */
  @Override
  public boolean containsVersion(final Version version) {
    // compareTo to allow supplying ConcreteSnapshotVersions
    return this.version.compareTo(version) == 0;
  }

  @Override
  public Optional<SpecificationElement> getIntersectionWithVersionBased(
      final SpecificationElement.VersionBased other) {
    return this.version.equals(other.getVersion()) ? Optional.of(this) : Optional.empty();
  }

  @Override
  public ComparisonResult compareToVersionBased(final SpecificationElement.VersionBased other) {
    final int cmp = this.version.compareTo(other.getVersion());
    if (cmp > 0) {
      return ComparisonResult.HIGHER;
    }
    if (cmp < 0) {
      return ComparisonResult.LOWER;
    }
    return ComparisonResult.INTERSECTS_EQUALY;
  }

  @Override
  public ComparisonResult compareToBoundariesBased(
      final SpecificationElement.BoundariesBased other) {
    final int cmpLower = other.getBoundaries().getLower().compareTo(this.version);
    if (cmpLower > 0) {
      return ComparisonResult.LOWER;
    }
    final int cmpUpper = other.getBoundaries().getUpper().compareTo(this.version);
    if (cmpUpper < 0) {
      return ComparisonResult.HIGHER;
    }
    return cmpLower == 0 && cmpUpper == 0
        ? ComparisonResult.INTERSECTS_EQUALY
        : ComparisonResult.INTERSECTS_PARTIALLY;
  }

  @Override
  public Optional<SpecificationElement> getIntersectionWithBoundariesBased(
      final SpecificationElement.BoundariesBased other) {
    return other.getBoundaries().containsVersion(this.version)
        ? Optional.of(this)
        : Optional.empty();
  }

  @Override
  public BaseVersion getVersion() {
    return this.version;
  }

  @Override
  public String toString() {
    return this.version.toString();
  }

  @Override
  public int hashCode() {
    return 17 + this.version.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof ExplicitVersionElement)) {
      return false;
    }
    return this.version.equals(((ExplicitVersionElement) other).version);
  }
}
