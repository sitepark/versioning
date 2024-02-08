package com.sitepark.versioning.version;

/**
 * A rolling {@code snapshot} {@link Version}.
 * Conceptually this is based on how
 * <a href="https://maven.apache.org/">maven</a> defines {@code snapshots}.
 * They are considered as a "yet-unreleased" version of the associated
 * {@code major}, {@code minor} and {@code incremental}.  It supersets all
 * current and future {@link ConcreteSnapshotVersion}s with the same
 * {@link Version} fields.  When refering to a {@code SnapshotVersion} usually
 * its latest {@code ConcreteSnapshotVersion} is meant.
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
 *        .setConcreteSnapshotTimestamp("12345678.123456")
 *        .setConcreteSnapshotBuildnumber(42)
 *        .buildConcreteSnapshot();
 *
 *    assert(concreteSnapshot.compareTo(snapshot) == 0);
 *    assert(snapshot.compareTo(concreteSnapshot) == 0);
 * </pre>
 *
 * <p>
 * {@code SnapshotVersion}s are usually identified by their last
 * {@code qualifier} {@code "SNAPSHOT"}.  But technically that may also be
 * caused by/interpreted as a normal {@code qualifier} of a
 * {@link ReleaseVersion}.
 */
public class SnapshotVersion extends AbstractVersion implements BaseVersion {
  private static final long serialVersionUID = 5634960545752086851L;

  SnapshotVersion(final VersionBuilder builder) {
    super(builder);
  }

  SnapshotVersion(final Version other) {
    super(other);
  }

  /**
   * Returns wether this Version is considered a {@code snapshot}.
   * In the case of {@code SnapshotVersion}s this always returns {@code true}.
   *
   * @return {@code true}
   */
  @Override
  public boolean isSnapshot() {
    return true;
  }

  /**
   * Creates a new {@link ReleaseVersion} with the same {@link Version} fields
   * of this instance.
   * In other words this method creates a new Version without the
   * {@code "SNAPSHOT"} {@code qualifier}.
   *
   * @return a new {@code ReleaseVersion}
   */
  public ReleaseVersion toRelease() {
    return new ReleaseVersion(this);
  }

  @Override
  public boolean equals(final Object other) {
    return other instanceof SnapshotVersion && super.equals(other);
  }
}
