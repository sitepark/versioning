package com.sitepark.versioning.version;

import java.text.ParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sitepark.versioning.Branch;

public class VersionParserTest {

	public static final VersionParser PARSER = new VersionParser();

	// ----- parseRelease -----

	@Test
	public void testFullRelease() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2.3-branch-qualifierA-qualifierB");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutBranch() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2.3");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, version.getBranch());
		Assertions.assertEquals(0, version.getQualifiers().size());
	}

	@Test
	public void testFullDevelopRelease() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2.3-develop-qualifierA-qualifierB");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutIncremental() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2-branch-qualifierA-qualifierB");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(0, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMinor() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1..3-branch-qualifierA-qualifierB");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(0, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMinorAndIncremental() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1-branch-qualifierA-qualifierB");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(0, version.getMinor());
		Assertions.assertEquals(0, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMajor() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				".2.3-branch-qualifierA-qualifierB");
		Assertions.assertEquals(0, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMajorAndMinor() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"..3-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertEquals(0, version.getMajor());
		Assertions.assertEquals(0, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMajorAndIncremental() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				".2.-branch-qualifierA-qualifierB");
		Assertions.assertEquals(0, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(0, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithoutMajorAndMinorAndIncremental()
			throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"..-branch-qualifierA-qualifierB");
		Assertions.assertEquals(0, version.getMajor());
		Assertions.assertEquals(0, version.getMinor());
		Assertions.assertEquals(0, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
	}

	@Test
	public void testReleaseWithTimestampBuildnumberLikeQualifiers()
			throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2.3-20210101.131313-123");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(
				new Branch("20210101.131313"),
				version.getBranch());
		Assertions.assertEquals("123", version.getQualifiers().get(0));
	}

	@Test
	public void testReleaseWithSnapshotLikeQualifier() throws ParseException {
		final ReleaseVersion version = VersionParserTest.PARSER.parseRelease(
				"1.2.3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertEquals(1, version.getMajor());
		Assertions.assertEquals(2, version.getMinor());
		Assertions.assertEquals(3, version.getIncremental());
		Assertions.assertEquals(new Branch("branch"), version.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				version.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				version.getQualifiers().get(1));
		Assertions.assertEquals("SNAPSHOT", version.getQualifiers().get(2));
	}

	@Test
	public void testReleaseVersionEndsWithHyphon() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifierB-"));
	}

	@Test
	public void testReleaseVersionWithEmptyQualifier() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA--qualifierB"));
	}

	@Test
	public void testReleaseVersionWithNonNumericalMajor()
			throws ParseException {
	Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"a.2.3-branch-qualifierA-qualifierB"));
	}

	@Test
	public void testReleaseVersionWithNonNumericalMinor()
			throws ParseException {
	Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.b.3-branch-qualifierA-qualifierB"));
	}

	@Test
	public void testReleaseVersionWithNonNumericalIncremental()
			throws ParseException {
	Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.c-branch-qualifierA-qualifierB"));
	}

	@Test
	public void testReleaseVersionEmptyString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(""));
	}

	@Test
	public void testReleaseVersionTextString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease("someText"));
	}

	@Test
	public void testReleaseVersionWithWhitespace() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier B"));
	}

	@Test
	public void testReleaseVersionWithNewLine() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier\nB"));
	}

	@Test
	public void testReleaseVersionWithTab() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier\tB"));
	}

	@Test
	public void testReleaseVersionWithLiteralTab() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier	B"));
	}

	@Test
	public void testReleaseVersionWithReturn() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier\rB"));
	}

	@Test
	public void testReleaseVersionWithBackspace() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier\bB"));
	}

	@Test
	public void testReleaseVersionWithNullTerminator() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parseRelease(
						"1.2.3-branch-qualifierA-qualifier\0B"));
	}

	// ----- parsePotentialConcreteSnapshot -----

	@Test
	public void testConcreteFullSnapshot() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals("qualifierA", snapshot.getQualifiers().get(0));
		Assertions.assertEquals("qualifierB", snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutBranch() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot("1.2.3-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(0, snapshot.getQualifiers().size());
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteFullDevelopSnapshot() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-develop-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutIncremental() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1.2-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMinor() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1..3-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMinorAndIncremental()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMajor()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					".2.3-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMajorAndMinor()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"..3-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMajorAndIncremental()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					".2.-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithoutMajorAndMinorAndIncremental()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"..-branch-qualifierA-qualifierB-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithOnlyTimestampBuildnumber()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot("..-20210101.131313-123");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(0, snapshot.getQualifiers().size());
		Assertions.assertEquals("20210101.131313", snapshot.getTimestamp());
		Assertions.assertEquals(123, snapshot.getBuildnumber());
	}

	@Test
	public void testConcreteSnapshotWithTimestampBuildnumberLikeQualifiers()
			throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-20210101.131313-123-20210202.101112-4");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final ConcreteSnapshotVersion snapshot
				= version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(
				new Branch("20210101.131313"),
				snapshot.getBranch());
		Assertions.assertEquals("123", snapshot.getQualifiers().get(0));
		Assertions.assertEquals("20210202.101112", snapshot.getTimestamp());
		Assertions.assertEquals(4, snapshot.getBuildnumber());
	}

	@Test
	public void testSnapshotParsedAsConcreteSnapshot() throws ParseException {
		final PotentialConcreteSnapshotVersion version
				= VersionParserTest.PARSER
			.parsePotentialConcreteSnapshot(
					"1.2.3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertFalse(version.isSnapshot());
		Assertions.assertTrue(version.isRelease());
		final ReleaseVersion release = version.getReleaseOrElse(null);
		Assertions.assertEquals(1, release.getMajor());
		Assertions.assertEquals(2, release.getMinor());
		Assertions.assertEquals(3, release.getIncremental());
		Assertions.assertEquals(new Branch("branch"), release.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				release.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				release.getQualifiers().get(1));
		Assertions.assertEquals("SNAPSHOT", release.getQualifiers().get(2));
	}

	@Test
	public void testConcreteSnapshotVersionEndsWithHyphon()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifierB"
							+ "-20210101.131313-123-"));
	}

	@Test
	public void testConcreteSnapshotVersionWithEmptyQualifier()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA--qualifierB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithNonNumericalMajor()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"a.2.3-branch-qualifierA-qualifierB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithNonNumericalMinor()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.b.3-branch-qualifierA-qualifierB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithNonNumericalIncremental()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.c-branch-qualifierA-qualifierB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionEmptyString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER
					.parsePotentialConcreteSnapshot(""));
	}

	@Test
	public void testConcreteSnapshotVersionTextString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER
					.parsePotentialConcreteSnapshot("someText"));
	}

	@Test
	public void testConcreteSnapshotVersionWithWhitespace()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier B"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithNewLine() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier\nB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithTab() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier\tB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithLiteralTab()
			throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier	B"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithReturn() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier\rB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithBackspace()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier\bB"
							+ "-20210101.131313-123"));
	}

	@Test
	public void testConcreteSnapshotVersionWithNullTerminator()
				throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialConcreteSnapshot(
						"1.2.3-branch-qualifierA-qualifier\0B"
							+ "-20210101.131313-123"));
	}

	// ----- parsePotentialSnapshot -----

	@Test
	public void testFullSnapshot() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"1.2.3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutBranch() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot("1.2.3-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(0, snapshot.getQualifiers().size());
	}

	@Test
	public void testFullDevelopSnapshot() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"1.2.3-develop-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutIncremental() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"1.2-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMinor() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"1..3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMinorAndIncremental() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"1-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMajor() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					".2.3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMajorAndMinor() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"..3-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMajorAndIncremental() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					".2.-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithoutMajorAndMinorAndIncremental()
			throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot(
					"..-branch-qualifierA-qualifierB-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("branch"), snapshot.getBranch());
		Assertions.assertEquals(
				"qualifierA",
				snapshot.getQualifiers().get(0));
		Assertions.assertEquals(
				"qualifierB",
				snapshot.getQualifiers().get(1));
	}

	@Test
	public void testSnapshotWithOnlySnapshotQualifier() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot("..-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(0, snapshot.getMajor());
		Assertions.assertEquals(0, snapshot.getMinor());
		Assertions.assertEquals(0, snapshot.getIncremental());
		Assertions.assertEquals(Branch.DEVELOP, snapshot.getBranch());
		Assertions.assertEquals(0, snapshot.getQualifiers().size());
	}

	@Test
	public void testSnapshotWithSnapshotLikeQualifier() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot("1.2.3-SNAPSHOT-SNAPSHOT");
		Assertions.assertTrue(version.isSnapshot());
		Assertions.assertFalse(version.isRelease());
		final SnapshotVersion snapshot = version.getSnapshotOrElse(null);
		Assertions.assertEquals(1, snapshot.getMajor());
		Assertions.assertEquals(2, snapshot.getMinor());
		Assertions.assertEquals(3, snapshot.getIncremental());
		Assertions.assertEquals(new Branch("SNAPSHOT"), snapshot.getBranch());
		Assertions.assertEquals(0, snapshot.getQualifiers().size());
	}

	@Test
	public void testParseSnapshotBranch() throws ParseException {
		final PotentialSnapshotVersion version = VersionParserTest.PARSER
			.parsePotentialSnapshot("1.2.3-SNAPSHOT-qualifier");
		Assertions.assertFalse(version.isSnapshot());
		Assertions.assertTrue(version.isRelease());
		final ReleaseVersion release = version.getReleaseOrElse(null);
		Assertions.assertEquals(1, release.getMajor());
		Assertions.assertEquals(2, release.getMinor());
		Assertions.assertEquals(3, release.getIncremental());
		Assertions.assertEquals(new Branch("SNAPSHOT"), release.getBranch());
		Assertions.assertEquals(1, release.getQualifiers().size());
		Assertions.assertEquals("qualifier", release.getQualifiers().get(0));
	}

	@Test
	public void testSnapshotVersionEndsWithHyphon() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifierB-SNAPSHOT-"));
	}

	@Test
	public void testSnapshotVersionWithEmptyQualifier() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA--qualifierB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithNonNumericalMajor()
			throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"a.2.3-branch-qualifierA-qualifierB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithNonNumericalMinor()
			throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.b.3-branch-qualifierA-qualifierB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithNonNumericalIncremental()
			throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.c-branch-qualifierA-qualifierB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionEmptyString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(""));
	}

	@Test
	public void testSnapshotVersionTextString() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"someText"));
	}

	@Test
	public void testSnapshotVersionWithWhitespace() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier B-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithNewLine() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier\nB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithTab() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier\tB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithLiteralTab() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier	B-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithReturn() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier\rB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithBackspace() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier\bB-SNAPSHOT"));
	}

	@Test
	public void testSnapshotVersionWithNullTerminator() throws ParseException {
		Assertions.assertThrows(
				ParseException.class,
				() -> VersionParserTest.PARSER.parsePotentialSnapshot(
						"1.2.3-branch-qualifierA-qualifier\0B-SNAPSHOT"));
	}
}
