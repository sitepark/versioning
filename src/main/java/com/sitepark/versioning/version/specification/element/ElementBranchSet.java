package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.version.Version;
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

  /**
   * Returns wether a {@link Version} is considered contained by any of the
   * {@link SpecificationElement}s in this instance.
   *
   * More formally, returns {@code true} if this instance contains one
   * {@code element} such that {@code SpecificationElement.contains(version)}.
   *
   * @param version the {@code Version} to check
   * @return {@code true} if the {@code Version} is contained in this instance
   */
  public boolean containsVersion(Version version);
}
