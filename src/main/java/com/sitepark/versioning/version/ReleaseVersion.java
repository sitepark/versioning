package com.sitepark.versioning.version;

/**
 * A unique, released {@link Version}.
 * In contrast to {@link SnapshotVersion}s this class referes to exactly one
 * {@code Version}.
 */
public class ReleaseVersion extends AbstractVersion
		implements BaseVersion, ConcreteVersion {
	private static final long serialVersionUID = -1839392003320400343L;

	ReleaseVersion(final VersionBuilder builder) {
		super(builder);
	}

	ReleaseVersion(final Version other) {
		super(other);
	}

	/**
	 * Returns wether this Version is considered a {@code snapshot}.
	 * In the case of {@code ReleaseVersion}s this always returns {@code false}.
	 *
	 * @return {@code false}
	 */
	@Override
	public boolean isSnapshot() {
		return false;
	}

	/**
	 * Returns a {@link BaseVersion} that supersets this
	 * {@link ConcreteVersion}.
	 * In the case of {@code ReleaseVersion}s this always returns itself.
	 *
	 * @return this instance
	 */
	@Override
	public BaseVersion asBaseVersion() {
		return this;
	}

	/**
	 * Creates a new {@link SnapshotVersion} with the same {@link Version}
	 * fields of this instance.
	 * In other words this method creates a new Version and appends the
	 * {@code "SNAPSHOT"} {@code qualifier}.
	 *
	 * @return a new {@code SnapshotVersion}
	 */
	public SnapshotVersion toSnapshot() {
		return new SnapshotVersion(this);
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof ReleaseVersion
			&& super.equals(other);
	}
}
