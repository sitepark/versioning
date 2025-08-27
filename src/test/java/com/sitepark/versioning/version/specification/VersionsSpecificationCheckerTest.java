package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.VersionBuilder;
import com.sitepark.versioning.version.specification.element.ElementsIntersectException;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionsSpecificationCheckerTest {

  private final BaseVersion onePointTwo =
      new VersionBuilder().setMajor(1).setMinor(2).buildRelease();
  private final BaseVersion onePointTwoBranch =
      new VersionBuilder().setMajor(1).setMinor(2).setBranch(new Branch("feature")).buildRelease();
  private final BaseVersion onePointTwoSnapshot =
      new VersionBuilder().setMajor(1).setMinor(2).buildSnapshot();
  private final BaseVersion onePointTwoPointOne =
      new VersionBuilder().setMajor(1).setMinor(2).setIncremental(1).buildSnapshot();

  @Test
  public void testSingleVersionRangeInclusiveUpperContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new UnlimitedLowerBoundary(), new InclusiveUpperBoundary(this.onePointTwo))
            .build();
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testSingleVersionRangeExclusiveUpperContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new UnlimitedLowerBoundary(), new ExclusiveUpperBoundary(this.onePointTwo))
            .build();
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testSingleVersionRangeInclusiveLowerContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new InclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
            .build();
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testSingleVersionRangeExclusiveLowerContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new ExclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
            .build();
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testMultipleExclusiveVersionRangesContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new UnlimitedLowerBoundary(), new ExclusiveUpperBoundary(this.onePointTwo))
            .addVersionRange(
                new ExclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
            .build();
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testMultipleInclusiveVersionRangesContains() {
    Assertions.assertThrows(
        ElementsIntersectException.class,
        () ->
            this.builder()
                .addVersionRange(
                    new UnlimitedLowerBoundary(), new InclusiveUpperBoundary(this.onePointTwo))
                .addVersionRange(
                    new InclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
                .build());
  }

  @Test
  public void testMultipleBranchesVersionRangesContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new UnlimitedLowerBoundary(), new InclusiveUpperBoundary(this.onePointTwoBranch))
            .addVersionRange(
                new InclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
            .build();
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwo, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoBranch, specification));
    Assertions.assertFalse(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoSnapshot, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.DEFAULT.check(this.onePointTwoPointOne, specification));
  }

  @Test
  public void testIgnoringBranchesContains() {
    final VersionsSpecification specification =
        this.builder()
            .addVersionRange(
                new UnlimitedLowerBoundary(), new InclusiveUpperBoundary(this.onePointTwoBranch))
            .addVersionRange(
                new InclusiveLowerBoundary(this.onePointTwo), new UnlimitedUpperBoundary())
            .build();
    Assertions.assertTrue(
        VersionsSpecificationChecker.IGNORING_BRANCHES.check(this.onePointTwo, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.IGNORING_BRANCHES.check(
            this.onePointTwoBranch, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.IGNORING_BRANCHES.check(
            this.onePointTwoSnapshot, specification));
    Assertions.assertTrue(
        VersionsSpecificationChecker.IGNORING_BRANCHES.check(
            this.onePointTwoPointOne, specification));
  }

  private final VersionsSpecificationBuilder builder() {
    return new VersionsSpecificationBuilder();
  }
}
