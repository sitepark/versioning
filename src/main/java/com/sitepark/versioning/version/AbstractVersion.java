package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A class containing the common fields of {@link ReleaseVersion},
 * {@link SnapshotVersion} and {@link ConcreteSnapshotVersion}.
 */
abstract sealed class AbstractVersion implements Version, Serializable
    permits ReleaseVersion, SnapshotVersion, ConcreteSnapshotVersion {
  private static final long serialVersionUID = 762173304150679702L;

  private final int major;
  private final int minor;
  private final int incremental;
  private final Branch branch;
  private final List<String> qualifiers;

  AbstractVersion(final Version other) {
    this.major = other.getMajor();
    this.minor = other.getMinor();
    this.incremental = other.getIncremental();
    this.branch = other.getBranch();
    this.qualifiers = other.getQualifiers();
  }

  AbstractVersion(final VersionBuilder builder) {
    this.major = builder.getMajor();
    this.minor = builder.getMinor();
    this.incremental = builder.getIncremental();
    this.branch = builder.getBranch();
    this.qualifiers = List.copyOf(builder.getQualifiers());
  }

  @Override
  public int getMajor() {
    return this.major;
  }

  @Override
  public int getMinor() {
    return this.minor;
  }

  @Override
  public int getIncremental() {
    return this.incremental;
  }

  @Override
  public Branch getBranch() {
    return this.branch;
  }

  @Override
  public List<String> getQualifiers() {
    return this.qualifiers;
  }

  /**
   * Returns a String representation of this instance as formated by
   * {@link VersionFormatter#DEFAULT_BASE_VERSION_FORMATTER}.
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    return VersionFormatter.DEFAULT_BASE_VERSION_FORMATTER.format(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.getMajor(),
        this.getMinor(),
        this.getIncremental(),
        this.getBranch(),
        this.getQualifiers());
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final AbstractVersion that
        && this.getMajor() == that.getMajor()
        && this.getMinor() == that.getMinor()
        && this.getIncremental() == that.getIncremental()
        && this.getBranch().equals(that.getBranch())
        && this.getQualifiers().size() == that.getQualifiers().size()
        && this.getQualifiers().containsAll(that.getQualifiers());
  }
}
