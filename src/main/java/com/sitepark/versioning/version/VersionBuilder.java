package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A builder class to instantiate {@link ReleaseVersion}s,
 * {@link SnapshotVersion}s and {@link ConcreteSnapshotVersion}s.
 * For {@code ConcreteSnapshotVersion}s both fields
 * {@link #setConcreteSnapshotTimestamp(String)} and
 * {@link #setConcreteSnapshotBuildnumber(int)} are required and will cause a
 * {@link IllegalArgumentException} if not specified.
 * All other fields have default values.
 *
 * Usage:
 * <pre>new VersionBuilder()
 *    .setMinor(1)
 *    .buildRelease()</pre>
 *
 * <pre>new VersionBuilder()
 *    .setMajor(3)
 *    .setIncremental(1)
 *    .addQualifier("hotfix")
 *    .buildSnapshot();</pre>
 *
 * <pre>new VersionBuilder()
 *    .setMajor(1)
 *    .setBranch(new Branch("new_core_engine"))
 *    .setConcreteSnapshotTimestamp(
 *            DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss")
 *                .format(LocalDate.now()))
 *    .setConcreteSnapshotBuildnumber(this.buildnumber++)
 *    .buildConcreteSnapshot();</pre>
 */
public class VersionBuilder {
  private AtomicInteger major;
  private AtomicInteger minor;
  private AtomicInteger incremental;
  private Branch branch;
  private final List<String> qualifiers;

  // TODO: should this be a Date?
  private Optional<String> concreteSnapshotTimestamp;
  private Optional<Integer> concreteSnapshotBuildnumber;

  /**
   * Class Constructor
   */
  public VersionBuilder() {
    this.major = new AtomicInteger(0);
    this.minor = new AtomicInteger(0);
    this.incremental = new AtomicInteger(0);
    this.branch = Branch.DEVELOP;
    this.qualifiers = Collections.synchronizedList(new LinkedList<>());
    this.concreteSnapshotTimestamp = Optional.empty();
    this.concreteSnapshotBuildnumber = Optional.empty();
  }

  /**
   * Specifies a {@code major} to set on {@link Version}s created by this
   * instance.
   * Otherwise defaults to zero ({@code 0}).
   *
   * @param major the major version
   * @return this instance
   * @see Version#getMajor()
   */
  public VersionBuilder setMajor(final int major) {
    this.major.set(major);
    return this;
  }

  /**
   * Returns the currently set {@code major}.
   * Defaults to zero ({@code 0}).
   *
   * @return the major version
   * @see Version#getMajor()
   */
  public int getMajor() {
    return this.major.get();
  }

  /**
   * Specifies a {@code minor} to set on {@link Version}s created by this
   * instance.
   * Otherwise defaults to zero ({@code 0}).
   *
   * @param minor the minor version
   * @return this instance
   * @see Version#getMinor()
   */
  public VersionBuilder setMinor(final int minor) {
    this.minor.set(minor);
    return this;
  }

  /**
   * Returns the currently set {@code minor}.
   * Defaults to zero ({@code 0}).
   *
   * @return the minor version
   * @see Version#getMinor()
   */
  public int getMinor() {
    return this.minor.get();
  }

  /**
   * Specifies a {@code incremental} to set on {@link Version}s created by
   * this instance.
   * Otherwise defaults to zero ({@code 0}).
   *
   * @param incremental the incremental version
   * @return this instance
   * @see Version#getIncremental()
   */
  public VersionBuilder setIncremental(final int incremental) {
    this.incremental.set(incremental);
    return this;
  }

  /**
   * Returns the currently set {@code incremental}.
   * Defaults to zero ({@code 0}).
   *
   * @return the incremental version
   * @see Version#getIncremental()
   */
  public int getIncremental() {
    return this.incremental.get();
  }

  /**
   * Specifies a {@link Branch} to set on {@link Version}s created by this
   * instance.
   * Otherwise defaults to {@link Branch#DEVELOP}.
   *
   * @param branch the branch to set
   * @return this instance
   * @see Version#getBranch()
   */
  public VersionBuilder setBranch(final Branch branch) {
    this.branch = Objects.requireNonNull(branch);
    return this;
  }

  /**
   * Returns the currently set {@link Branch}.
   * Defaults to zero ({@code 0}).
   *
   * @return the incremental version
   * @see Version#getIncremental()
   */
  public Branch getBranch() {
    return this.branch;
  }

  /**
   * Specifies and overwrites all {@code qualifiers} to set on
   * {@link Version}s created by this instance.
   *
   * @param qualifiers a {@code List} of qualifiers to set
   * @return this instance
   * @throws NullPointerException if {@code qualifiers} contains {@code null}
   * @see #addQualifier(String)
   * @see Version#getQualifiers()
   */
  public VersionBuilder setQualifiers(final List<String> qualifiers) {
    this.qualifiers.clear();
    for (final String qualifier : qualifiers) {
      this.qualifiers.add(Objects.requireNonNull(qualifier));
    }
    return this;
  }

  /**
   * Appends a {@code qualifier} to the {@code qualifiers} to set on
   * {@link Version}s created by this instance.
   *
   * @param qualifier the qualifier to append
   * @return this instance
   * @throws NullPointerException if {@code qualifier} is {@code null}
   * @see #setQualifiers(List)
   * @see Version#getQualifiers()
   */
  public VersionBuilder addQualifier(final String qualifier) {
    this.qualifiers.add(Objects.requireNonNull(qualifier));
    return this;
  }

  /**
   * Returns a {@code List} of all currently set {@code qualifiers}.
   *
   * @return all {@code qualifiers}
   * @see #setQualifiers(List)
   * @see #addQualifier(String)
   * @see Version#getQualifiers()
   */
  public List<String> getQualifiers() {
    return Collections.unmodifiableList(this.qualifiers);
  }

  /**
   * Specifies a {@code timestamp} to set on {@link ConcreteSnapshotVersion}s
   * created by this instance.
   * This value should follow the format {@code "yyyyMMdd.HHmmss"}.
   *
   * @param timestamp the timestamp to set
   * @return this instance
   * @see ConcreteSnapshotVersion#getTimestamp()
   */
  public VersionBuilder setConcreteSnapshotTimestamp(final String timestamp) {
    this.concreteSnapshotTimestamp = Optional.of(timestamp);
    return this;
  }

  /**
   * Returns the currently set {@code timestamp} for
   * {@link ConcreteSnapshotVersion}s.
   * This value should follow the format {@code "yyyyMMdd.HHmmss"}.
   *
   * @return the timestamp or an empty {@link Optional} if absent
   * @see ConcreteSnapshotVersion#getBuildnumber()
   */
  public Optional<String> getConcreteSnapshotTimestamp() {
    return this.concreteSnapshotTimestamp;
  }

  /**
   * Specifies a {@code buildnumber} to set on
   * {@link ConcreteSnapshotVersion}s created by this instance.
   *
   * @param buildnumber the buildnumber to set
   * @return this instance
   * @see ConcreteSnapshotVersion#getBuildnumber()
   */
  public VersionBuilder setConcreteSnapshotBuildnumber(final int buildnumber) {
    this.concreteSnapshotBuildnumber = Optional.of(buildnumber);
    return this;
  }

  /**
   * Returns the currently set {@code buildnumber} for
   * {@link ConcreteSnapshotVersion}s.
   *
   * @return the buildnumber or an empty {@link Optional} if absent
   * @see ConcreteSnapshotVersion#getBuildnumber()
   */
  public Optional<Integer> getConcreteSnapshotBuildnumber() {
    return this.concreteSnapshotBuildnumber;
  }

  /**
   * Builds a {@link ReleaseVersion} with all set fields.
   * For Fields that were not explicitly specified their default values are
   * applied.
   *
   * @return a new ReleaseVersion
   */
  public ReleaseVersion buildRelease() {
    final ReleaseVersion value = new ReleaseVersion(this);
    return value;
  }

  /**
   * Builds a {@link SnapshotVersion} with all set fields.
   * For Fields that were not explicitly specified their default values are
   * applied.
   *
   * @return a new SnapshotVersion
   */
  public SnapshotVersion buildSnapshot() {
    final SnapshotVersion value = new SnapshotVersion(this);
    return value;
  }

  /**
   * Builds a {@link ConcreteSnapshotVersion} with all set fields.
   * This requires the {@code timestamp} and {@code buildnumber} fields to be
   * specified.  Other Fields that were not explicitly specified have their
   * default values applied.
   *
   * @return a new ConcreteSnapshotVersion
   * @throws IllegalArgumentException if either {@code timestamp} or
   *                                  {@code buildnumber} are not set
   * @see #setConcreteSnapshotTimestamp(String)
   * @see #setConcreteSnapshotBuildnumber(int)
   */
  public ConcreteSnapshotVersion buildConcreteSnapshot() throws IllegalArgumentException {
    if (this.concreteSnapshotTimestamp.isEmpty()) {
      throw new IllegalArgumentException("cannot build concrete snapshots without a buildnumber");
    }
    if (this.concreteSnapshotBuildnumber.isEmpty()) {
      throw new IllegalArgumentException("cannot build concrete snapshots without a timestamp");
    }
    return new ConcreteSnapshotVersion(this);
  }
}
