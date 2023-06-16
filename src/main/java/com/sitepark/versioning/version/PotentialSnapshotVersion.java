package com.sitepark.versioning.version;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container object that contains either a {@link ReleaseVersion} or a
 * {@link SnapshotVersion}.
 * This is intended to simplify handling of cases, where a {@link BaseVersion}
 * has to be differentiated into these two.
 *
 * <p>
 * For example a API that returns a {@link BaseVersion} has to manually do
 * {@code instanceof} checks:
 * <pre>
 *    final BaseVersion version = repository.getLatestBaseVersion());
 *    final ConcreteVersion latest;
 *    if (version instanceof ReleaseVersion) {
 *        latest = (ReleaseVersion)version;
 *    } else if (version instanceof SnapshotVersion) {
 *        latest = repository.getLatestConcreteVersion(
 *                (SnapshotVersion)version);
 *    } else {
 *        throw new IllegalStateException(
 *                "encountered an unknown BaseVersion class: "
 *                    + version.getClass().getName());
 *    }
 * </pre>
 *
 * And in contrast the same API returning a {@code PotentialSnapshotVersion}:
 * <pre>
 *     final ConcreteVersion latest = repository.getLatestBaseVersion()
 *         .mapEither(
 *             repository::getLatestConcreteVersion,
 *             Function.identity());
 * </pre>
 * @see PotentialConcreteSnapshotVersion
 */
public final class PotentialSnapshotVersion {
	private final SnapshotVersion snapshot;
	private final ReleaseVersion release;

	/**
	 * Returns an {@code PotentialSnapshotVersion} with the specified
	 * {@link SnapshotVersion}.
	 *
	 * @param version the {@code SnapshotVersion}
	 * @return a {@code PotentialSnapshotVersion} wrapping the version
	 * @see #ofRelease(ReleaseVersion)
	 * @see #ofVersion(BaseVersion)
	 */
	public static PotentialSnapshotVersion ofSnapshot(
			final SnapshotVersion version) {
		return new PotentialSnapshotVersion(version, null);
	}

	/**
	 * Returns an {@code PotentialSnapshotVersion} with the specified
	 * {@link ReleaseVersion}.
	 *
	 * @param version the {@code ReleaseVersion}
	 * @return a {@code PotentialSnapshotVersion} wrapping the version
	 * @see #ofSnapshot(SnapshotVersion)
	 * @see #ofVersion(BaseVersion)
	 */
	public static PotentialSnapshotVersion ofRelease(
			final ReleaseVersion version) {
		return new PotentialSnapshotVersion(null, version);
	}

	/**
	 * Returns an {@code PotentialSnapshotVersion} with the specified
	 * {@link BaseVersion}.
	 *
	 * @param version the {@code BaseVersion}
	 * @return a {@code PotentialSnapshotVersion} wrapping the version
	 * @throws IllegalArgumentException if a {@code BaseVersion} is supplied
	 *                                  that is neither a
	 *                                  {@link SnapshotVersion} nor a
	 *                                  {@link ReleaseVersion}
	 * @see #ofSnapshot(SnapshotVersion)
	 * @see #ofRelease(ReleaseVersion)
	 */
	public static PotentialSnapshotVersion ofVersion(
				final BaseVersion version) throws IllegalArgumentException {
		if (version instanceof SnapshotVersion) {
			return PotentialSnapshotVersion.ofSnapshot(
					(SnapshotVersion)version);
		}
		if (version instanceof ReleaseVersion) {
			return PotentialSnapshotVersion.ofRelease((ReleaseVersion)version);
		}
		throw new IllegalArgumentException(
				"required either a SnapshotVersion or a ReleaseVersion, got "
					+ version.getClass().getName());
	}

	private PotentialSnapshotVersion(
			final SnapshotVersion snapshot,
			final ReleaseVersion release) {
		if (!(snapshot == null ^ release == null)) {
			throw new IllegalArgumentException(
					"PotentialSnapshotVersion has to have exactly "
						+ "one SnapshotVersion or ReleaseVersion");
		}
		this.snapshot = snapshot;
		this.release = release;
	}

	/**
	 * Returns the contained {@link BaseVersion}.
	 *
	 * @return the value as {@code BaseVersion}
	 * @see #getSnapshot()
	 * @see #getRelease()
	 * @see #getSnapshotOrElse(SnapshotVersion)
	 * @see #getReleaseOrElse(ReleaseVersion)
	 * @see #getSnapshotOrElseGet(Supplier)
	 * @see #getReleaseOrElseGet(Supplier)
	 * @see #getSnapshotOrElseThrow(Supplier)
	 * @see #getReleaseOrElseThrow(Supplier)
	 */
	public BaseVersion get() {
		return this.snapshot != null ? this.snapshot : this.release;
	}

