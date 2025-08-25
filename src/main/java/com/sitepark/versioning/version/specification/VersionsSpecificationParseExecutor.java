package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.version.BaseVersion;
import com.sitepark.versioning.version.VersionParser;
import com.sitepark.versioning.version.specification.element.ElementsIntersectException;
import com.sitepark.versioning.version.specification.element.boundary.Boundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.ExclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InclusiveUpperBoundary;
import com.sitepark.versioning.version.specification.element.boundary.InvalidBoundariesException;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedLowerBoundary;
import com.sitepark.versioning.version.specification.element.boundary.UnlimitedUpperBoundary;
import java.text.ParseException;

/**
 * Implements the {@link VersionsSpecification}-parsing process.
 *
 * <strong>Attention:</strong> this class is meant for a single execution and
 * therefore not thread safe!
 */
class VersionsSpecificationParseExecutor {

  /**
   * The {@link VersionParser} used to parse all {@link BaseVersion}s
   * encountered.
   *
   * {@code Qualififiers} are ignored to prevent potentially
   * unintended behaviour when comparing {@link Boundary}s.
   */
  private static final VersionParser VERSION_PARSER =
      new VersionParser(VersionParser.Characteristics.IGNORE_QUALIFIERS);

  /**
   * The section of a {@link VersionsSpecification}-String this parser may be
   * in.
   */
  private enum Section {
    BEFORE_ELEMENT,
    RANGE_FROM,
    RANGE_TO,
    SINGLE_VERSION,
    AFTER_ELEMENT;
  }

  private final String string;
  private final int maxIndex;
  private final VersionsSpecificationBuilder builder;

  private char currentChar;
  private boolean isLastChar;
  private Section section = Section.BEFORE_ELEMENT;
  private int index = -1;

  private String currentUpperVersion = "";
  private int currentUpperVersionLength = 0;
  private String currentLowerVersion = "";
  private int currentLowerVersionLength = 0;
  private boolean currentRangeIsStartInclusive = true;
  private boolean currentRangeIsEndInclusive = true;

  public VersionsSpecificationParseExecutor(final String string) {
    this.string = string;
    this.maxIndex = string.length() - 1;
    this.builder = new VersionsSpecificationBuilder();
  }

  /**
   * Executes the parsing process of the String specified in the constructor.
   *
   * @throws ParseException if the String is not compliant with the required
   *                        format
   * @throws InvalidBoundariesException if the boundaries of a range are invalid
   * @throws ElementsIntersectException if parsed elements intersect
   */
  public VersionsSpecification execute() throws ParseException, InvalidBoundariesException {
    if (this.maxIndex == -1) {
      throw new ParseException(this.string, this.index);
    }
    do {
      this.isLastChar = ++this.index == this.maxIndex;
      this.currentChar = this.string.charAt(this.index);
      this.step();
    } while (!this.isLastChar);
    return this.build();
  }

  /**
   * Consumes a single char
   */
  private void step() throws ParseException, InvalidBoundariesException {
    switch (this.currentChar) {
      case ' ':
        // ignore spaces
        break;
      case '(':
        this.failIfInSection(Section.BEFORE_ELEMENT);
        this.currentRangeIsStartInclusive = false;
        this.section = Section.RANGE_FROM;
        break;
      case '[':
        this.failIfInSection(Section.BEFORE_ELEMENT);
        this.section = Section.RANGE_FROM;
        break;
      case ')':
        this.failIfInSection(Section.RANGE_TO);
        this.currentRangeIsEndInclusive = false;
        this.addVersionRange();
        this.section = Section.AFTER_ELEMENT;
        break;
      case ']':
        this.failIfInSection(Section.RANGE_TO);
        this.addVersionRange();
        this.section = Section.AFTER_ELEMENT;
        break;
      case ',':
        this.handleComma();
        break;
      default:
        this.handleNormalChar();
    }
  }

  private void handleComma() throws ParseException {
    switch (this.section) {
      case BEFORE_ELEMENT:
      case RANGE_TO:
        this.fail();
        break;
      case RANGE_FROM:
        this.section = Section.RANGE_TO;
        break;
      case SINGLE_VERSION:
        this.addSingleVersion();
      case AFTER_ELEMENT:
        this.resetValues();
        this.section = Section.BEFORE_ELEMENT;
        break;
      default:
        this.fail();
    }
  }

