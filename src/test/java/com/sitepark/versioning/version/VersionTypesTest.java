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
        0, VersionTypes.NONE.getTypes().size(), "VersionTypes.NONE sollte keine Types beeinhalten");
  }

  @Test
  public void testOnlyDevelopReleases() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.ONLY_DEVELOP_RELEASES.getBranchTypes(),
        "ONLY_DEVELOP_RELEASES sollte nur den BranchType DEVELOP beeinhalten");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.RELEASES),
        VersionTypes.ONLY_DEVELOP_RELEASES.getPublicationStatusTypes(),
        "ONLY_DEVELOP_RELEASES sollte nur den PublicationStatusType RELEASES beeinhalten");
  }

  @Test
  public void testOnlyDevelopSnapshots() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.ONLY_DEVELOP_SNAPSHOTS.getBranchTypes(),
        "ONLY_DEVELOP_SNAPSHOTS sollte nur den BranchType DEVELOP beeinhalten");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.SNAPSHOTS),
        VersionTypes.ONLY_DEVELOP_SNAPSHOTS.getPublicationStatusTypes(),
        "ONLY_DEVELOP_SNAPSHOTS sollte nur den PublicationStatusType SNAPSHOTS beeinhalten");
  }

  @Test
  public void testDevelopReleasesAndSnapshots() {
    Assertions.assertEquals(
        Set.of(BranchType.DEVELOP),
        VersionTypes.DEVELOP_RELEASES_AND_SNAPSHOTS.getBranchTypes(),
        "DEVELOP_RELEASES_AND_SNAPSHOTS sollte nur den BranchType DEVELOP beeinhalten");
    Assertions.assertEquals(
        Set.of( // Die Reihenfolge der Liste ist relevant!
            PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS),
        VersionTypes.DEVELOP_RELEASES_AND_SNAPSHOTS.getPublicationStatusTypes(),
        "DEVELOP_RELEASES_AND_SNAPSHOTS sollte die PublicationStatusTypes SNAPSHOTS und RELEASES"
            + " beeinhalten");
  }

  @Test
  public void testAll() {
    Assertions.assertEquals(
        Set.of( // Die Reihenfolge der Liste ist relevant!
            BranchType.DEVELOP, BranchType.FEATURES),
        VersionTypes.ALL.getBranchTypes(),
        "ALL sollte die BranchTypes DEVELOP und FEATURE beeinhalten");
    Assertions.assertEquals(
        Set.of( // Die Reihenfolge der Liste ist relevant!
            PublicationStatusType.RELEASES, PublicationStatusType.SNAPSHOTS),
        VersionTypes.ALL.getPublicationStatusTypes(),
        "ALL sollte die PublicationStatusTypes SNAPSHOTS und RELEASES beeinhalten");
  }

  @Test
  public void testIterableConstructor() {
    final Set<Type> types = Set.of(BranchType.FEATURES, PublicationStatusType.SNAPSHOTS);
    final VersionTypes versionTypes = new VersionTypes(types);
    Assertions.assertEquals(
        Set.of(BranchType.FEATURES),
        versionTypes.getBranchTypes(),
        "VersionTypes sollte nur den BranchType FEATURES enthalten");
    Assertions.assertEquals(
        Set.of(PublicationStatusType.SNAPSHOTS),
        versionTypes.getPublicationStatusTypes(),
        "VersionTypes sollte nur den PublicationStatusType SNAPSHOTS enthalten");
    Assertions.assertNotSame(
        types,
        versionTypes.getTypes(),
        "Die Liste der Types im Konstruktor sollte nicht per Referenz gehalten werden");
  }
}