	/**
	 * Returns wether the contained {@link BaseVersion} is an instance of
	 * {@link SnapshotVersion}.
	 * This method is the opposite of {@link #isRelease}; If one returns
	 * {@code true} the other will always return {@code false}.
	 *
	 * @return {@code true} if the contained value is a {@code SnapshotVersion},
	 *         {@code false} otherwise
	 */
	public boolean isSnapshot() {
		return this.snapshot != null;
	}

	/**
	 * Returns wether the contained {@link BaseVersion} is an instance of
	 * {@link ReleaseVersion}.
	 * This method is the opposite of {@link #isSnapshot}; If one returns
	 * {@code true} the other will always return {@code false}.
	 *
	 * @return {@code true} if the contained value is a {@code ReleaseVersion},
	 *         {@code false} otherwise
	 */
	public boolean isRelease() {
		return this.release != null;
	}

	/**
	 * Invokes the specified {@link Consumer} with the value contained by this
	 * instance if it is a {@link SnapshotVersion}.
	 *
	 * @param action the action to potentially execute
	 * @see #isSnapshot()
	 * @see #ifIsRelease(Consumer)
	 * @see #ifIsSnapshotOrElse(Consumer, Consumer)
	 */
	public void ifIsSnapshot(final Consumer<? super SnapshotVersion> action) {
		if (this.isSnapshot()) {
			action.accept(this.snapshot);
		}
	}

	/**
	 * Invokes the specified {@link Consumer} with the value contained by this
	 * instance if it is a {@link ReleaseVersion}.
	 *
	 * @param action the action to potentially execute
	 * @see #isRelease()
	 * @see #ifIsSnapshot(Consumer)
	 * @see #ifIsSnapshotOrElse(Consumer, Consumer)
	 */
	public void ifIsRelease(final Consumer<? super ReleaseVersion> action) {
		if (this.isRelease()) {
			action.accept(this.release);
		}
	}

	/**
	 * Invokes one of the specified {@link Consumer} with the value contained
	 * by this instance depending on wether it is a {@link SnapshotVersion} or a
	 * {@link ReleaseVersion}.
	 *
	 * @param snapshotAction the action to execute if the value is a
	 *                       {@code SnapshotVersion}
	 * @param releaseAction the action to execute if the value is a
	 *                      {@code releaseVersion}
	 * @see #isSnapshot()
	 * @see #isRelease()
	 * @see #ifIsSnapshot(Consumer)
	 * @see #ifIsRelease(Consumer)
	 */
	public void ifIsSnapshotOrElse(
			final Consumer<? super SnapshotVersion> snapshotAction,
			final Consumer<? super ReleaseVersion> releaseAction) {
		if (this.isSnapshot()) {
			snapshotAction.accept(this.snapshot);
		} else {
			releaseAction.accept(this.release);
		}
	}

	/**
	 * Wrapps this instances value in a {@link Optional}.
	 *
	 * The {@code Optional} is empty if the value is not a
	 * {@link SnapshotVersion}.
	 *
	 * @return a {@code Optional} containing this instances
	 *         {@code SnapshotVersion} or an empty one if it is a
	 *         {@link ReleaseVersion}
	 * @see #isSnapshot()
	 * @see #getSnapshotOrElse(SnapshotVersion)
	 * @see #getSnapshotOrElseGet(Supplier)
	 * @see #getSnapshotOrElseThrow(Supplier)
	 * @see #getRelease()
	 */
	public Optional<SnapshotVersion> getSnapshot() {
		return Optional.ofNullable(this.snapshot);
	}

	/**
	 * Wrapps this instances value in a {@link Optional}.
	 * The {@code Optional} is empty if the value is not a
	 * {@link ReleaseVersion}.
	 *
	 * @return a {@code Optional} containing this instances
	 *         {@code ReleaseVersion} or an empty one if it is a
	 *         {@link SnapshotVersion}
	 * @see #isRelease()
	 * @see #getReleaseOrElse(ReleaseVersion)
	 * @see #getReleaseOrElseGet(Supplier)
	 * @see #getReleaseOrElseThrow(Supplier)
	 * @see #getSnapshot()
	 */
	public Optional<ReleaseVersion> getRelease() {
		return Optional.ofNullable(this.release);
	}

	/**
	 * Returns the contained value if it is a {@link SnapshotVersion} or the
	 * specified {@code other} if not.
	 *
	 * @param other the {@code SnapshotVersion} to return if the value is not a
	 *              snapshot
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code SnapshotVersion} or the specified {@code other} if not
	 * @see #isSnapshot()
	 * @see #getSnapshot()
	 * @see #getSnapshotOrElseGet(Supplier)
	 * @see #getSnapshotOrElseThrow(Supplier)
	 * @see #getReleaseOrElse(ReleaseVersion)
	 */
	public SnapshotVersion getSnapshotOrElse(final SnapshotVersion other) {
		return this.isSnapshot() ? this.snapshot : other;
	}

