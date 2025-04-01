package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import java.io.Serializable;

/**
 * A class to represent a {@code Boundary} of a {@link Boundaries} instance and
 * by extension of a {@link VersionRangeElement}.
 *
 * This also defines the {@link Lower} and {@link Upper} interfaces for the
 * respective ends of {@code Boundaries} and a {@link WithVersion} class for
 * {@code Boundaries} that base on a {@link BaseVersion}.
 *
 * <p>
 * <strong>This class is not meant to be extended!</strong><br>
 * All {@code Boundary} sub classes have to be comparable with each other, which
 * can only be guaranteed if all of these are known beforehand.
 *
 * <p>
 * The following sub classes exist and should suffice:
 * <ul>
 *   <li>{@link UnlimitedUpperBoundary}</li>
 *   <li>{@link ExclusiveUpperBoundary}</li>
 *   <li>{@link InclusiveUpperBoundary}</li>
 *   <li>{@link UnlimitedLowerBoundary}</li>
 *   <li>{@link ExclusiveLowerBoundary}</li>
 *   <li>{@link InclusiveLowerBoundary}</li>
 * </ul>
 */
public abstract class Boundary implements Comparable<Boundary>, Serializable {
  private static final long serialVersionUID = 7154662301619537154L;

  /**
   * Class Constructor.
   */
  Boundary() {}

  /**
   * Lower {@link Boundary}s of {@link Boundaries} instances.
   */
  @SuppressWarnings("PMD.ImplicitFunctionalInterface")
  public interface Lower {

    /**
     * Compares this instance with the specified {@link Version} for order.
     *
     * Returns a negative {@code int}, zero ({@code 0}) or a positive
     * {@code int} as this {@code Boundary} is smaller, equal to or greater
     * than the {@code Version} compared to.
     *
     * @param version the {@code Version} to check compare to
     * @return a negative {@code int}, zero ({@code 0}) or a positive
     *         {@code int} as this {@code Boundary} is smaller, equal to or
     *         greater than the {@code Version} compared to
     */
    int compareTo(Version version);

    /**
     * Returns {@code true} if the specified {@link Version} is considered
     * included by this {@link Boundary}.
     *
     * This is Synonymous to {@code this.compareTo(version) <= 0}.
     *
     * @param version the {@code Version} to check for inclusion
     * @return {@code true} if this {@code Boundary} includes the
     *         {@code Version}; {@code false} otherwise
     */
    default boolean includesVersion(final Version version) {
      return this.compareTo(version) <= 0;
    }
  }

  /**
   * Upper {@link Boundary}s of {@link Boundaries} instances.
   */
  @SuppressWarnings("PMD.ImplicitFunctionalInterface")
  public interface Upper {

    /**
     * Compares this instance with the specified {@link Version} for order.
     *
     * Returns a negative {@code int}, zero ({@code 0}) or a positive
     * {@code int} as this {@code Boundary} is smaller, equal to or greater
     * than the {@code Version} compared to.
     *
     * @param version the {@code Version} to check compare to
     * @return a negative {@code int}, zero ({@code 0}) or a positive
     *         {@code int} as this {@code Boundary} is smaller, equal to or
     *         greater than the {@code Version} compared to
     */
    int compareTo(Version version);

    /**
     * /**
     * Returns {@code true} if the specified {@link Version} is considered
     * included by this {@link Boundary}.
     *
     * This is Synonymous to {@code this.compareTo(version) >= 0}.
     *
     * @param version the {@code Version} to check for inclusion
     * @return {@code true} if this {@code Boundary} includes the
     *         {@code Version}; {@code false} otherwise
     */
    default boolean includesVersion(final Version version) {
      return this.compareTo(version) >= 0;
    }
  }

  /**
   * A {@link Boundary} that is based on a {@link BaseVersion}.
   */
  abstract static class WithVersion extends Boundary {
    private static final long serialVersionUID = -5203759370756249414L;

    protected final BaseVersion version;

    WithVersion(final BaseVersion version) {
      this.version = version;
    }

    public final BaseVersion getVersion() {
      return this.version;
    }
  }

