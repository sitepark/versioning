package com.sitepark.versioning.version;

import com.sitepark.versioning.version.VersionTypes.BranchType;
import com.sitepark.versioning.version.VersionTypes.PublicationStatusType;
import com.sitepark.versioning.version.VersionTypes.Type;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionTypesTest {

  @Test
  public void testNone() {
    Assertions.assertEquals(
        0, VersionTypes.NONE.getTypes().size(), "VersionTypes.NONE should not contain any types");
  }

  @Test
  public void testOnlyDevelopReleases() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.ONLY_DEVELOP_RELEASES.getBranchTypes(),
        "ONLY_DEVELOP_RELEASES should only contain BranchType.DEVELOP");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.RELEASES),
        VersionTypes.ONLY_DEVELOP_RELEASES.getPublicationStatusTypes(),
        "ONLY_DEVELOP_RELEASES should only contain PublicationStatusType.RELEASES");
  }

  @Test
  public void testOnlyDevelopSnapshots() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.ONLY_DEVELOP_SNAPSHOTS.getBranchTypes(),
        "ONLY_DEVELOP_SNAPSHOTS should only contain BranchType.DEVELOP");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.SNAPSHOTS),
        VersionTypes.ONLY_DEVELOP_SNAPSHOTS.getPublicationStatusTypes(),
        "ONLY_DEVELOP_SNAPSHOTS should only contain PublicationStatusType.SNAPSHOTS");
  }

  @Test
  public void testDevelopReleasesAndSnapshots() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.DEVELOP_RELEASES_AND_SNAPSHOTS.getBranchTypes(),
        "DEVELOP_RELEASES_AND_SNAPSHOTS should only contain BranchType.DEVELOP");
    Assertions.assertEquals(
        // order matters!
        Set.of(PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS),
        VersionTypes.DEVELOP_RELEASES_AND_SNAPSHOTS.getPublicationStatusTypes(),
        "DEVELOP_RELEASES_AND_SNAPSHOTS should contain PublicationStatusType.SNAPSHOTS and"
            + " RELEASES");
  }

  @Test
  public void testAll() {
    Assertions.assertEquals(
        // order matters!
        Set.of(BranchType.DEVELOP, BranchType.FEATURES),
        VersionTypes.ALL.getBranchTypes(),
        "ALL should contain BranchTypes.DEVELOP and FEATURE");
    Assertions.assertEquals(
        // order matters!
        Set.of(PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS),
        VersionTypes.ALL.getPublicationStatusTypes(),
        "ALL should contain PublicationStatusType.SNAPSHOTS and RELEASES");
  }

  @Test
  public void testIterableConstructor() {
    final Set<Type> types = Set.of(BranchType.FEATURES, PublicationStatusType.SNAPSHOTS);
    final VersionTypes versionTypes = new VersionTypes(types);
    Assertions.assertEquals(
        Set.of(BranchType.FEATURES),
        versionTypes.getBranchTypes(),
        "VersionTypes should only contain BranchType.FEATURES");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.SNAPSHOTS),
        versionTypes.getPublicationStatusTypes(),
        "VersionTypes should only contain PublicationStatusType.SNAPSHOTS");
    Assertions.assertNotSame(
        types,
        versionTypes.getTypes(),
        "the types constructed with should not be held by reference");
  }
}
