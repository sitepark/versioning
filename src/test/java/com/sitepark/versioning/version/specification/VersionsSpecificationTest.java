package com.sitepark.versioning.version.specification;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.VersionBuilder;
import com.sitepark.versioning.version.specification.element.ElementsIntersectException;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundaries;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;

public class VersionsSpecificationTest {

	private final BaseVersion onePointTwo = new VersionBuilder()
		.setMajor(1)
		.setMinor(2)
		.buildRelease();
	private final BaseVersion onePointTwoBranch = new VersionBuilder()
		.setMajor(1)
		.setMinor(2)
		.setBranch(new Branch("feature"))
		.buildRelease();
	private final BaseVersion onePointTwoSnapshot = new VersionBuilder()
		.setMajor(1)
		.setMinor(2)
		.buildSnapshot();
	private final BaseVersion onePointTwoPointOne = new VersionBuilder()
		.setMajor(1)
		.setMinor(2)
		.setIncremental(1)
		.buildSnapshot();

	@Test
	public void testEmptySpecification() {
		Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> this.builder().build());
	}

	@Test
	public void testToString() {
		final String result = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new ExclusiveUpperBoundary(
											this.onePointTwo))))
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoBranch))
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new ExclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build()
			.toString();
		Assertions.assertEquals("1.2.0-feature,(,1.2.0),(1.2.0,)", result);
	}

	@Test
	public void testSingleVersionRangeInclusiveUpperContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
												this.onePointTwo))))
			.build();
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testSingleVersionRangeExclusiveUpperContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new ExclusiveUpperBoundary(
											this.onePointTwo))))
			.build();
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testSingleVersionRangeInclusiveLowerContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build();
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testSingleVersionRangeExclusiveLowerContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new ExclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build();
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testMultipleExclusiveVersionRangesContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new ExclusiveUpperBoundary(
											this.onePointTwo))))
			.addVersionRange(
					new  VersionRangeElement(
							new Boundaries<>(
									new ExclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build();
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testMultipleInclusiveVersionRangesContains() {
		Assertions.assertThrows(
				ElementsIntersectException.class,
				() -> this.builder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new UnlimitedLowerBoundary(),
											new InclusiveUpperBoundary(
													this.onePointTwo))))
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new InclusiveLowerBoundary(
													this.onePointTwo),
											new UnlimitedUpperBoundary())))
					.build());
	}

	@Test
	public void testMultipleBranchesVersionRangesContains() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
											this.onePointTwoBranch))))
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build();
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwo));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoBranch));
		Assertions.assertFalse(
				specification.containsVersion(this.onePointTwoSnapshot));
		Assertions.assertTrue(
				specification.containsVersion(this.onePointTwoPointOne));
	}

	@Test
	public void testEqualExplicitVersionIntersection() {
		final VersionsSpecification specification = this.builder()
			.addExplicitVersion(new ExplicitVersionElement(this.onePointTwo))
			.build();
		Optional<VersionsSpecification> intersection
				= specification.getIntersection(specification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(specification, intersection.get());
	}

	@Test
	public void testEqualVersionRangeIntersection() {
		final VersionsSpecification specification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwoSnapshot),
									new InclusiveUpperBoundary(
											this.onePointTwoPointOne))))
			.build();
		Optional<VersionsSpecification> intersection
				= specification.getIntersection(specification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(specification, intersection.get());
	}

	@Test
	public void testExplicitVersionAndVersionRangeIntersection() {
		final VersionsSpecification rangeSpecification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwoSnapshot),
									new InclusiveUpperBoundary(
											this.onePointTwoPointOne))))
			.build();
		final VersionsSpecification versionSpecification = this.builder()
			.addExplicitVersion(new ExplicitVersionElement(this.onePointTwo))
			.build();
		Optional<VersionsSpecification> intersection
				= rangeSpecification.getIntersection(versionSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(versionSpecification, intersection.get());
		intersection = versionSpecification.getIntersection(rangeSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(versionSpecification, intersection.get());
	}

	@Test
	public void testVersionRangesPartialIntersection() {
		final InclusiveLowerBoundary highestLower = new InclusiveLowerBoundary(
				this.onePointTwoSnapshot);
		final InclusiveUpperBoundary lowestUpper = new InclusiveUpperBoundary(
				this.onePointTwoPointOne);
		final VersionsSpecification specification1 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									highestLower,
									new UnlimitedUpperBoundary())))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									lowestUpper)))
			.build();
		final VersionsSpecification expectedIntersection = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(highestLower, lowestUpper)))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
	}

	@Test
	public void testVersionRangesCompleteIntersection() {
		final InclusiveLowerBoundary highestLower = new InclusiveLowerBoundary(
				this.onePointTwoSnapshot);
		final InclusiveUpperBoundary lowestUpper = new InclusiveUpperBoundary(
				this.onePointTwo);
		final VersionsSpecification specification1 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									highestLower,
									lowestUpper)))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
											this.onePointTwoPointOne))))
			.build();
		final VersionsSpecification expectedIntersection = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(highestLower, lowestUpper)))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
	}

	@Test
	public void testVersionRangeAndMultipleExplicitVersionsIntersection() {
		final VersionsSpecification rangeSpecification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwoSnapshot),
									new InclusiveUpperBoundary(
											this.onePointTwoPointOne))))
			.build();
		final VersionsSpecification versionSpecification = this.builder()
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoSnapshot))
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwo))
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoPointOne))
			.build();
		Optional<VersionsSpecification> intersection
				= rangeSpecification.getIntersection(versionSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(versionSpecification, intersection.get());
		intersection = versionSpecification.getIntersection(rangeSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(versionSpecification, intersection.get());
	}

	@Test
	public void testVersionRangeAndMultipleExplicitVersionsPartialIntersection() {
		final VersionsSpecification rangeSpecification = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new ExclusiveLowerBoundary(
											this.onePointTwoSnapshot),
									new ExclusiveUpperBoundary(
											this.onePointTwoPointOne))))
			.build();
		final VersionsSpecification versionSpecification = this.builder()
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoSnapshot))
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwo))
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoPointOne))
			.build();
		final VersionsSpecification expected = this.builder()
			.addExplicitVersion(new ExplicitVersionElement(this.onePointTwo))
			.build();
		Optional<VersionsSpecification> intersection
				= rangeSpecification.getIntersection(versionSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expected, intersection.get());
		intersection = versionSpecification.getIntersection(rangeSpecification);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expected, intersection.get());
	}

	@Test
	public void testMultipleVersionRangesIntersection() {
		final InclusiveLowerBoundary lower1 = new InclusiveLowerBoundary(
				this.onePointTwoSnapshot);
		final ExclusiveUpperBoundary upper1 = new ExclusiveUpperBoundary(
				this.onePointTwo);
		final ExclusiveLowerBoundary lower2 = new ExclusiveLowerBoundary(
				this.onePointTwo);
		final InclusiveUpperBoundary upper2 = new InclusiveUpperBoundary(
				this.onePointTwoPointOne);
		final VersionsSpecification specification1 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									upper1)))
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									lower2,
									new UnlimitedUpperBoundary())))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addVersionRange(
					new VersionRangeElement(new Boundaries<>(lower1, upper2)))
			.build();
		final VersionsSpecification expectedIntersection = this.builder()
			.addVersionRange(
					new VersionRangeElement(new Boundaries<>(lower1, upper1)))
			.addVersionRange(
					new VersionRangeElement(new Boundaries<>(lower2, upper2)))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
	}

	@Test
	public void testMultipleVersionRangesOfDifferentBranchesIntersection() {
		final ExclusiveLowerBoundary lower = new ExclusiveLowerBoundary(
				this.onePointTwo);
		final InclusiveUpperBoundary upper = new InclusiveUpperBoundary(
				this.onePointTwoPointOne);
		final VersionsSpecification specification1 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
											this.onePointTwoBranch))))
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									lower,
									new UnlimitedUpperBoundary())))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new InclusiveLowerBoundary(
											this.onePointTwoSnapshot),
									upper)))
			.build();
		final VersionsSpecification expectedIntersection = this.builder()
			.addVersionRange(
					new VersionRangeElement(new Boundaries<>(lower, upper)))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertTrue(intersection.isPresent());
		Assertions.assertEquals(expectedIntersection, intersection.get());
	}

	@Test
	public void testDifferentExplicitVersionsIntersect() {
		final VersionsSpecification specification1 = this.builder()
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwo))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addExplicitVersion(
					new ExplicitVersionElement(this.onePointTwoBranch))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertFalse(intersection.isPresent());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertFalse(intersection.isPresent());
	}

	@Test
	public void testDifferentVersionRangesIntersect() {
		final VersionsSpecification specification1 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
											this.onePointTwo))))
			.build();
		final VersionsSpecification specification2 = this.builder()
			.addVersionRange(
					new VersionRangeElement(
							new Boundaries<>(
									new ExclusiveLowerBoundary(
											this.onePointTwo),
									new UnlimitedUpperBoundary())))
			.build();
		Optional<VersionsSpecification> intersection
				= specification1.getIntersection(specification2);
		Assertions.assertFalse(intersection.isPresent());
		intersection = specification2.getIntersection(specification1);
		Assertions.assertFalse(intersection.isPresent());
	}

	private final VersionsSpecificationBuilder builder() {
		return new VersionsSpecificationBuilder();
	}
}