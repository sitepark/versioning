package com.sitepark.versioning.version.specification.element;

import java.util.Map;
import java.util.Set;

import com.sitepark.versioning.Branch;

/**
 * A {@link Set} that sorts {@link SpecificationElement}s by their
 * {@link Branch} in ascending order.  Elements cannot overlap.
 *
 * <p>
 * <strong>Warning</strong>: This class is not thread-safe and does not check
 * for concurrent modifications!
 */
public final class SortedElementBranchSet
		extends MapBasedElementBranchSet<SortedElementSet> {
	private static final long serialVersionUID = -6911160984916789491L;

	/**
	 * Class Constructor.
	 *
	 * Creates an empty instance.
	 */
	public SortedElementBranchSet() {
		super();
	}

	SortedElementBranchSet(final Map<Branch, SortedElementSet> branchMap) {
		super(branchMap);
	}

	@Override
	protected SortedElementSet createItem() {
		return new SortedElementSet();
	}

	/**
	 * Returns a {@link SortedElementBranchSet} of the intersection of this and
	 * another instance such that {@code A ∩ B}.
	 *
	 * @param other another {@code ElementBranchSet} to calculate an
	 *        intersection with
	 * @return a new {@code SortedElementBranchSet} depicting an intersection of
	 *         this and the specified instance; May be empty if there is none
	 */
	@Override
	public SortedElementBranchSet getIntersection(
			final ElementBranchSet other) {
		if (this == other) {
			return this;
		}
		return super.getIntersection((MapBasedElementBranchSet<?>)other);
	}

	@Override
	public UnmodifiableSortedElementBranchSet unmodifiableClone() {
		return super.unmodifiableClone();
	}
}