  /**
   * A {@link FunctionalInterface} for comparing two {@link Boundary}s similar
   * to {@link Comparable#compareTo(Object)}.
   *
   * A implementation is synonymous with {@code left.compareTo(right)}.
   * @see Boundary#compareTo(Boundary)
   */
  @FunctionalInterface
  private interface Comparison<A extends Boundary, B extends Boundary> {
    /**
     * The {@link Boundary}s are always equal.
     */
    Comparison<? super Boundary, ? super Boundary> EQUAL = (a, b) -> 0;

    /**
     * The first {@link Boundary} is always greater than the second one.
     */
    Comparison<? super Boundary, ? super Boundary> LARGER = (a, b) -> 1;

    /**
     * The first {@link Boundary} is always smaller than the second one.
     */
    Comparison<? super Boundary, ? super Boundary> SMALLER = (a, b) -> -1;

    /**
     * The {@link Boundary} with the larger {@link Version} is considered
     * greater.
     */
    Comparison<? super WithVersion, ? super WithVersion> COMPARE_VERSIONS =
        (a, b) -> a.getVersion().compareTo(b.getVersion());

    /**
     * The {@link Boundary} with the larger {@link Version} is considered
     * greater.  If both are equal the {@code Boundary} first is considered
     * greater.
     */
    Comparison<? super WithVersion, ? super WithVersion> COMPARE_VERSIONS_OR_LARGER =
        (a, b) -> {
          final int cmp = a.getVersion().compareTo(b.getVersion());
          return cmp != 0 ? cmp : 1;
        };

    /**
     * The {@link Boundary} with the larger {@link Version} is considered
     * greater.  If both are equal the {@code Boundary} first is considered
     * smaller.
     */
    Comparison<? super WithVersion, ? super WithVersion> COMPARE_VERSIONS_OR_SMALLER =
        (a, b) -> {
          final int cmp = a.getVersion().compareTo(b.getVersion());
          return cmp != 0 ? cmp : -1;
        };

    /**
     * Compares two {@link Boundary}s.
     *
     * Synonymous to {@code left.compareTo(right)}.
     * @see Boundary#compareTo(Boundary)
     */
    int compare(A left, B right);
  }

  /**
   * An enum containing a {@link BoundaryComparator} for all known
   * {@link Boundary} sub classes.
   *
   * All {@code Boundary}s have to be comparable with each other, which is
   * why they cannot be inherited from.
   */
  private enum BoundaryType {
    UNLIMITED_UPPER_BOUNDARY(
        UnlimitedUpperBoundary.class,
        new BoundaryComparator<>( // compare UnlimitedUpperBoundary to...
            /*UnlimitedUpperBoundary*/ Comparison.EQUAL,
            /*ExclusiveUpperBoundary*/ Comparison.LARGER,
            /*InclusiveUpperBoundary*/ Comparison.LARGER,
            /*UnlimitedLowerBoundary*/ Comparison.LARGER,
            /*ExclusiveLowerBoundary*/ Comparison.LARGER,
            /*InclusiveLowerBoundary*/ Comparison.LARGER)),
    EXCLUSIVE_UPPER_BOUNDARY(
        ExclusiveUpperBoundary.class,
        new BoundaryComparator<>( // compare ExclusiveUpperBoundary to...
            /*UnlimitedUpperBoundary*/ Comparison.SMALLER,
            /*ExclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS,
            /*InclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS_OR_SMALLER,
            /*UnlimitedLowerBoundary*/ Comparison.LARGER,
            /*ExclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS_OR_SMALLER,
            /*InclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS_OR_SMALLER)),
    INCLUSIVE_UPPER_BOUNDARY(
        InclusiveUpperBoundary.class,
        new BoundaryComparator<>( // compare InclusiveUpperBoundary to
            /*UnlimitedUpperBoundary*/ Comparison.SMALLER,
            /*ExclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER,
            /*InclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS,
            /*UnlimitedLowerBoundary*/ Comparison.LARGER,
            /*ExclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS_OR_SMALLER,
            /*InclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS)),
    UNLIMITED_LOWER_BOUNDARY(
        UnlimitedLowerBoundary.class,
        new BoundaryComparator<>( // compare UnlimitedLowerBoundary to...
            /*UnlimitedUpperBoundary*/ Comparison.SMALLER,
            /*ExclusiveUpperBoundary*/ Comparison.SMALLER,
            /*InclusiveUpperBoundary*/ Comparison.SMALLER,
            /*UnlimitedLowerBoundary*/ Comparison.EQUAL,
            /*ExclusiveLowerBoundary*/ Comparison.SMALLER,
            /*InclusiveLowerBoundary*/ Comparison.SMALLER)),
    EXCLUSIVE_LOWER_BOUNDARY(
        ExclusiveLowerBoundary.class,
        new BoundaryComparator<>( // compare ExclusiveLowerBoundary to...
            /*UnlimitedUpperBoundary*/ Comparison.SMALLER,
            /*ExclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER,
            /*InclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER,
            /*UnlimitedLowerBoundary*/ Comparison.LARGER,
            /*ExclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS,
            /*InclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER)),
    INCLUSIVE_LOWER_BOUNDARY(
        InclusiveLowerBoundary.class,
        new BoundaryComparator<>( // compare InclusiveLowerBoundary to...
            /*UnlimitedUpperBoundary*/ Comparison.SMALLER,
            /*ExclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER,
            /*InclusiveUpperBoundary*/ Comparison.COMPARE_VERSIONS,
            /*UnlimitedLowerBoundary*/ Comparison.LARGER,
            /*ExclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS_OR_LARGER,
            /*InclusiveLowerBoundary*/ Comparison.COMPARE_VERSIONS));

