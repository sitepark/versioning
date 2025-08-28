package com.sitepark.versioning.version;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container object that contains either a {@link ReleaseVersion} or a
 * {@link ConcreteSnapshotVersion}.
 * This is intended to simplify handling of cases, where a
 * {@link ConcreteVersion} has to be differentiated into these two.
 *
 * <p>
 * For example a API that returns a {@link ConcreteVersion} has to manually do
 * {@code instanceof} checks:
 * <code>
 *    final ConcreteVersion version = dependency.getVersion();
 *    if (version instanceof ConcreteSnapshotVersion) {
 *        throw new SnapshotDependenciesException(
 *                "cannot have snapshot dependencies; found " + dependency);
 *    }
 *    if (!(version instanceof ReleaseVersion)) {
 *        throw new IllegalStateException(
 *                "encountered an unknown ConcreteVersion class: "
 *                    + version.getClass().getName());
 *    }
 *    return (ReleaseVersion)version;
 * </code>
 *
 * And in contrast the same API returning a
 * {@code PotentialConcreteSnapshotVersion}:
 * <code>
 *     return dependency.getVersion()
 *         .getReleaseOrElseThrow(() -&gt; new SnapshotDependenciesException(
 *                "cannot have snapshot dependencies; found " + dependency));
 * </code>
 * @deprecated as of java 17 this class can be substituted with pattern-matching
 * @see PotentialSnapshotVersion
 */
@Deprecated(since = "3.0.0", forRemoval = true)
public final class PotentialConcreteSnapshotVersion {
  private final ConcreteVersion version;

  /**
   * Returns an {@code PotentialConcreteSnapshotVersion} with the specified
   * {@link ConcreteSnapshotVersion}.
   *
   * @param version the {@code ConcreteSnapshotVersion}
   * @return a {@code PotentialConcreteSnapshotVersion} wrapping the version
   * @see #ofRelease(ReleaseVersion)
   * @see #ofVersion(ConcreteVersion)
   */
  public static PotentialConcreteSnapshotVersion ofSnapshot(final ConcreteSnapshotVersion version) {
    return new PotentialConcreteSnapshotVersion(version);
  }

  /**
   * Returns an {@code PotentialConcreteSnapshotVersion} with the specified
   * {@link ReleaseVersion}.
   *
   * @param version the {@code ReleaseVersion}
   * @return a {@code PotentialConcreteSnapshotVersion} wrapping the version
   * @see #ofSnapshot(ConcreteSnapshotVersion)
   * @see #ofVersion(ConcreteVersion)
   */
  public static PotentialConcreteSnapshotVersion ofRelease(final ReleaseVersion version) {
    return new PotentialConcreteSnapshotVersion(version);
  }

  /**
   * Returns an {@code PotentialConcreteSnapshotVersion} with the specified
   * {@link ConcreteVersion}.
   *
   * @param version the {@code ConcreteVersion}
   * @return a {@code PotentialConcreteSnapshotVersion} wrapping the version
   * @throws IllegalArgumentException if a {@code ConcreteVersion} is supplied
   *                                  that is neither a
   *                                  {@link ConcreteSnapshotVersion} nor a
   *                                  {@link ReleaseVersion}
   * @see #ofSnapshot(ConcreteSnapshotVersion)
   * @see #ofRelease(ReleaseVersion)
   */
  public static PotentialConcreteSnapshotVersion ofVersion(final ConcreteVersion version) {
    return new PotentialConcreteSnapshotVersion(version);
  }

  private PotentialConcreteSnapshotVersion(final ConcreteVersion version) {
    this.version = Objects.requireNonNull(version);
  }

  /**
   * Returns the contained {@link ConcreteVersion}.
   *
   * @return the value as {@code ConcreteVersion}
   * @see #getSnapshot()
   * @see #getRelease()
   * @see #getSnapshotOrElse(ConcreteSnapshotVersion)
   * @see #getReleaseOrElse(ReleaseVersion)
   * @see #getSnapshotOrElseGet(Supplier)
   * @see #getReleaseOrElseGet(Supplier)
   * @see #getSnapshotOrElseThrow(Supplier)
   * @see #getReleaseOrElseThrow(Supplier)
   */
  public ConcreteVersion get() {
    return this.version;
  }

  /**
   * Returns wether the contained {@link ConcreteVersion} is an instance of
   * {@link ConcreteSnapshotVersion}.
   * This method is the opposite of {@link #isRelease}; If one returns
   * {@code true} the other will always return {@code false}.
   *
   * @return {@code true} if the contained value is a
   *         {@code ConcreteSnapshotVersion}, {@code false} otherwise
   */
  public boolean isSnapshot() {
    return this.version instanceof ConcreteSnapshotVersion;
  }

