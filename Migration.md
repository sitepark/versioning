# `2.0.0` => `3.0.0`

## Java Version changed from 11 to 21

## removed `PotentialSnapshotVersion` and `PotentialConcreteSnapshotVersion`

With the introduction of pattern-matching in Java 17 these classes have become
obsolete. All of their methods may be replaced by `switch`- or
`instanceof`-statements:

```java
final PotentialSnapshotVersion version = parser.parsePotentialSnapshot("1.2.3-SNAPSHOT");

final ReleaseVersion release = version.mapEither(
        Snapshot::toRelease,
        Function.identity());

final SnapshotVersion snapshot = version.getSnapshotOrElseThrow(
        () -> new IllegalArgumentException("expected a snapshot!"));
```

```java
final BaseVersion version = parser.parseBaseVersion("1.2.3-SNAPSHOT");

final ReleaseVersion release = switch (version) {
    case SnapshotVersion snapshot -> snapshot.toRelease();
    case ReleaseVersion release -> release;
};

if (!(version instanceof final SnapshotVersion snapshot)) {
    throw new IllegalArgumentException("expected a snapshot!"));
}
```

## removed `VersionParser` methods for `Potential*Version` classes

The `VersionParser` transforms `String`s into either `BaseVersion`s or
`ConcreteVersion`s. Previously those were wrapped by `PotentialSnapshotVersion`
and `PotentialConcreteSnapshotVersion` instances in order to allow to
differentiate the implementations of these interfaces without awkward
`instanceof` checks.


```java
final BaseVersion base = parser.parsePotentialSnapshot("1.2.3-SNAPSHOT")
    .get();
final ConcreteVersion concrete = parser.parsePotentialConcreteSnapshot("1.2.3")
    .get();
```

These wrapper class [have been removed](#removed_VersionParser_methods_for_Potential_Version_classes)
and with them the `parsePotentialSnapshot` and `parsePotentialConcreteSnapshot`
methods have been changed to return the `BaseVersion`/`ConcreteVersion`
directly and in the process been renamed to `parseBaseVersion` and
`parseConcreteVersion`:

```java
final BaseVersion base = parser.parseBaseVersion("1.2.3-SNAPSHOT");
final ConcreteVersion concrete = parser.parseConcreteVersion("1.2.3");
```

## `VersionsSpecificationBuilder` methods `addVersionRangeElement` and `addExplicitVersion` signature changed

Previously instances of `VersionRangeElement` and `ExplicitVersionElement` had
to be created:

```java
final Version version = new VersionsSpecificationBuilder()
    .addVersionRangeElement(
            new VersionRangeElement(new Boundaries<>(lower, upper)))
    .addExplicitVersion(new ExplicitVersionElement(explicit))
    .build();
```

Now the methods just take the same arguments as the constructor of the
respective `SpecificationElement` (the class `Boundaries` is also superfluous
now):

```java
final Version version = new VersionsSpecificationBuilder()
    .addVersionRangeElement(lower, upper)
    .addExplicitVersion(explicit)
    .build();
```

## `DatedBaseVersion` no longer implements `BaseVersion`

This class is merely a container for a `BaseVersion` and a date. It
implementing `BaseVersion` was complicating the usage of that interface.

## changed `ConcreteSnapshotVersion` creation via `VersionBuilder`

A `ConcreteSnapshotVersion` requires the additional fields `timestamp` and
`buildnumber`. Previously these were supplied to the `VersionBuilder` via
respective setters:

```java
final ConcreteSnapshotVersion version = new VersionBuilder()
    .setMajor(1)
    .setMinor(2)
    .setIncremental(3)
    .setConcreteSnapshotTimestamp("12345678.123456")
    .setConcreteSnapshotBuildnumber(42)
    .buildConcreteSnapshot();
```

This has been improved:

```java
final ConcreteSnapshotVersion version = new VersionBuilder()
    .setMajor(1)
    .setMinor(2)
    .setIncremental(3)
    .buildConcreteSnapshot("12345678.123456", 42);
```

## `Version`, `BaseVersion` and `ConcreteVersion` interfaces may no longer be extended

These interfaces are now `sealed` and can thus no longer be implemented by
other classes.

