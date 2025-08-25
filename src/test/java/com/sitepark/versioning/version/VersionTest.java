package com.sitepark.versioning.version;

import java.text.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionTest {

  private static final VersionParser PARSER = new VersionParser();

  @Test
  public void testIsSnapshot() throws ParseException {
    final Version release = VersionTest.PARSER.parseRelease("1.2");
    final Version snapshot = VersionTest.PARSER.parseBaseVersion("2.3-SNAPSHOT");
    final Version concreteSnapshot =
        VersionTest.PARSER.parseConcreteVersion("2.3-87654321.123456-7");
    Assertions.assertFalse(release.isSnapshot());
    Assertions.assertTrue(snapshot.isSnapshot());
    Assertions.assertTrue(concreteSnapshot.isSnapshot());
  }

  @Test
  public void testIsRelease() throws ParseException {
    final Version release = VersionTest.PARSER.parseRelease("1.2");
    final Version snapshot = VersionTest.PARSER.parseBaseVersion("2.3-SNAPSHOT");
    final Version concreteSnapshot =
        VersionTest.PARSER.parseConcreteVersion("2.3-87654321.123456-7");
    Assertions.assertTrue(release.isRelease());
    Assertions.assertFalse(snapshot.isRelease());
    Assertions.assertFalse(concreteSnapshot.isRelease());
  }

  @Test
  public void testMajorCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseRelease("1");
    final Version bigger = VersionTest.PARSER.parseRelease("3");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testMinorCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseRelease("1.1");
    final Version bigger = VersionTest.PARSER.parseRelease("1.3");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testIncrementalCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseRelease("1.1.1");
    final Version bigger = VersionTest.PARSER.parseRelease("1.1.3");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testFeatureBranchCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseRelease("1.1.1-featureA");
    final Version bigger = VersionTest.PARSER.parseRelease("1.1.1-featureE");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testDevelopBranchCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseRelease("1.1.1-Zfeature");
    final Version bigger = VersionTest.PARSER.parseRelease("1.1.1-develop");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
    Assertions.assertTrue(bigger.compareTo(bigger) == 0);
  }

  @Test
  public void testSnapshotCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-SNAPSHOT");
    final Version bigger = VersionTest.PARSER.parseBaseVersion("1.1.1-branch");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
    Assertions.assertTrue(bigger.compareTo(bigger) == 0);
  }

  @Test
  public void testQualifierValueCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-alpha-SNAPSHOT");
    final Version bigger = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-beta-SNAPSHOT");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testQualifierAmountCompareTo() throws ParseException {
    final Version smaller = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-alpha-beta-SNAPSHOT");
    final Version bigger = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-alpha-SNAPSHOT");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testConcreteSnapshotTimestampCompareTo() throws ParseException {
    final Version smaller =
        VersionTest.PARSER.parseConcreteVersion("1.1.1-branch-alpha-20210101.101010-13");
    final Version bigger =
        VersionTest.PARSER.parseConcreteVersion("1.1.1-branch-alpha-20210101.101027-13");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testConcreteSnapshotBuildnumberCompareTo() throws ParseException {
    final Version smaller =
        VersionTest.PARSER.parseConcreteVersion("1.1.1-branch-alpha-20210101.101027-13");
    final Version bigger =
        VersionTest.PARSER.parseConcreteVersion("1.1.1-branch-alpha-20210101.101027-16");
    Assertions.assertTrue(smaller.compareTo(bigger) < 0);
    Assertions.assertTrue(bigger.compareTo(smaller) > 0);
    Assertions.assertTrue(smaller.compareTo(smaller) == 0);
  }

  @Test
  public void testConcreteSnapshotAndSnapshotCompareTo() throws ParseException {
    final Version unconcrete = VersionTest.PARSER.parseBaseVersion("1.1.1-branch-alpha-SNAPSHOT");
    final Version concrete =
        VersionTest.PARSER.parseConcreteVersion("1.1.1-branch-alpha-20210101.101027-16");
    Assertions.assertTrue(unconcrete.compareTo(concrete) == 0);
    Assertions.assertTrue(concrete.compareTo(unconcrete) == 0);
  }
}
