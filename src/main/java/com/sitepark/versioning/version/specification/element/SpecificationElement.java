package com.sitepark.versioning.version.specification.element;

import java.io.Serializable;
import java.util.Optional;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.VersionsSpecification;
import com.sitepark.versioning.version.specification.element.boundary.Boundaries;

/**
 * Defines a subset of {@link Version}s as part of a
 * {@link VersionsSpecification}.
 *
 * A implementation may either be {@link VersionBased} or
 * {@link BoundariesBased}.
 *
 * <p>
 * Each instance has to define a {@code Branch} to limit it's contained
 * {@code Version}s to.  If a {@code Version} does not have an equal
 * {@code Branch} it may not be considered contained by the
 * {@link SpecificationElement} instance.
 *
 * @see #containsVersion(Version)
 * @see ExplicitVersionElement
 * @see VersionRangeElement
 */
public abstract class SpecificationElement implements Serializable {
	private static final long serialVersionUID = 3293062041380042736L;

	/**
	 * Class Constructor.
	 */
	SpecificationElement() {
	}

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
		private ComparisonResult() {
		}
	}

	/**
	 * A type of a {@link SpecificationElement} that is based on a singular
	 * {@link Version}.
	 */
	public static abstract class VersionBased extends SpecificationElement {
		private static final long serialVersionUID = 5066407549129927233L;

		/**
		 * Class Constructor.
		 */
		VersionBased() {
		}

		/**
		 * Returns the {@link Version} this instance is based on.
		 *
		 * @return the {@code Version} of this instance
		 */
		abstract BaseVersion getVersion();
	}

	/**
	 * A type of a {@link SpecificationElement} that is based on
	 * {@link Boundaries}.
	 */
	public static abstract class BoundariesBased extends SpecificationElement {
		private static final long serialVersionUID = -3922176996016553181L;

		/**
		 * Class Constructor.
		 */
		BoundariesBased() {
		}

		/**
		 * Returns the {@link Boundaries} this instance is based on.
		 *
		 * @return the {@code Boundaries} of this instance
		 */
		abstract Boundaries<?, ?> getBoundaries();
	}

	/**
	 * A {@code Exception} that signifies a sub class of
	 * {@link SpecificationElement} was encounted that is neither
	 * {@link VersionBased} nor {@link BoundariesBased}.
	 */
	public static final class UnknownSpecificationElementException
			extends RuntimeException {
		private static final long serialVersionUID = -2028795695995999260L;

		/**
		 * Class Constructor specifying the unknown {@link SpecificationElement}
		 * sub class.
		 *
		 * @param clazz the {@code SpecificationElement} sub class
		 */
		public UnknownSpecificationElementException(
				final Class<? extends SpecificationElement> clazz) {
			super(
					"unknown SpecificationElement subclass \""
						+ clazz.getName() + "\" encountered");
		}
	}

	/**
	 * Returns the {@link Branch} this {@link SpecificationElement} is limited
	 * to.
	 *
	 * Only {@link Version}s with a equal {@code Branch} may be considered
	 * contained by this instance.
	 *
	 * @return the {@code Branch} of this instance
	 * @see #containsVersion(Version)
	 */
	public abstract Branch getBranch();

	/**
	 * Returns wether a {@link Version} is contained in the subset represented
	 * by this instance.
	 *
	 * Only {@link Version}s with a equal {@code Branch} may be considered
	 * contained.
	 *
	 * @param version the {@code Version} to check
	 * @return {@code true} if the {@code Version} is contained in this instance
	 */
	public abstract boolean containsVersion(Version version);

	/**
	 * Calculates an intersection between this {@link SpecificationElement} and
	 * a specified one that is {@link VersionBased}.
	 *
	 * The resulting intersection is represented by a new
	 * {@code SpecificationElement} inside an {@link Optional}.  If the two
	 * instances do not intersect the returned {@code Optional} is empty.
	 *
	 * <p>
	 * {@link SpecificationElement}s may only intersect with one another if
	 * they have equal {@code Branch}es.
	 *
	 * @param other the {@code VersionBased} {@code SpecificationElement} to
	 *        calculate an intersection with
	 * @return a {@link Optional} containing the intersection with the specified
	 *         instance or an empty one if they do not intersect
	 * @see Branch#compareTo(Branch)
	 * @see #getIntersection(SpecificationElement)
	 * @see #getIntersectionWithBoundariesBased(BoundariesBased)
	 */
	abstract Optional<SpecificationElement> getIntersectionWithVersionBased(
			VersionBased other);

	/**
	 * Calculates an intersection between this {@link SpecificationElement} and
	 * a specified one that is {@link BoundariesBased}.
	 *
	 * The resulting intersection is represented by a new
	 * {@code SpecificationElement} inside an {@link Optional}.  If the two
	 * instances do not intersect the returned {@code Optional} is empty.
	 *
	 * <p>
	 * {@link SpecificationElement}s may only intersect with one another if
	 * they have equal {@code Branch}es.
	 *
	 * @param other the {@code VersionBased} {@code SpecificationElement} to
	 *        calculate an intersection with
	 * @return a {@link Optional} containing the intersection with the specified
	 *         instance or an empty one if they do not intersect
	 * @see Branch#compareTo(Branch)
	 * @see #getIntersection(SpecificationElement)
	 * @see #getIntersectionWithVersionBased(VersionBased)
	 */
	abstract Optional<SpecificationElement> getIntersectionWithBoundariesBased(
			BoundariesBased other);

	/**
	 * Compares this {@link SpecificationElement} to another
	 * {@link VersionBased} one.
	 *
	 * {@link SpecificationElement}s may only intersect with one another if
	 * they have equal {@code Branch}es; Otherwise they will always be either
	 * {@link ComparisonResult#LOWER} or {@link ComparisonResult#LOWER}.
	 *
	 * @param other the {@code VersionBased} {@code SpecificationElement} to
	 *        compare this instance to
	 * @return the result of the comparison
	 * @see Branch#compareTo(Branch)
	 * @see #compareTo(SpecificationElement)
	 * @see #compareToBoundariesBased(BoundariesBased)
	 */
	abstract ComparisonResult compareToVersionBased(VersionBased other);

	/**
	 * Compares this {@link SpecificationElement} to another
	 * {@link BoundariesBased} one.
	 *
	 * {@link SpecificationElement}s may only intersect with one another if
	 * they have equal {@code Branch}es; Otherwise they will always be either
	 * {@link ComparisonResult#LOWER} or {@link ComparisonResult#LOWER}.
	 *
	 * @param other the {@code BoundariesBased} {@code SpecificationElement} to
	 *        compare this instance to
	 * @return the result of the comparison
	 * @see Branch#compareTo(Branch)
	 * @see #compareTo(SpecificationElement)
	 * @see #compareToVersionBased(VersionBased)
	 */
	abstract ComparisonResult compareToBoundariesBased(BoundariesBased other);

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
	 * @param other the {@code SpecificationElement} to calculate an
	 *              intersection with
	 * @return a {@link Optional} containing the intersection with the specified
	 *         instance or an empty one if they do not intersect
	 * @see Branch#compareTo(Branch)
	 * @see #getIntersectionWithVersionBased(VersionBased)
	 * @see #getIntersectionWithBoundariesBased(BoundariesBased)
	 */
	public final Optional<SpecificationElement> getIntersection(
			final SpecificationElement other) {
		if (other instanceof VersionBased) {
			return this.getIntersectionWithVersionBased((VersionBased)other);
		}
		if (other instanceof BoundariesBased) {
			return this.getIntersectionWithBoundariesBased(
					(BoundariesBased)other);
		}
		throw new UnknownSpecificationElementException(other.getClass());
	}

	/**
	 * Compares this {@link SpecificationElement} to another one.
	 *
	 * {@link SpecificationElement}s may only intersect with one another if
	 * they have equal {@code Branch}es; Otherwise they will always be either
	 * {@link ComparisonResult#LOWER} or {@link ComparisonResult#LOWER}.
	 *
	 * @param other the {@code SpecificationElement} to compare this instance to
	 * @return the result of the comparison
	 * @throws UnknownSpecificationElementException if the supplied
	 *                                              {@code SpecificationElement}
	 *                                              is not a subclass of either
	 *                                              {@link VersionBased} or
	 *                                              {@link BoundariesBased}.
	 * @see Branch#compareTo(Branch)
	 * @see #compareToVersionBased(VersionBased)
	 * @see #compareToBoundariesBased(BoundariesBased)
	 */
	public final ComparisonResult compareTo(final SpecificationElement other) {
		if (other instanceof VersionBased) {
			return this.compareToVersionBased((VersionBased)other);
		}
		if (other instanceof BoundariesBased) {
			return this.compareToBoundariesBased((BoundariesBased)other);
		}
		throw new UnknownSpecificationElementException(other.getClass());
	}
}