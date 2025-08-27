package com.sitepark.versioning.version.specification.element;

import java.io.Serializable;
import java.util.Set;

/**
 * A {@link Set} containing {@link SpecificationElement}s, able to intersect
 * with one another.
 */
public interface ElementBranchSet extends Set<SpecificationElement>, Serializable {

  /**
   * Returns a {@link SortedElementBranchSet} of the intersection of this and
   * another instance such that {@code A ∩ B}.
   *
   * @param other another {@code ElementBranchSet} to calculate an
   *        intersection with
   * @return a new {@code SortedElementBranchSet} depicting an intersection of
   *         this and the specified instance; May be empty if there is none
   */
  public SortedElementBranchSet getIntersection(ElementBranchSet other);
}