  /**
   * Returns wether the contained {@link ConcreteVersion} is an instance of
   * {@link ReleaseVersion}.
   * This method is the opposite of {@link #isSnapshot}; If one returns
   * {@code true} the other will always return {@code false}.
   *
   * @return {@code true} if the contained value is a {@code ReleaseVersion},
   *         {@code false} otherwise
   */
  public boolean isRelease() {
    return this.version instanceof ReleaseVersion;
  }

  /**
   * Invokes the specified {@link Consumer} with the value contained by this
   * instance if it is a {@link ConcreteSnapshotVersion}.
   *
   * @param action the action to potentially execute
   * @see #isSnapshot()
   * @see #ifIsRelease(Consumer)
   * @see #ifIsSnapshotOrElse(Consumer, Consumer)
   */
  public void ifIsSnapshot(final Consumer<? super ConcreteSnapshotVersion> action) {
    if (this.version instanceof final ConcreteSnapshotVersion snapshot) {
      action.accept(snapshot);
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
    if (this.version instanceof final ReleaseVersion release) {
      action.accept(release);
    }
  }

  /**
   * Invokes one of the specified {@link Consumer} with the value contained
   * by this instance depending on wether it is a
   * {@link ConcreteSnapshotVersion} or a {@link ReleaseVersion}.
   *
   * @param snapshotAction the action to execute if the value is a
   *                       {@code ConcreteSnapshotVersion}
   * @param releaseAction the action to execute if the value is a
   *                      {@code releaseVersion}
   * @see #isSnapshot()
   * @see #isRelease()
   * @see #ifIsSnapshot(Consumer)
   * @see #ifIsRelease(Consumer)
   */
  public void ifIsSnapshotOrElse(
      final Consumer<? super ConcreteSnapshotVersion> snapshotAction,
      final Consumer<? super ReleaseVersion> releaseAction) {
    switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> snapshotAction.accept(snapshot);
      case ReleaseVersion release -> releaseAction.accept(release);
    }
  }