    private final Class<? extends Boundary> clazz;
    private final BoundaryComparator<? extends Boundary> comparator;

    <E extends Boundary> BoundaryType(
        final Class<E> clazz, final BoundaryComparator<E> comparator) {
      this.clazz = clazz;
      this.comparator = comparator;
    }

    /**
     * Liefert den, der übergebenen {@link Boundary}-Klasse
     * entsprechenden {@link BoundaryType}.
     * @throws UnknownBoundaryTypeException wenn kein der Klasse
     *     entsprechender BoundaryType existiert.
     */
    static <E extends Boundary> BoundaryType getByClass(final Class<E> clazz) {
      for (final BoundaryType type : BoundaryType.values()) {
        if (type.clazz.equals(clazz)) {
          return type;
        }
      }
      throw new UnknownBoundaryTypeException(clazz);
    }
  }

  /**
   * Class that contains {@link Comparison}s for each known {@link Boundary}
   * sub classes.
   */
  private static final class BoundaryComparator<E extends Boundary> {
    private final Comparison<? super E, ? super UnlimitedUpperBoundary> unlimitedUpper;
    private final Comparison<? super E, ? super ExclusiveUpperBoundary> exclusiveUpper;
    private final Comparison<? super E, ? super InclusiveUpperBoundary> inclusiveUpper;
    private final Comparison<? super E, ? super UnlimitedLowerBoundary> unlimitedLower;
    private final Comparison<? super E, ? super ExclusiveLowerBoundary> exclusiveLower;
    private final Comparison<? super E, ? super InclusiveLowerBoundary> inclusiveLower;

    BoundaryComparator(
        final Comparison<? super E, ? super UnlimitedUpperBoundary> unlimitedUpper,
        final Comparison<? super E, ? super ExclusiveUpperBoundary> exclusiveUpper,
        final Comparison<? super E, ? super InclusiveUpperBoundary> inclusiveUpper,
        final Comparison<? super E, ? super UnlimitedLowerBoundary> unlimitedLower,
        final Comparison<? super E, ? super ExclusiveLowerBoundary> exclusiveLower,
        final Comparison<? super E, ? super InclusiveLowerBoundary> inclusiveLower) {
      this.unlimitedLower = unlimitedLower;
      this.exclusiveUpper = exclusiveUpper;
      this.inclusiveUpper = inclusiveUpper;
      this.unlimitedUpper = unlimitedUpper;
      this.exclusiveLower = exclusiveLower;
      this.inclusiveLower = inclusiveLower;
    }