  private void handleNormalChar() throws ParseException {
    switch (this.section) {
      case BEFORE_ELEMENT:
        this.section = Section.SINGLE_VERSION;
      case SINGLE_VERSION:
        this.appendCurrentLowerVersion();
        if (this.isLastChar) {
          this.addSingleVersion();
        }
        break;
      case AFTER_ELEMENT:
        this.fail();
        break;
      case RANGE_FROM:
        this.appendCurrentLowerVersion();
        break;
      case RANGE_TO:
        this.appendCurrentUpperVersion();
        break;
      default:
        this.fail();
    }
  }

  private void failIfInSection(final Section section) throws ParseException {
    if (this.section != section) {
      this.fail();
    }
  }

  private void fail() throws ParseException {
    throw new ParseException(this.string, this.index);
  }

  private void appendCurrentLowerVersion() {
    this.currentLowerVersion += this.currentChar;
    this.currentLowerVersionLength += 1;
  }

  private void appendCurrentUpperVersion() {
    this.currentUpperVersion += this.currentChar;
    this.currentUpperVersionLength += 1;
  }

  private void addVersionRange() throws ParseException, InvalidBoundariesException {
    if (this.currentLowerVersionLength == 0) {
      if (this.currentUpperVersionLength == 0) {
        throw new InvalidBoundariesException(
            "VersionRanges have to have atleast one limiting Boundary");
      }
      this.parseOnlyUpperBoundary();
    } else if (this.currentUpperVersionLength == 0) {
      this.parseOnlyLowerBoundary();
    } else {
      this.parseBothBoundaries();
    }
  }

  private void addSingleVersion() throws ParseException {
    if (this.currentLowerVersionLength == 0) {
      this.fail();
    }
    this.builder.addExplicitVersion(
        VersionsSpecificationParseExecutor.VERSION_PARSER.parseBaseVersion(
            this.currentLowerVersion));
  }

  private void resetValues() {
    this.currentUpperVersion = "";
    this.currentUpperVersionLength = 0;
    this.currentLowerVersion = "";
    this.currentLowerVersionLength = 0;
    this.currentRangeIsStartInclusive = true;
    this.currentRangeIsEndInclusive = true;
  }

  private VersionsSpecification build() {
    return new VersionsSpecification(this.builder);
  }

  private void parseOnlyUpperBoundary() throws ParseException {
    final BaseVersion version =
        VersionsSpecificationParseExecutor.VERSION_PARSER.parseBaseVersion(
            this.currentUpperVersion);
    this.builder.addVersionRange(
        new UnlimitedLowerBoundary(),
        this.currentRangeIsEndInclusive
            ? new InclusiveUpperBoundary(version)
            : new ExclusiveUpperBoundary(version));
  }

  private void parseOnlyLowerBoundary() throws ParseException {
    final BaseVersion version =
        VersionsSpecificationParseExecutor.VERSION_PARSER.parseBaseVersion(
            this.currentLowerVersion);
    this.builder.addVersionRange(
        this.currentRangeIsStartInclusive
            ? new InclusiveLowerBoundary(version)
            : new ExclusiveLowerBoundary(version),
        new UnlimitedUpperBoundary());
  }

  private void parseBothBoundaries() throws ParseException {
    final BaseVersion lowerVersion =
        VersionsSpecificationParseExecutor.VERSION_PARSER.parseBaseVersion(
            this.currentLowerVersion);
    final BaseVersion upperVersion =
        VersionsSpecificationParseExecutor.VERSION_PARSER.parseBaseVersion(
            this.currentUpperVersion);
    this.builder.addVersionRange(
        this.currentRangeIsStartInclusive
            ? new InclusiveLowerBoundary(lowerVersion)
            : new ExclusiveLowerBoundary(lowerVersion),
        this.currentRangeIsEndInclusive
            ? new InclusiveUpperBoundary(upperVersion)
            : new ExclusiveUpperBoundary(upperVersion));
  }
}