	/**
	 * Returns the contained value if it is a {@link ReleaseVersion} or the
	 * specified {@code other} if not.
	 *
	 * @param other the {@code SnapshotVersion} to return if the value is not a
	 *              release
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code ReleaseVersion} or the specified {@code other} if not
	 * @see #isRelease()
	 * @see #getRelease()
	 * @see #getReleaseOrElseGet(Supplier)
	 * @see #getReleaseOrElseThrow(Supplier)
	 * @see #getSnapshotOrElse(SnapshotVersion)
	 */
	public ReleaseVersion getReleaseOrElse(final ReleaseVersion other) {
		return this.isRelease() ? this.release : other;
	}

	/**
	 * Returns the contained value if it is a {@link SnapshotVersion} or
	 * executes the specified {@code supplier} and returns its result if not.
	 *
	 * @param supplier the {@code Supplier} to invoke if the value is not a
	 *                 snapshot
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code SnapshotVersion} or the result of the specified
	 *         {@code supplier} if not
	 * @see #isSnapshot()
	 * @see #getSnapshot()
	 * @see #getSnapshotOrElse(SnapshotVersion)
	 * @see #getSnapshotOrElseThrow(Supplier)
	 * @see #getReleaseOrElseGet(Supplier)
	 */
	public SnapshotVersion getSnapshotOrElseGet(
			final Supplier<? extends SnapshotVersion> supplier) {
		return this.isSnapshot() ? this.snapshot : supplier.get();
	}

	/**
	 * Returns the contained value if it is a {@link ReleaseVersion} or
	 * executes the specified {@code supplier} and returns its result if not.
	 *
	 * @param supplier the {@code Supplier} to invoke if the value is not a
	 *                 release
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code ReleaseVersion} or the result of the specified
	 *         {@code supplier} if not
	 * @see #isRelease()
	 * @see #getRelease()
	 * @see #getReleaseOrElse(ReleaseVersion)
	 * @see #getReleaseOrElseThrow(Supplier)
	 * @see #getSnapshotOrElseGet(Supplier)
	 */
	public ReleaseVersion getReleaseOrElseGet(
			final Supplier<? extends ReleaseVersion> supplier) {
		return this.isRelease() ? this.release : supplier.get();
	}

	/**
	 * Returns the contained value if it is a {@link SnapshotVersion} or throws
	 * an exception to be created by the provided {@code exceptionSupplier}.
	 *
	 * @param <X> type of the exception to potentially be thrown
	 * @param exceptionSupplier a {@code Supplier} to return a exception to
	 *                          throw
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code SnapshotVersion}
	 * @throws X if this instances {@code BaseVersion} is not a
	 *         {@code SnapshotVersion}
	 * @see #isSnapshot()
	 * @see #getSnapshot()
	 * @see #getSnapshotOrElse(SnapshotVersion)
	 * @see #getSnapshotOrElseGet(Supplier)
	 * @see #getReleaseOrElseThrow(Supplier)
	 */
	public <X extends Throwable> SnapshotVersion getSnapshotOrElseThrow(
			final Supplier<? extends X> exceptionSupplier) throws X {
		if (this.isSnapshot()) {
			return this.snapshot;
		}
		throw exceptionSupplier.get();
	}

	/**
	 * Returns the contained value if it is a {@link ReleaseVersion} or throws
	 * an exception to be created by the provided {@code exceptionSupplier}.
	 *
	 * @param <X> type of the exception to potentially be thrown
	 * @param exceptionSupplier a {@code Supplier} to return a exception to
	 *                          throw
	 * @see #isRelease()
	 * @return this instances {@link BaseVersion} if it is a
	 *         {@code ReleaseVersion}
	 * @throws X if this instances {@code BaseVersion} is not a
	 *         {@code ReleaseVersion}
	 * @see #getRelease()
	 * @see #getReleaseOrElse(ReleaseVersion)
	 * @see #getReleaseOrElseGet(Supplier)
	 * @see #getSnapshotOrElseThrow(Supplier)
	 */
	public <X extends Throwable> ReleaseVersion getReleaseOrElseThrow(
			final Supplier<? extends X> exceptionSupplier) throws X {
		if (this.isRelease()) {
			return this.release;
		}
		throw exceptionSupplier.get();
	}

	/**
	 * Applies one of the specified {@link Function}s to the contained value
	 * depending on wether it is a {@link SnapshotVersion} or a
	 * {@link ReleaseVersion}.
	 *
	 * @param <R> the type of the result of the mapping {@code Function}s
	 * @param snapshotMapper a mapping {@code Function} to apply if the value is
	 *                       a {@code SnapshotVersion}
	 * @param releaseMapper a mapping {@code Function} to apply if the value is
	 *                      a {@code ReleaseVersion}
	 * @return the result of the appropriate mapping {@code Function}
	 */
	public <R> R mapEither(
			final Function<? super SnapshotVersion, ? extends R> snapshotMapper,
			final Function<? super ReleaseVersion, ? extends R> releaseMapper) {
		return this.isSnapshot()
			? snapshotMapper.apply(this.snapshot)
			: releaseMapper.apply(this.release);
	}
}