    /**
     * Vergleicht zwei {@link Boundary}'s der Form
     * <code>left.compareTo(right)</code>.<br>
     * <strong>Achtung</strong>: <code>left</code> muss eine Instanz der
     * Generischen-Klasse <code>E</code> sein!<br>
     * Die Methoden-Signatur muss <code>Boundary</code> sein, da innerhalb
     * des Enums {@link BoundaryType} nicht gecastet werden kann.
     * @throws UnknownBoundaryTypeException wenn <code>right</code> keine
     *     der validen Boundary-Klassen ist
     * @throws ClassCastException wenn <code>left</code> keine Instanz von
     *     <code>E</code> ist
     * @see Boundary#compareTo(Boundary)
     */
    @SuppressWarnings("unchecked")
    int compareBoundaries(final Boundary left, final Boundary right) {
      switch (BoundaryType.getByClass(right.getClass())) {
        case UNLIMITED_UPPER_BOUNDARY:
          return this.unlimitedUpper.compare((E) left, (UnlimitedUpperBoundary) right);
        case EXCLUSIVE_UPPER_BOUNDARY:
          return this.exclusiveUpper.compare((E) left, (ExclusiveUpperBoundary) right);
        case INCLUSIVE_UPPER_BOUNDARY:
          return this.inclusiveUpper.compare((E) left, (InclusiveUpperBoundary) right);
        case UNLIMITED_LOWER_BOUNDARY:
          return this.unlimitedLower.compare((E) left, (UnlimitedLowerBoundary) right);
        case EXCLUSIVE_LOWER_BOUNDARY:
          return this.exclusiveLower.compare((E) left, (ExclusiveLowerBoundary) right);
        case INCLUSIVE_LOWER_BOUNDARY:
          return this.inclusiveLower.compare((E) left, (InclusiveLowerBoundary) right);
        default:
          throw new UnknownBoundaryTypeException(right.getClass());
      }
    }
  }

  /**
   * A {@code Exception} that signifies a sub class of {@link Boundary} was
   * encounted that is not an instance of one of the following ones:
   * <ul>
   *   <li>{@link UnlimitedUpperBoundary}</li>
   *   <li>{@link ExclusiveUpperBoundary}</li>
   *   <li>{@link InclusiveUpperBoundary}</li>
   *   <li>{@link UnlimitedLowerBoundary}</li>
   *   <li>{@link ExclusiveLowerBoundary}</li>
   *   <li>{@link InclusiveLowerBoundary}</li>
   * </ul>
   */
  static final class UnknownBoundaryTypeException extends RuntimeException {
    private static final long serialVersionUID = 8117692656984408427L;

    UnknownBoundaryTypeException(final Class<? extends Boundary> clazz) {
      super("encountered unknown Boundary class: " + clazz.getName());
    }
  }

  /**
   * Compares this {@code Boundary} with the specified one for order.
   *
   * Returns a negative {@code int}, zero ({@code 0}) or a positive
   * {@code int} as this {@code Boundary} is smaller, equal to or greater
   * than the specified one.
   *
   * @param boundary another {@code Boundary} to compare to
   * @return a negative {@code int}, zero ({@code 0}) or a positive
   *         {@code int} as this {@code Boundary} is smaller, equal to or
   *         greater than the specified one.
   * @throws UnknownBoundaryTypeException if the specified {@code Boundary}
   *                                      is not a known sub class
   */
  @Override
  public final int compareTo(final Boundary boundary) {
    final BoundaryType type = BoundaryType.getByClass(this.getClass());
    return type.comparator.compareBoundaries(this, boundary);
  }

  /**
   * Compares this instance with the specified {@link Version} for order.
   *
   * Returns a negative {@code int}, zero ({@code 0}) or a positive
   * {@code int} as this {@code Boundary} is smaller, equal to or greater
   * than the {@code Version} compared to.
   *
   * @param version the {@code Version} to compare to
   * @return a negative {@code int}, zero ({@code 0}) or a positive
   *         {@code int} as this {@code Boundary} is smaller, equal to or
   *         greater than the {@code Version} compared to
   */
  public abstract int compareTo(Version version);

  /**
   * Returns {@code true} if the specified {@link Version} is considered
   * included by this {@link Boundary}.
   *
   * @param version the {@code Version} to check for inclusion
   * @return {@code true} if this {@code Boundary} includes the
   *         {@code Version}; {@code false} otherwise
   */
  public abstract boolean includesVersion(Version version);

  public abstract boolean equals(Object other);
}
