package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.util.List;

/**
 * A representation of a generic Version.
 * This includes the definition of the most basic fields (named after
 * <a href="https://semver.org/">semvers</a> and
 * <a href="https://maven.apache.org/">mavens</a> terminology):
 * <ul>
 *   <li>{@code major}</li>
 *   <li>{@code minor}</li>
 *   <li>{@code incremental}</li>
 *   <li>{@code branch}</li>
 *   <li>{@code qualifiers}</li>
 * </ul>
 * A instance may be described in the commonly used String format of the
 * {@link VersionParser} like so:<br>
 * {@code <major>.<minor>.<incremental>-<branch>-<qualifiers>}
 */
public sealed interface Version extends Comparable<Version>
    permits AbstractVersion, BaseVersion, ConcreteVersion {

  /**
   * Returns the {@code major} version.
   * May be zero ({@code 0}) but never negative.
   *
   * @return the major version
   */
  public int getMajor();

  /**
   * Returns the {@code minor} version.
   * May be zero ({@code 0}) but never negative.
   *
   * @return the minor version
   */
  public int getMinor();

  /**
   * Returns the {@code incremental} version.
   * May be zero ({@code 0}) but never negative.
   *
   * @return the incremental version
   */
  public int getIncremental();

  /**
   * Returns the {@link Branch} of this Version.
   * This is never {@code null}; The absence of a feature branch is denoted
   * by the {@link Branch#DEVELOP} instance.
   *
   * @return the branch
   */
  public Branch getBranch();

  /**
   * Returns all {@code qualifiers} of this Version.
   * This is never {@code null}; If the Version does not define any the
   * returned {@link List} is empty.
   *
   * @return the branch
   */
  public List<String> getQualifiers();

  /**
   * Returns wether this Version is considered a {@code snapshot}.
   * Depending on the implementing class this may be determined by varying
   * factors.
   *
   * <p>
   * A Version is strictly classifiable into either a {@code snapshot} or a
   * {@code release}.  Meaning that a instance that returns {@code false} is
   * always a {@code release}.
   *
   * @return {@code true} if this Version is considered a {@code snapshot},
   *         {@code false} otherwise
   * @see #isRelease()
   */
  public abstract boolean isSnapshot();

  /**
   * Returns wether this Version is considered a {@code release}.
   * Depending on the implementing class this may be determined by varying
   * factors.
   *
   * <p>
   * A Version is strictly classifiable into either a {@code snapshot} or a
   * {@code release}.  Meaning that a instance that returns {@code false} is
   * always a {@code snapshot}.
   *
   * @return {@code true} if this Version is considered a {@code release},
   *         {@code false} otherwise
   * @see #isSnapshot()
   */
  public abstract boolean isRelease();

  /**
   * Compares this Version to another Version.
   * The comparison is carried out in the following order of precedence until
   * a field is not considered equal:
   * <ol>
   *   <li>{@code major} - Higher major versions are considered greater.</li>
   *   <li>{@code minor} - Higher minor versions are considered greater.</li>
   *   <li>
   *     {@code incremental} - Higher incremental versions are considered
   *     greater.
   *   </li>
   *   <li>
   *     {@code snapshot status} - Release versions are considered greater
   *     than snapshot versions.
   *   </li>
   *   <li>
   *     {@code branch} - {@link Branch}es are compared as described by
   *     {@link  Branch#compareTo}.
   *   </li>
   *   <li>
   *     {@code qualifiers} - Qualifiers are compared lexicographically (in
   *     order).  A version with fewer qualifiers is considered
   *     greater.
   *   </li>
   * </ol>
   *
   * @param that the version to be compared
   * @return a negative {@code int}, zero ({@code 0}), or a positive
   *         {@code int} as this Version is less than, equal to, or greater
   *         than the specified Version.
   * @see ReleaseVersion#compareTo(Version)
   * @see SnapshotVersion#compareTo(Version)
   * @see ConcreteSnapshotVersion#compareTo(Version)
   */
  @Override
  public default int compareTo(final Version that) {
    return VersionComparator.NATUAL.compare(this, that);
  }
}
