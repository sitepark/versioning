package com.sitepark.versioning.version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A class coupling a {@link LocalDateTime} with a {@link BaseVersion}.
 * This can be usefull if the {@code builddate} of a {@link Version} is not
 * part of the {@code Version} (see {@link ConcreteSnapshotVersion}) or may
 * differ from it.
 *
 * @param version the version to associate a date with
 * @param date the date to associate the version with
 */
public record DatedBaseVersion(BaseVersion version, LocalDateTime date)
    implements Comparable<DatedBaseVersion>, Serializable {

  private static final long serialVersionUID = -3999130925615216416L;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n");

  @Override
  public int compareTo(final DatedBaseVersion other) {
    final int cmp = this.version.compareTo(other.version);
    return cmp != 0 ? cmp : this.date.compareTo(other.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.version, this.date);
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final DatedBaseVersion that
        && this.version.equals(that.version)
        && this.date.equals(that.date);
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
