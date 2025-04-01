package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.text.ParseException;

/**
 * Implements the {@link ConcreteVersion}-parsing process.
 * Results are either {@link ConcreteSnapshotVersion}s or
 * {@link ReleaseVersion}s, depending on wether the last two {@code qualifiers}
 * are a {@code timestamp} of the format {@code yyyyMMdd.HHmmss} and a
 * {@code buildnumber}.
 * To be able to handle these two cases more elegantly they are wrapped in a
 * {@link PotentialSnapshotVersion} instance.
 *
 * <strong>Attention:</strong> this class is meant for a single execution and
 * therefore not thread safe!
 */
class PotentialConcreteSnapshotParseExecutor
    extends VersionParseExecutor<PotentialConcreteSnapshotVersion> {

  /**
   * A field to store a potential timestamp-qualifier.
   * When we encounter what looks like a timestamp-qualifier we have to look out
   * for a following builddate-qualifier (which also has to be the last) before
   * labeling the version a {@link ConcreteSnapshotVersion}.
   */
  private String timestampQualifier = null;

  /**
   * Wether or not a potential timestamp-qualifier (stored in
   * {@link #timestampQualifier} should be interpreted as the versions branch if
   * found not to be part of a {@link ConcreteSnapshotVersion}.
   */
  private boolean timestampQualifierWasBranch = true;

  PotentialConcreteSnapshotParseExecutor(final String string, final byte flags) {
    super(string, flags);
  }

  @Override
  protected PotentialConcreteSnapshotVersion buildVersion() {
    return this.versionBuilder.getConcreteSnapshotTimestamp().isPresent()
            && this.versionBuilder.getConcreteSnapshotBuildnumber().isPresent()
        ? PotentialConcreteSnapshotVersion.ofSnapshot(this.versionBuilder.buildConcreteSnapshot())
        : PotentialConcreteSnapshotVersion.ofRelease(this.versionBuilder.buildRelease());
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
          if (this.timestampQualifier != null) {
            if (this.timestampQualifierWasBranch) {
              this.addStoredTimestampAsBranch();
            } else {
              this.addStoredTimestampAsNormalQualifier();
            }
          }
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
        if (this.currentItemCouldBeTimestamp()) {
          this.storeCurrentItemAsTimestamp();
          this.timestampQualifierWasBranch = true;
        } else {
          this.addBranch();
        }
        break;
      case QUALIFIER:
        if (this.currentItemCouldBeTimestamp()) {
          this.storeCurrentItemAsTimestamp();
          this.timestampQualifierWasBranch = false;
          break;
        }
        if (this.timestampQualifier != null) {
          if (this.timestampQualifierWasBranch) {
            this.addStoredTimestampAsBranch();
          } else {
            this.addStoredTimestampAsNormalQualifier();
          }
        }
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
    if (this.timestampQualifier != null) {
      if (this.currentItemCouldBeBuildnumber()) {
        this.addTimestampBuildnumber();
        return;
      }
      if (this.timestampQualifierWasBranch) {
        this.addStoredTimestampAsBranch();
      } else {
        this.addStoredTimestampAsNormalQualifier();
      }
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
        this.addBranch();
        break;
      case QUALIFIER:
        this.addQualifier();
        break;
      default:
        this.fail();
    }
  }

  private void storeCurrentItemAsTimestamp() {
    this.timestampQualifier = this.currentItem;
    this.resetCurrentItem();
    this.currentSection = Section.QUALIFIER;
  }

  private void addTimestampBuildnumber() throws ParseException {
    try {
      this.versionBuilder.setConcreteSnapshotTimestamp(this.timestampQualifier);
      this.versionBuilder.setConcreteSnapshotBuildnumber(Integer.parseInt(this.currentItem));
    } catch (final NumberFormatException exception) {
      this.fail();
    }
    // we do not have to reset currentItem since there should not be any
    // other element after this.
  }

  private void addStoredTimestampAsNormalQualifier() {
    this.versionBuilder.addQualifier(this.timestampQualifier);
    this.timestampQualifier = null;
  }

  private void addStoredTimestampAsBranch() {
    this.versionBuilder.setBranch(new Branch(this.timestampQualifier));
    this.timestampQualifier = null;
  }

  private boolean currentItemCouldBeTimestamp() {
    // by far the fastest way
    return this.currentItem.length() == 15
        && Character.isDigit(this.currentItem.charAt(0))
        && Character.isDigit(this.currentItem.charAt(1))
        && Character.isDigit(this.currentItem.charAt(2))
        && Character.isDigit(this.currentItem.charAt(3))
        && Character.isDigit(this.currentItem.charAt(4))
        && Character.isDigit(this.currentItem.charAt(5))
        && Character.isDigit(this.currentItem.charAt(6))
        && Character.isDigit(this.currentItem.charAt(7))
        && this.currentItem.charAt(8) == '.'
        && Character.isDigit(this.currentItem.charAt(9))
        && Character.isDigit(this.currentItem.charAt(10))
        && Character.isDigit(this.currentItem.charAt(11))
        && Character.isDigit(this.currentItem.charAt(12))
        && Character.isDigit(this.currentItem.charAt(13))
        && Character.isDigit(this.currentItem.charAt(14));
  }

  private boolean currentItemCouldBeBuildnumber() {
    for (int i = this.currentItem.length() - 1; i >= 0; i--) {
      if (!Character.isDigit(this.currentItem.charAt(i))) {
        return false;
      }
    }
    return true;
  }
}
