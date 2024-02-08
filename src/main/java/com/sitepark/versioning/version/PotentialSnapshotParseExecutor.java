package com.sitepark.versioning.version;

import java.text.ParseException;

/**
 * Implements the {@link ConcreteVersion}-parsing process.
 * Results are either {@link ConcreteSnapshotVersion}s or
 * {@link ReleaseVersion}s, depending on wether the last two {@code qualifiers}
 * are a {@code timestamp} of the format {@code yyyyMMdd.HHmmss} and a
 * {@code buildnumber}.  To be able to handle these two cases more elegantly
 * they are wrapped in a {@link PotentialConcreteSnapshotVersion} instance.
 *
 * <strong>Attention:</strong> this class is meant for a single execution and
 * therefore not thread safe!
 */
class PotentialSnapshotParseExecutor extends VersionParseExecutor<PotentialSnapshotVersion> {

  private boolean hasSnapshotQualifier = false;

  PotentialSnapshotParseExecutor(final String string, final byte flags) {
    super(string, flags);
  }

  /**
   * Erstellt ein {@link PotentialSnapshotVersion} Objekt mit
   * einer {@link SnapshotVersion}, wenn ein
   * <em>"SNAPSHOT"</em> Qualifier beim Parsen gefunden wurde
   * oder einer {@link ReleaseVersion}, wenn nicht.
   */
  @Override
  protected PotentialSnapshotVersion buildVersion() {
    return this.hasSnapshotQualifier
        ? PotentialSnapshotVersion.ofSnapshot(this.versionBuilder.buildSnapshot())
        : PotentialSnapshotVersion.ofRelease(this.versionBuilder.buildRelease());
  }

  @Override
  protected void handleDot() throws ParseException {
    switch (this.currentSection) {
      case MAJOR:
        this.addMajor();
        if (this.isLastChar) {
          this.addMinor();
          this.addIncremental();
        }
        break;
      case MINOR:
        this.addMinor();
        if (this.isLastChar) {
          this.addIncremental();
        }
        break;
      case INCREMENTAL:
        this.fail();
        break;
      case BRANCH:
        this.appendCharToCurrentItem();
        if (this.isLastChar) {
          this.addBranch();
        }
        break;
      case QUALIFIER:
        this.appendCharToCurrentItem();
        if (this.isLastChar) {
          this.addQualifier();
        }
        break;
      default:
        this.fail();
    }
  }

  @Override
  protected void handleHyphen() throws ParseException {
    if (this.isLastChar) {
      this.fail();
    }
    switch (this.currentSection) {
      case MAJOR:
        this.addMajor();
      case MINOR:
        this.addMinor();
      case INCREMENTAL:
        this.addIncremental();
        break;
      case BRANCH:
        if (this.currentItemLength == 0) {
          this.fail();
        }
        this.addBranch();
        break;
      case QUALIFIER:
        if (this.currentItemLength == 0) {
          this.fail();
        }
        this.addQualifier();
        break;
      default:
        this.fail();
    }
  }

  @Override
  protected void handleNormalChar() throws ParseException {
    this.appendCharToCurrentItem();
    if (!this.isLastChar) {
      return;
    }
    switch (this.currentSection) {
      case MAJOR:
        this.addMajor();
      case MINOR:
        this.addMinor();
      case INCREMENTAL:
        this.addIncremental();
        break;
      case BRANCH:
        if (this.currentItem.equals("SNAPSHOT")) {
          this.addSnapshot();
        } else {
          this.addBranch();
        }
        break;
      case QUALIFIER:
        if (this.currentItem.equals("SNAPSHOT")) {
          this.addSnapshot();
        } else {
          this.addQualifier();
        }
        break;
      default:
        this.fail();
    }
  }

  private void addSnapshot() {
    this.hasSnapshotQualifier = true;
    // we do not have to reset currentItem since there should not be any
    // other element after this.
  }
}
