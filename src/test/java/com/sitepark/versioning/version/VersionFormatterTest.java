package com.sitepark.versioning.version;

import java.text.ParseException;
import java.util.IllegalFormatFlagsException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VersionFormatterTest {

	public static Version featureVersion;
	public static Version fullVersion;
	public static Version qualifiersVersion;
	public static Version simpleVersion;
	public static Version snapshotVersion;
	public static Version unparsedSnapshotVersion;

	@BeforeAll
	public static void setUp() throws ParseException {
		VersionFormatterTest.featureVersion = VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot("1.2.3-my_feature")
			.get();
		VersionFormatterTest.fullVersion = VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-my_feature-some-other-qualifiers-20121209.171545-13")
			.get();
		VersionFormatterTest.qualifiersVersion = VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-my_feature-some-other-qualifiers")
			.get();
		VersionFormatterTest.simpleVersion = VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot("1.2.3")
			.get();
		VersionFormatterTest.snapshotVersion = VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot("1.2.3-20121209.171545-13")
			.get();
		VersionFormatterTest.unparsedSnapshotVersion
				= VersionParser.DEFAULT_PARSER
			.parsePotentialConcreteSnapshot("1.2.3-SNAPSHOT")
			.get();
	}

	@Test
	public void testRawStringFormat() {
		final String format = "i am just a string";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				format,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				format,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				format,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				format,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				format,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				format,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testLiteralColonFormat() {
		final String format = "i am just a string with a single ::";
		final String expected = "i am just a string with a single :";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testMajorKeyword() {
		final String format = "the major version is :MAJOR:.";
		final String expected = "the major version is 1.";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testMinorKeyword() {
		final String format = "the minor version is :MINOR:.";
		final String expected = "the minor version is 2.";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testIncrementalKeyword() {
		final String format = "the incremetal version is :INCREMENTAL:.";
		final String expected = "the incremetal version is 3.";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				expected,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testFeatureKeyword() {
		final String format = "the feature branch is :FEATURE:.";
		final String absent = "the feature branch is .";
		final String present = "the feature branch is my_feature.";
		final String snapshot = "the feature branch is SNAPSHOT.";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				absent,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				absent,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				snapshot,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testSnapshotKeyword() {
		final String format = "version is a :SNAPSHOT:.";
		final String present = "version is a SNAPSHOT.";
		final String empty = "version is a .";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testTimestampKeyword() {
		final String format = "the timestamp is :TIMESTAMP:.";
		final String present = "the timestamp is 20121209.171545.";
		final String empty = "the timestamp is .";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testBuildnumberKeyword() {
		final String format = "the buildnumber is :BUILDNUMBER:.";
		final String present = "the buildnumber is 13.";
		final String empty = "the buildnumber is .";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testQualifiersKeyword() {
		final String format = "the qualifiers are :QUALIFIERS:.";
		final String present = "the qualifiers are some-other-qualifiers.";
		final String empty = "the qualifiers are .";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				present,
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				empty,
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testAllKeywords() {
		final String format = "|:MAJOR:|:MINOR:|:INCREMENTAL:|:FEATURE:"
				+ "|:SNAPSHOT:|:TIMESTAMP:|:BUILDNUMBER:|:QUALIFIERS:|";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				"|1|2|3|my_feature|||||",
				formatter.format(VersionFormatterTest.featureVersion));
		Assertions.assertEquals(
				"|1|2|3|my_feature|SNAPSHOT|"
					+ "20121209.171545|13|some-other-qualifiers|",
				formatter.format(VersionFormatterTest.fullVersion));
		Assertions.assertEquals(
				"|1|2|3|my_feature||||some-other-qualifiers|",
				formatter.format(VersionFormatterTest.qualifiersVersion));
		Assertions.assertEquals(
				"|1|2|3||||||",
				formatter.format(VersionFormatterTest.simpleVersion));
		Assertions.assertEquals(
				"|1|2|3||SNAPSHOT|20121209.171545|13||",
				formatter.format(VersionFormatterTest.snapshotVersion));
		Assertions.assertEquals(
				"|1|2|3|SNAPSHOT|||||",
				formatter.format(
						VersionFormatterTest.unparsedSnapshotVersion));
	}

	@Test
	public void testKeywordPrefix() {
		final String format = ":-present-MAJOR:-:absent-SNAPSHOT:";
		final VersionFormatter formatter = new VersionFormatter(format);
		Assertions.assertEquals(
				"-present-1-",
				formatter.format(VersionFormatterTest.simpleVersion));
	}

	@Test
	public void testUnknownKeyword() {
		final String format = "the qualifiers are :NOTAKEYWORD:.";
		Assertions.assertThrows(
				IllegalFormatFlagsException.class,
				() -> new VersionFormatter(format));
	}

	@Test
	public void testMissingColonFormat() {
		final String format = "this format: faulty";
		Assertions.assertThrows(
				IllegalFormatFlagsException.class,
				() -> new VersionFormatter(format));
	}
}
