package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.SortedElementBranchSet;
import com.sitepark.versioning.version.specification.element.SpecificationElement;
import com.sitepark.versioning.version.specification.element.UnmodifiableSortedElementBranchSet;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundaries;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;

/**
 * A specification of a subset of {@link Version}s.
 * This is similar to
 * <a href="https://cwiki.apache.org/confluence/display/MAVENOLD/Dependency+Mediation+and+Conflict+Resolution#DependencyMediationandConflictResolution-DependencyVersionRanges">Maven's VersionRanges</a>
 * and (appart from the syntax)
 * <a href="https://devhints.io/semver">Semver's Semantic Versioning Syntax</a>.
 * The biggest difference is the possibility to limit {@code Version}s by their
 * {@link Branch}es.  If all {@code Version}s of a {@link SpecificationElement}
 * define the same {@code Branch} it only permits (otherwise acceptable)
 * {@code Version}s of that {@code Branch}.
 *
 * <p>
 * Generally speaking, a {@code VersionsSpecification} is a set of one or more
 * {@code SpecificationElement}s, each defining a subset of {@code Version}s to
 * permit.  These may not overlap.
 *
 * @see #containsVersion(Version)
 * @see VersionsSpecificationParser
 * @see VersionsSpecificationBuilder
 */
public class VersionsSpecification implements Serializable {
  private static final long serialVersionUID = 6549872561421500098L;

  private final UnmodifiableSortedElementBranchSet elements;

  VersionsSpecification(final VersionsSpecificationBuilder builder) {
    this(builder.getElements().unmodifiableClone());
  }

  private VersionsSpecification(final UnmodifiableSortedElementBranchSet elements) {
    if (elements.isEmpty()) {
      throw new IllegalArgumentException(
          "a VersionsSpecification has to have at least one Element");
    }
    this.elements = elements;
  }

  /**
   * Returns wether a {@link Version} is contained inside this instance.
   *
   * This is the case if any of this instances {@link SpecificationElement}s
   * consideres the {@code Version} to be either equal (in the case of
   * {@link ExplicitVersionElement}s) or inside it's {@link Boundaries} (in
   * the case of {@link VersionRangeElement}s).
   *
   * @param version the {@code Version} to check
   * @return {@code true} if the {@code Version} is compliant with this
   *         instance, {@code false} otherwise
   * @see SpecificationElement#containsVersion(Version)
   */
  public boolean containsVersion(final Version version) {
    return this.elements.containsVersion(version);
  }

  /**
   * Returns a {@link VersionsSpecification} of the intersection of this and
   * another instance such that {@code A ∩ B}.
   *
   * @param other another {@code VersionsSpecification} to calculate an
   *        intersection with
   * @return an {@link Optional} of a new {@code VersionsSpecification}
   *         depicting an intersection of this and the specified instance
   *         or an empty {@code Optional} if there is none
   * @see SpecificationElement#getIntersection(SpecificationElement)
   */
  public Optional<VersionsSpecification> getIntersection(final VersionsSpecification other) {
    return Optional.of(this.elements.getIntersection(other.elements))
        .filter(e -> !e.isEmpty())
        .map(SortedElementBranchSet::unmodifiableClone)
        .map(VersionsSpecification::new);
  }

  /**
   * Returns a String representation of this instance.
   *
   * The resulting String is a verbose form of a String a
   * {@link VersionsSpecificationParser} would parse into an instance equal
   * to this one.
   *
   * <p>
   * The {@link SpecificationElement}s are sorted and grouped by their
   * {@link Branch}es.
   *
   * <p>
   * An example may look like this:
   * <code>[1.0.0,1.5.0),(1.5.0,),1.5.0-featureA,1.4.9-featureB</code>
   *
   * @return a descriptive String of this instance
   * @see VersionsSpecificationParser#parse(String)
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(64);
    for (final Iterator<SpecificationElement> iterator = this.elements.iterator();
        iterator.hasNext(); ) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append(',');
      }
    }
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof VersionsSpecification)) {
      return false;
    }
    final VersionsSpecification that = (VersionsSpecification) other;
    return this.elements.equals(that.elements);
  }

  @Override
  @SuppressWarnings("checkstyle:nofinalizer")
  protected final void finalize() {
    // prevent finalizer attacks
  }
}
