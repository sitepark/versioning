package com.sitepark.versioning.version;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatedBaseVersionTest {

  private static final LocalDateTime DATE =
      LocalDateTime.of(
          2021, // year
          4, // month
          15, // day
          13, // hour
          4, // minute
          46, // second
          190721000); // nanosecond

  private static final SnapshotVersion VERSION =
      new VersionBuilder().setMajor(2).setMinor(4).setIncremental(3).buildSnapshot();

  @Test
  public void testCompareToEquals() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion equal =
        new DatedBaseVersion(
            DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.plusDays(1).minusDays(1));
    Assertions.assertTrue(version.compareTo(equal) == 0);
  }

  @Test
  public void testCompareToLater() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion later =
        new DatedBaseVersion(
            DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.plusMinutes(13));
    Assertions.assertTrue(version.compareTo(later) < 0);
  }

  @Test
  public void testCompareToEarlier() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion earlier =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.minusDays(2));
    Assertions.assertTrue(version.compareTo(earlier) > 0);
  }

  @Test
  public void testCompareToLager() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion larger =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION.toRelease(), DatedBaseVersionTest.DATE);
    Assertions.assertTrue(version.compareTo(larger) < 0);
  }

  @Test
  public void testCompareToSmaller() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion smaller =
        new DatedBaseVersion(
            new VersionBuilder()
                .setMajor(version.version().getMajor())
                .setMinor(version.version().getMinor())
                .setIncremental(version.version().getIncremental() - 2)
                .buildSnapshot(),
            DatedBaseVersionTest.DATE);
    Assertions.assertTrue(version.compareTo(smaller) > 0);
  }

  @Test
  public void testEqualsSameVersion() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion sameVersion =
        new DatedBaseVersion(
            DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.plusDays(1).minusDays(1));
    Assertions.assertEquals(version, sameVersion);
  }

  @Test
  public void testEqualsSameDate() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion sameDate =
        new DatedBaseVersion(
            new VersionBuilder()
                .setMajor(version.version().getMajor())
                .setMinor(version.version().getMinor())
                .setIncremental(version.version().getIncremental())
                .buildSnapshot(),
            DatedBaseVersionTest.DATE);
    Assertions.assertEquals(version, sameDate);
  }

  @Test
  public void testEqualsPlusDayMinusDay() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion equal =
        new DatedBaseVersion(
            DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.plusDays(1).minusDays(1));
    Assertions.assertEquals(version, equal);
  }

  @Test
  public void testNotEqualsLater() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion later =
        new DatedBaseVersion(
            DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.plusMinutes(13));
    Assertions.assertNotEquals(version, later);
  }

  @Test
  public void testNotEqualsEarlier() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion earlier =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE.minusDays(2));
    Assertions.assertNotEquals(version, earlier);
  }

  @Test
  public void testNotEqualsLarger() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion larger =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION.toRelease(), DatedBaseVersionTest.DATE);
    Assertions.assertNotEquals(version, larger);
  }

  @Test
  public void testNotEqualsSmaller() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    final DatedBaseVersion smaller =
        new DatedBaseVersion(
            new VersionBuilder()
                .setMajor(version.version().getMajor())
                .setMinor(version.version().getMinor())
                .setIncremental(version.version().getIncremental() - 2)
                .buildSnapshot(),
            DatedBaseVersionTest.DATE);
    Assertions.assertNotEquals(version, smaller);
  }

  @Test
  public void testAsUndated() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    Assertions.assertEquals(DatedBaseVersionTest.VERSION, version.version());
  }

  @Test
  public void testToString() {
    final DatedBaseVersion version =
        new DatedBaseVersion(DatedBaseVersionTest.VERSION, DatedBaseVersionTest.DATE);
    Assertions.assertEquals(
        DatedBaseVersionTest.VERSION.toString() + "<2021-04-15T13:04:46.190721000>",
        version.toString());
  }
}
