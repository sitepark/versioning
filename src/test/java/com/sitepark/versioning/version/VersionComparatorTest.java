package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionComparatorTest {

  private static final Version SNAPSHOT_3_0_0_FEATURE_A_RC2 =
      new VersionBuilder()
          .setMajor(3)
          .setBranch(new Branch("feature_a"))
          .addQualifier("rc2")
          .buildConcreteSnapshot("20252608.145236", 2);
  private static final Version SNAPSHOT_3_0_0_FEATURE_B_RC1 =
      new VersionBuilder()
          .setMajor(3)
          .setBranch(new Branch("feature_b"))
          .addQualifier("rc1")
          .buildConcreteSnapshot("20252608.145232", 1);
  private static final Version SNAPSHOT_3_0_0 = new VersionBuilder().setMajor(3).buildSnapshot();
  private static final Version RELEASE_3_0_0_FEATURE_A =
      new VersionBuilder().setMajor(3).setBranch(new Branch("feature_a")).buildRelease();
  private static final Version RELEASE_3_0_0 = new VersionBuilder().setMajor(3).buildRelease();
  private static final Version SNAPSHOT_3_0_1 =
      new VersionBuilder().setMajor(3).setIncremental(1).buildSnapshot();
  private static final Version RELEASE_3_0_2 =
      new VersionBuilder().setMajor(3).setIncremental(2).buildRelease();
  private static final Version RELEASE_3_2_0 =
      new VersionBuilder().setMajor(3).setMinor(2).buildRelease();
  private static final Version RELEASE_3_2_1 =
      new VersionBuilder().setMajor(3).setMinor(2).setIncremental(1).buildRelease();
  private static final Version SNAPSHOT_4_0_0 = new VersionBuilder().setMajor(4).buildSnapshot();

  @Test
  public void testNatualOrdering() {
    final List<Version> expected =
        List.of(
            SNAPSHOT_3_0_0_FEATURE_A_RC2,
            SNAPSHOT_3_0_0_FEATURE_B_RC1,
            SNAPSHOT_3_0_0,
            RELEASE_3_0_0_FEATURE_A,
            RELEASE_3_0_0,
            SNAPSHOT_3_0_1,
            RELEASE_3_0_2,
            RELEASE_3_2_0,
            RELEASE_3_2_1,
            SNAPSHOT_4_0_0);
    final List<Version> result =
        Stream.<Version>of(
                SNAPSHOT_4_0_0,
                RELEASE_3_2_1,
                RELEASE_3_2_0,
                RELEASE_3_0_2,
                SNAPSHOT_3_0_1,
                RELEASE_3_0_0,
                RELEASE_3_0_0_FEATURE_A,
                SNAPSHOT_3_0_0,
                SNAPSHOT_3_0_0_FEATURE_B_RC1,
                SNAPSHOT_3_0_0_FEATURE_A_RC2)
            .sorted(VersionComparator.builder().build())
            .toList();
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void testReversedOrdering() {
    final List<Version> expected =
        List.of(
            SNAPSHOT_4_0_0,
            RELEASE_3_2_1,
            RELEASE_3_2_0,
            RELEASE_3_0_2,
            SNAPSHOT_3_0_1,
            RELEASE_3_0_0,
            RELEASE_3_0_0_FEATURE_A,
            SNAPSHOT_3_0_0,
            SNAPSHOT_3_0_0_FEATURE_B_RC1,
            SNAPSHOT_3_0_0_FEATURE_A_RC2);
    final List<Version> result =
        Stream.<Version>of(
                SNAPSHOT_3_0_0_FEATURE_A_RC2,
                SNAPSHOT_3_0_0_FEATURE_B_RC1,
                SNAPSHOT_3_0_0,
                RELEASE_3_0_0_FEATURE_A,
                RELEASE_3_0_0,
                SNAPSHOT_3_0_1,
                RELEASE_3_0_2,
                RELEASE_3_2_0,
                RELEASE_3_2_1,
                SNAPSHOT_4_0_0)
            .sorted(VersionComparator.builder().reverse().build())
            .toList();
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void testIgnoredBranches() {
    final List<Version> expected =
        List.of(
            SNAPSHOT_3_0_0,
            SNAPSHOT_3_0_0_FEATURE_B_RC1,
            SNAPSHOT_3_0_0_FEATURE_A_RC2,
            RELEASE_3_0_0,
            RELEASE_3_0_0_FEATURE_A,
            SNAPSHOT_3_0_1,
            RELEASE_3_0_2,
            RELEASE_3_2_0,
            RELEASE_3_2_1,
            SNAPSHOT_4_0_0);
    final List<Version> result =
        Stream.<Version>of(
                SNAPSHOT_4_0_0,
                RELEASE_3_2_1,
                RELEASE_3_2_0,
                RELEASE_3_0_2,
                SNAPSHOT_3_0_1,
                RELEASE_3_0_0,
                RELEASE_3_0_0_FEATURE_A,
                SNAPSHOT_3_0_0,
                SNAPSHOT_3_0_0_FEATURE_A_RC2,
                SNAPSHOT_3_0_0_FEATURE_B_RC1)
            .sorted(VersionComparator.builder().ignoreBranches().build())
            .toList();
    Assertions.assertEquals(expected, result);
  }

  @Test
  public void testIgnoredBranchesAndQualifiers() {
    final List<Version> expected =
        List.of(
            SNAPSHOT_3_0_0,
            SNAPSHOT_3_0_0_FEATURE_B_RC1,
            SNAPSHOT_3_0_0_FEATURE_A_RC2,
            RELEASE_3_0_0,
            RELEASE_3_0_0_FEATURE_A,
            SNAPSHOT_3_0_1,
            RELEASE_3_0_2,
            RELEASE_3_2_0,
            RELEASE_3_2_1,
            SNAPSHOT_4_0_0);
    final List<Version> result =
        Stream.<Version>of(
                SNAPSHOT_4_0_0,
                RELEASE_3_2_1,
                RELEASE_3_2_0,
                RELEASE_3_0_2,
                SNAPSHOT_3_0_1,
                RELEASE_3_0_0,
                RELEASE_3_0_0_FEATURE_A,
                SNAPSHOT_3_0_0,
                SNAPSHOT_3_0_0_FEATURE_A_RC2,
                SNAPSHOT_3_0_0_FEATURE_B_RC1)
            .sorted(VersionComparator.builder().ignoreBranches().ignoreQualifiers().build())
            .toList();
    Assertions.assertEquals(expected, result);
  }
}
