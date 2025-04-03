package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * A class coupling a {@link LocalDateTime} with a {@link BaseVersion}.
 * This can be usefull if the {@code builddate} of a {@link Version} is not
 * part of the {@code Version} (see {@link ConcreteSnapshotVersion}) or may
 * differ from it.
 */
public class DatedBaseVersion implements BaseVersion, Serializable {
  private static final long serialVersionUID = -3999130925615216416L;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n");

  private final BaseVersion version;
  private final LocalDateTime date;

  /**
   * Class Constructor specifiying the {@link BaseVersion} and a
   * {@link LocalDateTime} to associate it with.
   *
   * @param version the version to associate a date with
   * @param date the date to associate the version with
   */
  public DatedBaseVersion(final BaseVersion version, final LocalDateTime date) {
    this.version = Objects.requireNonNull(version);
    this.date = Objects.requireNonNull(date);
  }

  /**
   * Returns the {@link LocalDateTime} this instances {@link BaseVersion} is
   * associated with.
   *
   * @return the date of this {@code BaseVersion}
   */
  public LocalDateTime getDate() {
    return this.date;
  }

  /**
   * Returns this instances {@link BaseVersion} without date association.
   *
   * @return a {@code BaseVersion} without date
   */
  public BaseVersion asUndated() {
    return this.version;
  }

  @Override
  public int getMajor() {
    return this.version.getMajor();
  }

  @Override
  public int getMinor() {
    return this.version.getMinor();
  }

  @Override
  public int getIncremental() {
    return this.version.getIncremental();
  }

  @Override
  public Branch getBranch() {
    return this.version.getBranch();
  }

  @Override
  public List<String> getQualifiers() {
    return this.version.getQualifiers();
  }

  @Override
  public boolean isRelease() {
    return this.version.isRelease();
  }

  @Override
  public boolean isSnapshot() {
    return this.version.isSnapshot();
  }

  @Override
  public int compareTo(final Version other) {
    final int cmp = this.version.compareTo(other);
    return cmp == 0 && other instanceof DatedBaseVersion
        ? this.date.compareTo(((DatedBaseVersion) other).date)
        : cmp;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.version, this.date);
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof DatedBaseVersion)) {
      return false;
    }
    final DatedBaseVersion that = (DatedBaseVersion) other;
    return this.version.equals(that.version) && this.date.equals(that.date);
  }

  /**
   * Returns a String representation of this instance.
   * The {@link BaseVersion} is formated by
   * {@link VersionFormatter#DEFAULT_BASE_VERSION_FORMATTER}, which is
   * followed by the associated {@link LocalDateTime} enclosed in angle
   * brackets (<code>&lt;</code>, <code>&gt;</code>) in the format
   * <code>yyyy-MM-dd'T'HH:mm:ss.n</code>.
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    return this.version.toString()
        + "<"
        + DatedBaseVersion.DATE_TIME_FORMATTER.format(this.date)
        + ">";
  }
}
