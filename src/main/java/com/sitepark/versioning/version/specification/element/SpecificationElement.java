package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.VersionsSpecification;
import java.io.Serializable;
import java.util.Optional;

/**
 * Defines a subset of {@link Version}s as part of a
 * {@link VersionsSpecification}.
 *
 * A implementation may either be {@link ExplicitVersionElement} or
 * {@link VersionRangeElement}.
 *
 * <p>
 * Each instance has to define a {@code Branch} to limit it's contained
 * {@code Version}s to.  If a {@code Version} does not have an equal
 * {@code Branch} it may not be considered contained by the
 * {@link SpecificationElement} instance.
 *
 * @see ExplicitVersionElement
 * @see VersionRangeElement
 */
public sealed interface SpecificationElement extends Serializable
    permits ExplicitVersionElement, VersionRangeElement {

  /**
   * The result of a comparison of two {@link SpecificationElement}s.
   *
   * Since both may either represent a singular value or a range a simple
   * {@code int} (as commonly used by {@link Comparable}) is not sufficient
   * enough to depict this.
   *
   * <p>
   * {@link SpecificationElement}s may only intersect with one another if
   * they have equal {@code Branch}es; Otherwise they will always be either
   * {@link #LOWER} or {@link #LOWER}.
   *
   * @see Branch#compareTo(Branch)
   */
  public enum ComparisonResult {
    /**
     * The element is smaller than the one it was compared to.
     *
     * A reversed comparison should return {@link #HIGHER}.
     */
    LOWER,
    /**
     * The element is larger than the one it was compared to.
     *
     * A reversed comparison should return {@link #LOWER}.
     */
    HIGHER,
    /**
     * Both elements are equal.
     *
     * A reversed comparison should return the same result.
     */
    INTERSECTS_EQUALY,
    /**
     * The element intersects with the one it was compared to on it's lower
     * end.
     *
     * A reversed comparison should return {@link #INTERSECTS_HIGHER}.
     */
    INTERSECTS_LOWER,
    /**
     * The element intersects with the one it was compared to on it's upper
     * end.
     *
     * A reversed comparison should return {@link #INTERSECTS_LOWER}.
     */
    INTERSECTS_HIGHER,
    /**
     * The element completely surrounds the element it was compared to.
     *
     * A reversed comparison should return {@link #INTERSECTS_PARTIALLY}.
     */
    INTERSECTS_COMPLETELY,
    /**
     * The element is completely surrounded by the element it was compared
     * to.
     *
     * A reversed comparison should return {@link #INTERSECTS_COMPLETELY}.
     */
    INTERSECTS_PARTIALLY;

    /**
     * Class Constructor.
     */
    private ComparisonResult() {}
  }

  /**
   * Returns the {@link Branch} this {@link SpecificationElement} is limited
   * to.
   *
   * @return the {@code Branch} of this instance
   */
  public abstract Branch getBranch();

  /**
   * Calculates an intersection between this {@link SpecificationElement} and
   * a specified one.
   *
   * The resulting intersection is represented by a new
   * {@code SpecificationElement} inside an {@link Optional}.  If the two
   * instances do not intersect the returned {@code Optional} is empty.
   *
   * <p>
   * {@link SpecificationElement}s may only intersect with one another if
   * they have equal {@code Branch}es.
   *
   * @param element the {@code SpecificationElement} to calculate an
   *              intersection with
   * @return a {@link Optional} containing the intersection with the specified
   *         instance or an empty one if they do not intersect
   * @see Branch#compareTo(Branch)
   */
  public abstract Optional<SpecificationElement> getIntersection(SpecificationElement element);

  /**
   * Compares this {@link SpecificationElement} to another one.
   *
   * {@link SpecificationElement}s may only intersect with one another if
   * they have equal {@code Branch}es; Otherwise they will always be either
   * {@link ComparisonResult#LOWER} or {@link ComparisonResult#LOWER}.
   *
   * @param element the {@code SpecificationElement} to compare this instance to
   * @return the result of the comparison
   * @see Branch#compareTo(Branch)
   */
  public abstract ComparisonResult compareTo(SpecificationElement element);
}
