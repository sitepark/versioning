package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.VersionComparator;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.SpecificationElement;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;

/**
 * Verifies that a {@link Version} satisfies the rules of a {@link VersionsSpecification}s.
 */
@FunctionalInterface
public interface VersionsSpecificationChecker {

  /**
   * A default {@link VersionsSpecificationChecker} implementation.
   *
   * Checks for each {@link SpecificationElement} with the same {@link Branch}
   * as the {@link Version}, wether they are equal in the case of
   * {@link ExplicitVersionElement}s or inside it's {@link Boundary}'s in
   * the case of {@link VersionRangeElement}s.
   *
   * <p>
   * This instance uses the {@link VersionComparator#NATUAL} for comparissons.
   */
  public static final VersionsSpecificationChecker DEFAULT =
      new VersionsSpecificationChecker() {
        @Override
        public boolean check(final Version version, final VersionsSpecification specification) {
          return specification
              .getElements()
              .forBranch(version.getBranch())
              .anyMatch(element -> this.checkElement(element, version, VersionComparator.NATUAL));
        }
      };

  /**
   * A {@link VersionsSpecificationChecker} implementation, that ignores the
   * {@link Branch}es of the {@link SpecificationElement}s and the
   * {@link Version}s given.
   *
   * <p>
   * This means, that a specification like
   * {@code [3.4.1-feature, 4.0.0-feature-SNAPSHOT)}, complies with all of these
   * versions: {@code 3.4.2, 3.4.2-feature, 3.4.2-otherfeature} under this
   * instance, whereas {@link #DEFAULT} would only allow {@code 3.4.2-feature}.
   */
  public static final VersionsSpecificationChecker IGNORING_BRANCHES =
      new VersionsSpecificationChecker() {
        @Override
        public boolean check(final Version version, final VersionsSpecification specification) {
          return specification.getElements().stream()
              .anyMatch(
                  element ->
                      this.checkElement(element, version, VersionComparator.IGNORING_BRANCHES));
        }
      };

  /**
   * Verifies that a {@link Version} complies with a {@link VersionsSpecification}s.
   *
   * @param specification the specification to check the {@code version} against
   * @param version the version to check against the {@code specification}
   * @return {@code true} if the {@code Version} is compliant with the
   *         {@code VersionsSpecification}, {@code false} otherwise
   */
  public boolean check(Version version, VersionsSpecification specification);

  /**
   * Performs a "check", wether the given {@link Version} complies with a
   * {@link SpecificationElement}.
   *
   * A version {@code V} may be considered complient to an element {@code E} if
   * either:
   *
   * <ul>
   * <li><code>B<sub>L</sub> ≤ V ≤ B<sub>U</sub></code> when {@code E} is a
   *   {@link VersionRangeElement} with the {@link Boundary.Lower}
   *   <code>B<sub>L</sub></code> and the {@link Boundary.Upper}
   *   <code>B<sub>U</sub></code></li>
   * <li><code>V == V<sub>E</sub></code> when {@code E} is a
   *   {@link ExplicitVersionElement} with the {@code Version}
   *   <code>V<sub>E</sub></code></li>
   * </ul>
   *
   * @param element the element to check the {@code version} against
   * @param version the version to check against the {@code element}
   * @param comparator a {@link VersionComparator} to use for comparissons
   * @return {@code true} if the {@code Version} is compliant with the
   *         {@code SpecificationElement}, {@code false} otherwise
   */
  default boolean checkElement(
      final SpecificationElement element,
      final Version version,
      final VersionComparator<Version> comparator) {
    return switch (element) {
      case ExplicitVersionElement e -> comparator.compare(e.getVersion(), version) == 0;
      case VersionRangeElement v ->
          switch (v.getLowerBoundary()) {
                case ExclusiveLowerBoundary b -> comparator.compare(b.getVersion(), version) < 0;
                case InclusiveLowerBoundary b -> comparator.compare(b.getVersion(), version) <= 0;
                case UnlimitedLowerBoundary b -> true;
              }
              && switch (v.getUpperBoundary()) {
                case ExclusiveUpperBoundary b -> comparator.compare(b.getVersion(), version) > 0;
                case InclusiveUpperBoundary b -> comparator.compare(b.getVersion(), version) >= 0;
                case UnlimitedUpperBoundary b -> true;
              };
    };
  }
}