  /**
   * Wrapps this instances value in a {@link Optional}.
   * The {@code Optional} is empty if the value is not a
   * {@link ConcreteSnapshotVersion}.
   *
   * @return a {@code Optional} containing this instances
   *         {@code ConcreteSnapshotVersion} or an empty one if it is a
   *         {@link ReleaseVersion}
   * @see #isSnapshot()
   * @see #getSnapshotOrElse(ConcreteSnapshotVersion)
   * @see #getSnapshotOrElseGet(Supplier)
   * @see #getSnapshotOrElseThrow(Supplier)
   * @see #getRelease()
   */
  public Optional<ConcreteSnapshotVersion> getSnapshot() {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> Optional.of(snapshot);
      case ReleaseVersion release -> Optional.empty();
    };
  }

  /**
   * Wrapps this instances value in a {@link Optional}.
   * The {@code Optional} is empty if the value is not a
   * {@link ReleaseVersion}.
   *
   * @return a {@code Optional} containing this instances
   *         {@code ReleaseVersion} or an empty one if it is a
   *         {@link ConcreteSnapshotVersion}
   * @see #isRelease()
   * @see #getReleaseOrElse(ReleaseVersion)
   * @see #getReleaseOrElseGet(Supplier)
   * @see #getReleaseOrElseThrow(Supplier)
   * @see #getSnapshot()
   */
  public Optional<ReleaseVersion> getRelease() {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> Optional.empty();
      case ReleaseVersion release -> Optional.of(release);
    };
  }

  /**
   * Returns the contained value if it is a {@link ConcreteSnapshotVersion}
   * or the specified {@code other} if not.
   *
   * @param other the {@code ConcreteSnapshotVersion} to return if the value
   *              is not a snapshot
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code ReleaseVersion} or the specified {@code other} if not
   * @see #isSnapshot()
   * @see #getSnapshot()
   * @see #getSnapshotOrElseGet(Supplier)
   * @see #getSnapshotOrElseThrow(Supplier)
   * @see #getReleaseOrElse(ReleaseVersion)
   */
  public ConcreteSnapshotVersion getSnapshotOrElse(final ConcreteSnapshotVersion other) {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> snapshot;
      case ReleaseVersion release -> other;
    };
  }

  /**
   * Returns the contained value if it is a {@link ReleaseVersion} or the
   * specified {@code other} if not.
   *
   * @param other the {@code ConcreteSnapshotVersion} to return if the value
   *              is not a release
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code ConcreteSnapshotVersion} or the specified {@code other}
   *         if not
   * @see #isRelease()
   * @see #getRelease()
   * @see #getReleaseOrElseGet(Supplier)
   * @see #getReleaseOrElseThrow(Supplier)
   * @see #getSnapshotOrElse(ConcreteSnapshotVersion)
   */
  public ReleaseVersion getReleaseOrElse(final ReleaseVersion other) {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> other;
      case ReleaseVersion release -> release;
    };
  }

  /**
   * Returns the contained value if it is a {@link ConcreteSnapshotVersion} or
   * executes the specified {@code supplier} and returns its result if not.
   *
   * @param supplier the {@code Supplier} to invoke if the value is not a
   *                 snapshot
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code ConcreteSnapshotVersion} or the result of the specified
   *         {@code supplier} if not
   * @see #isSnapshot()
   * @see #getSnapshot()
   * @see #getSnapshotOrElse(ConcreteSnapshotVersion)
   * @see #getSnapshotOrElseThrow(Supplier)
   * @see #getReleaseOrElseGet(Supplier)
   */
  public ConcreteSnapshotVersion getSnapshotOrElseGet(
      final Supplier<? extends ConcreteSnapshotVersion> supplier) {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> snapshot;
      case ReleaseVersion release -> supplier.get();
    };
  }

  /**
   * Returns the contained value if it is a {@link ReleaseVersion} or
   * executes the specified {@code supplier} and returns its result if not.
   *
   * @param supplier the {@code Supplier} to invoke if the value is not a
   *                 release
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code ReleaseVersion} or the result of the specified
   *         {@code supplier} if not
   * @see #isRelease()
   * @see #getRelease()
   * @see #getReleaseOrElse(ReleaseVersion)
   * @see #getReleaseOrElseThrow(Supplier)
   * @see #getSnapshotOrElseGet(Supplier)
   */
  public ReleaseVersion getReleaseOrElseGet(final Supplier<? extends ReleaseVersion> supplier) {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> supplier.get();
      case ReleaseVersion release -> release;
    };
  }

  /**
   * Returns the contained value if it is a {@link ConcreteSnapshotVersion}
   * or throws an exception to be created by the provided
   * {@code exceptionSupplier}.
   *
   * @param <X> type of the exception to potentially be thrown
   * @param exceptionSupplier a {@code Supplier} to return a exception to
   *                          throw
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code SnapshotVersion}
   * @throws X if this instances {@code ConcreteVersion} is not a
   *         {@code SnapshotVersion}
   * @see #isSnapshot()
   * @see #getSnapshot()
   * @see #getSnapshotOrElse(ConcreteSnapshotVersion)
   * @see #getSnapshotOrElseGet(Supplier)
   * @see #getReleaseOrElseThrow(Supplier)
   */
  public <X extends Throwable> ConcreteSnapshotVersion getSnapshotOrElseThrow(
      final Supplier<? extends X> exceptionSupplier) throws X {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> snapshot;
      case ReleaseVersion release -> throw exceptionSupplier.get();
    };
  }

  /**
   * Returns the contained value if it is a {@link ReleaseVersion} or throws
   * an exception to be created by the provided {@code exceptionSupplier}.
   *
   * @param <X> type of the exception to potentially be thrown
   * @param exceptionSupplier a {@code Supplier} to return a exception to
   *                          throw
   * @return this instances {@link ConcreteVersion} if it is a
   *         {@code ReleaseVersion}
   * @throws X if this instances {@code ConcreteVersion} is not a
   *         {@code ReleaseVersion}
   * @see #isRelease()
   * @see #getRelease()
   * @see #getReleaseOrElse(ReleaseVersion)
   * @see #getReleaseOrElseGet(Supplier)
   * @see #getSnapshotOrElseThrow(Supplier)
   */
  public <X extends Throwable> ReleaseVersion getReleaseOrElseThrow(
      final Supplier<? extends X> exceptionSupplier) throws X {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> throw exceptionSupplier.get();
      case ReleaseVersion release -> release;
    };
  }

  /**
   * Applies one of the specified {@link Function}s to the contained value
   * depending on wether it is a {@link ConcreteSnapshotVersion} or a
   * {@link ReleaseVersion}.
   *
   * @param <R> the type of the result of the mapping {@code Function}s
   * @param snapshotMapper a mapping {@code Function} to apply if the value is
   *                       a {@code ConcreteSnapshotVersion}
   * @param releaseMapper a mapping {@code Function} to apply if the value is
   *                      a {@code ReleaseVersion}
   * @return the result of the appropriate mapping {@code Function}
   */
  public <R> R mapEither(
      final Function<? super ConcreteSnapshotVersion, ? extends R> snapshotMapper,
      final Function<? super ReleaseVersion, ? extends R> releaseMapper) {
    return switch (this.version) {
      case ConcreteSnapshotVersion snapshot -> snapshotMapper.apply(snapshot);
      case ReleaseVersion release -> releaseMapper.apply(release);
    };
  }
}
