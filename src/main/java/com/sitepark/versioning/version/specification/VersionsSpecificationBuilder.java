package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.specification.element.ElementsIntersectException;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.SortedElementBranchSet;
import com.sitepark.versioning.version.specification.element.SpecificationElement;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
import com.sitepark.versioning.version.specification.element.boundary.InvalidBoundariesException;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;

/**
 * A builder class to create {@link VersionsSpecification} instances.
 *
 * This process is done by adding {@link VersionRangeElement}s and/or
 * {@link ExplicitVersionElement}s to this builder instance and then invoking
 * the {@link #build()} method to create a new {@code VersionsSpecification}
 * instance with all of the set {@link SpecificationElement}s (at least one).
 *
 * <p>
 * Each {code build()} invokation will create a new instance and may be used as
 * often as needed.  Modifications to the builder instance will not affect
 * {@code VersionsSpecification} instances that were build by it prior.
 *
 * <p>
 * <strong>Warning</strong>: This class uses a {@link SortedElementBranchSet}
 * and is therefore not thread-safe!
 */
public final class VersionsSpecificationBuilder {
  private final SortedElementBranchSet elements;

  /**
   * Class Constructor
   */
  public VersionsSpecificationBuilder() {
    this.elements = new SortedElementBranchSet();
  }

  /**
   * Adds a {@link VersionRangeElement} to this instance to create
   * {@link VersionsSpecification} instances with.
   *
   * <p>
   * This method accesses a {@link SortedElementBranchSet} and is therefore
   * not thread-safe!
   *
   * @param lower the {@link Boundary.Lower Lower} {@link Boundary} of the range
   * @param upper the {@link Boundary.Upper Upper} {@code Boundary} of the range
   * @return this instance
   * @throws InvalidBoundariesException if the lower {@code Boundary} is
   *                                    considered larger than the upper
   *                                    {@code Boundary}, both are
   *                                    {@link UnlimitedLowerBoundary} and
   *                                    {@link UnlimitedUpperBoundary} instances
   *                                    or both are based on
   *                                    {@link BaseVersion}s with different
   *                                    {@link Branch}es.
   * @throws ElementsIntersectException if the specified element intersects
   *                                    with a already present one
   * @see #build()
   */
  public VersionsSpecificationBuilder addVersionRange(
      final Boundary.Lower lower, final Boundary.Upper upper) {
    this.elements.add(new VersionRangeElement(lower, upper));
    return this;
  }

  /**
   * Adds a {@link ExplicitVersionElement} to this instance to create
   * {@link VersionsSpecification} instances with.
   *
   * <p>
   * This method accesses a {@code SortedElementBranchSet} and is therefore
   * not thread-safe!
   *
   * @param version the {@link BaseVersion} to add
   * @return this instance
   * @throws ElementsIntersectException if the specified element intersects
   *                                    with a already present one
   * @see #build()
   */
  public VersionsSpecificationBuilder addExplicitVersion(final BaseVersion version) {
    this.elements.add(new ExplicitVersionElement(version));
    return this;
  }

  /**
   * Creates and returns a new {@link VersionsSpecification} instance.
   *
   * The resulting instance contains all {@link VersionRangeElement}s and
   * {@link ExplicitVersionElement}s priviously set on this instance (at
   * least one is required).
   *
   * <p>
   * This method may be invoked as many times as needed, each creating a new
   * instance.  Modifications to the builder instance will not affect
   * {@code VersionsSpecification} instances that were build by it prior.
   *
   * @return a new {@code VersionsSpecification} instance build from all
   *         fields set on this instance
   * @throws IllegalArgumentException if this builder is empty
   * @see #addVersionRange(Boundary.Lower, Boundary.Upper)
   * @see #addExplicitVersion(BaseVersion)
   */
  public VersionsSpecification build() {
    return new VersionsSpecification(this);
  }

  SortedElementBranchSet getElements() {
    return this.elements;
  }
}
