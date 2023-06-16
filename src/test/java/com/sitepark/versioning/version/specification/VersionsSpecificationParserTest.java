package com.sitepark.versioning.version.specification;

import java.text.ParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.VersionBuilder;
import com.sitepark.versioning.version.specification.element.ElementsIntersectException;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.VersionRangeElement;
import com.sitepark.versioning.version.specification.element.boundary.Boundaries;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InvalidBoundariesException;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;

public class VersionsSpecificationParserTest {

	private static final VersionsSpecificationParser PARSER
			= new VersionsSpecificationParser();

	@Test
	public void testSingleExplicitVersion() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addExplicitVersion(
							new ExplicitVersionElement(
									new VersionBuilder()
										.setMajor(1)
										.buildRelease()))
					.build(),
				VersionsSpecificationParserTest.PARSER.parse("1.0"));
	}

	@Test
	public void testMultipleExplicitVersions() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addExplicitVersion(
							new ExplicitVersionElement(
									new VersionBuilder()
										.setMajor(1)
										.buildRelease()))
					.addExplicitVersion(
							new ExplicitVersionElement(
									new VersionBuilder()
										.setMajor(1)
										.setBranch(new Branch("feature_a"))
										.buildRelease()))
					.addExplicitVersion(
							new ExplicitVersionElement(
									new VersionBuilder()
										.setMajor(1)
										.setBranch(new Branch("feature_b"))
										.buildSnapshot()))
					.build(),
				VersionsSpecificationParserTest.PARSER
					.parse("1.0, 1.0-feature_a, 1.0-feature_b-SNAPSHOT"));
	}

	@Test
	public void testSingleInclusiveVersionRange() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new InclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.buildRelease()),
											new InclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(5)
														.buildRelease()))))
					.build(),
				VersionsSpecificationParserTest.PARSER.parse("[1.0, 1.5]"));
	}

	@Test
	public void testSingleExclusiveVersionRange() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new ExclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.buildRelease()),
											new ExclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(5)
														.buildRelease()))))
					.build(),
				VersionsSpecificationParserTest.PARSER.parse("(1.0, 1.5)"));
	}

	@Test
	public void testSingleUpperUnlimitedVersionRange() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new InclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.buildRelease()),
											new UnlimitedUpperBoundary())))
					.build(),
				VersionsSpecificationParserTest.PARSER.parse("[1.0,)"));
	}

	@Test
	public void testSingleLowerUnlimitedVersionRange() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new UnlimitedLowerBoundary(),
											new InclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(1)
														.buildRelease()))))
					.build(),
				VersionsSpecificationParserTest.PARSER.parse("(,1.0]"));
	}

	@Test
	public void testSingleCompletelyUnlimitedVersionRange()
			throws ParseException {
		Assertions.assertThrows(
				InvalidBoundariesException.class,
				() -> VersionsSpecificationParserTest.PARSER.parse("(,)"));
	}

	@Test
	public void testMultipleVersionRanges() throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new InclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.buildRelease()),
											new ExclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(3)
														.buildRelease()))))
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new ExclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(3)
														.buildRelease()),
											new ExclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(7)
														.buildRelease()))))
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new InclusiveLowerBoundary(
													new VersionBuilder()
														.setMajor(1)
														.setMinor(7)
														.setIncremental(3)
														.buildRelease()),
											new UnlimitedUpperBoundary())))
					.build(),
				VersionsSpecificationParserTest.PARSER
					.parse("[1.0, 1.3), (1.3, 1.7), [1.7.3,)"));
	}

	@Test
	public void testMultipleLowerUnlimitedVersionRanges()
			throws ParseException {
		Assertions.assertEquals(
				new VersionsSpecificationBuilder()
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new UnlimitedLowerBoundary(),
									new InclusiveUpperBoundary(
											new VersionBuilder()
												.setMajor(1)
												.buildRelease()))))
					.addVersionRange(
							new VersionRangeElement(
									new Boundaries<>(
											new UnlimitedLowerBoundary(),
											new ExclusiveUpperBoundary(
													new VersionBuilder()
														.setMajor(2)
														.setBranch(
																new Branch(
																		"feat"))
														.buildRelease()))))
					.build(),
				VersionsSpecificationParserTest.PARSER
					.parse("(,1.0], (,2.0-feat)"));
	}

	@Test
	public void testMultipleLowerUnlimitedVersionRangesOfTheSameBranch()
			throws ParseException {
		Assertions.assertThrows(
				ElementsIntersectException.class,
				() -> VersionsSpecificationParserTest.PARSER
					.parse("(,1.0-feature], (,2.0-feature)"));
	}
}