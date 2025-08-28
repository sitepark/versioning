package com.sitepark.versioning.version;

/**
 * A unique {@code snapshot} {@link Version}.
 * Conceptually this is based on how
 * <a href="https://maven.apache.org/">maven</a> defines {@code snapshots}.
 * They are considered as a "yet-unreleased" version of the associated
 * {@code major}, {@code minor} and {@code incremental}.  In external APIs
 * these are commonly refered to by their {@link SnapshotVersion}, which
 * supersets all current and future {@code ConcreteSnapshotVersion}s with the
 * same {@link Version} fields.
 *
 * <p>
 * To check wether a {@code ConcreteSnapshotVersion} is also represented by a
 * {@code SnapshotVersion} one may use either {@code compareTo} method.
 * {@code equals} does not work since they are different classes.
 * <pre>
 *    final SnapshotVersion snapshot = new VersionBuilder()
 *        .setMajor(1)
 *        .buildSnapshot();
 *    final ConcreteSnapshotVersion concreteSnapshot = new VersionBuilder()
 *        .setMajor(1)
 *        .buildConcreteSnapshot("12345678.123456", 42);
 *
 *    assert(concreteSnapshot.compareTo(snapshot) == 0);
 *    assert(snapshot.compareTo(concreteSnapshot) == 0);
 * </pre>
 *
 * <p>
 * {@code ConcreteSnapshotVersion}s are usually identified by their second to
 * last {@code qualifier} beeing a {@code builddate} of the format
 * <code>YYYYMMDD.HHMMSS</code> and their last {@code qualifier} beeing a
 * {@code buildnumber}.  But technically these may also be
 * caused by/interpreted as normal {@code qualifiers} of a
 * {@link ReleaseVersion}.
 */
public final class ConcreteSnapshotVersion extends AbstractVersion implements ConcreteVersion {
  private static final long serialVersionUID = -8343574484894405153L;

  private final String timestamp;
  private final int buildnumber;

  ConcreteSnapshotVersion(
      final VersionBuilder builder, final String timestamp, final int buildnumber) {
    super(builder);
    this.timestamp = timestamp;
    this.buildnumber = buildnumber;
  }

  /**
   * Returns the {@code builddate} of this Version.
   * This is generally a String of the format <code>YYYYMMDD.HHMMSS</code>,
   * but could currently be any String.  This however is discouraged as the
   * type of this field may change to an actual date in a later version.
   *
   * @return the {@code builddate}
   */
  public String getTimestamp() {
    return this.timestamp;
  }

  /**
   * Returns the {@code buildnumber} of this Version.
   * May be zero ({@code 0}) but never negative.
   *
   * @return the {@code buildnumber}
   */
  public int getBuildnumber() {
    return this.buildnumber;
  }

  /**
   * Returns wether this Version is considered a {@code snapshot}.
   * In the case of {@code ConcreteSnapshotVersion}s this always returns
   * {@code true}.
   *
   * @return {@code true}
   */
  @Override
  public boolean isSnapshot() {
    return true;
  }

  /**
   * Returns wether this Version is considered a {@code release}.
   * In the case of {@code ConcreteSnapshotVersion}s this always returns
   * {@code false}.
   *
   * @return {@code false}
   */
  @Override
  public boolean isRelease() {
    return false;
  }

  @Override
  public BaseVersion asBaseVersion() {
    return new SnapshotVersion(this);
  }

  /**
   * Returns a {@link SnapshotVersion} that supersets this instance.
   *
   * @return a new {@code SnapshotVersion}
   * @see #asBaseVersion()
   */
  public SnapshotVersion toSnapshot() {
    return new SnapshotVersion(this);
  }

  /**
   * Creates a new {@link ReleaseVersion} with the same {@link Version} fields
   * of this instance.
   * In other words this method creates a new Version without the
   * {@code builddate} and {@code buildnumber} {@code qualifiers}.
   *
   * @return a new {@code ReleaseVersion}
   */
  public ReleaseVersion toRelase() {
    return new ReleaseVersion(this);
  }

  /**
   * Returns a String representation of this instance as formated by
   * {@link VersionFormatter#DEFAULT_CONCRETE_VERSION_FORMATTER}.
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    return VersionFormatter.DEFAULT_CONCRETE_VERSION_FORMATTER.format(this);
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof final ConcreteSnapshotVersion that
        && super.equals(that)
        && this.timestamp.equals(that.timestamp)
        && this.buildnumber == that.buildnumber;
  }
}
