package com.sitepark.versioning.version;

import com.sitepark.versioning.Branch;
import java.util.ArrayList;
import java.util.IllegalFormatFlagsException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A class that can format {@link Version Versions}.
 * The format is given as a String where keywords can be used to insert the
 * corresponding values of a Version. These keywords must be formatted as
 * follows:
 * <pre>:"{prefix}{keyword}:"</pre>
 * The prefix is optional and will only be output if a value is created by the
 * keyword.
 *
 * <p>
 * These are the available keywords:
 * <ul>
 *   <li>{@code MAJOR}: the major version</li>
 *   <li>{@code MINOR}: the minor version</li>
 *   <li>{@code INCREMENTAL}: the incremental version</li>
 *   <li>{@code FEATURE}: the branch if it is a feature-branch</li>
 *   <li>{@code SNAPSHOT}: {@code "SNAPSHOT"}, if it is a snapshot</li>
 *   <li>
 *     {@code TIMESTAMP}: the timestamp of {@link ConcreteSnapshotVersion}s
 *   </li>
 *   <li>
 *     {@code BUILDNUMBER}: the buildnumber of {@link ConcreteSnapshotVersion}s
 *   </li>
 *   <li>
 *     {@code QUALIFIERS}: all qualifiers that are neither the {@link Branch},
 *     nor timestamp or buildnumber, separated by hyphons ({@code -})
 *   </li>
 * </ul>
 *
 * Two colons ({@code ::}) can be used to escape a single one.
 *
 * <p>
 * Example of use:
 * <pre>
 *    final Version version = VersionParser.DEFAULT_PARSER
 *        .parsePotentialConcreteSnapshotVersion(
 *                "1.2.3-My_Feature-build7-20121209.171545-13")
 *        .get();
 *    final VersionFormatter formatter = new VersionFormatter(
 *            ":MAJOR::.MINOR::-FEATURE::-SNAPSHOT:");
 *    System.out.println(formatter.format(version));
 *    // prints "1.2-SNAPSHOT"
 * </pre>
 *
 * <p>
 * The default instances {@link #DEFAULT_BASE_VERSION_FORMATTER} and
 * {@link #DEFAULT_CONCRETE_VERSION_FORMATTER} are used for the
 * {@code toString()} methods of all {@link Version} subclasses.
 *
 * <p>
 * This class is immutable and thread-safe.
 */
public class VersionFormatter {

  private static final String DEFAULT_FORMAT =
      ":MAJOR::.MINOR::.INCREMENTAL::-FEATURE::-QUALIFIERS:";

  /**
   * A default instance for {@link BaseVersion}s.
   * The format is:
   * <pre>:MAJOR::.MINOR::.INCREMENTAL::-FEATURE::-QUALIFIERS::-SNAPSHOT:</pre>
   */
  public static final VersionFormatter DEFAULT_BASE_VERSION_FORMATTER =
      new VersionFormatter(VersionFormatter.DEFAULT_FORMAT + ":-SNAPSHOT:");

  /**
   * A default instance for {@link ConcreteSnapshotVersion}s.
   * The format is:
   * <pre>:MAJOR::.MINOR::.INCREMENTAL::-FEATURE::-QUALIFIERS::-TIMESTAMP::-BUILDNUMBER</pre>
   */
  public static final VersionFormatter DEFAULT_CONCRETE_VERSION_FORMATTER =
      new VersionFormatter(VersionFormatter.DEFAULT_FORMAT + ":-TIMESTAMP::-BUILDNUMBER:");

  private final List<FormatElement> formatStrings;

  /**
   * Class Constructor specifying a String representation of the format this
   * instance should format {@link Version}s into.
   * In it keywords can be used to insert the corresponding values of a
   * Version.  They must be formatted as follows:
   * <pre>:{prefix}{keyword}:</pre>
   * The prefix is optional and will only be output if a value is created by
   * the keyword.
   *
   * <p>
   * These are the available keywords:
   * <ul>
   *   <li>{@code MAJOR}: the major version</li>
   *   <li>{@code MINOR}: the minor version</li>
   *   <li>{@code INCREMENTAL}: the incremental version</li>
   *   <li>{@code FEATURE}: the branch if it is a feature-branch</li>
   *   <li>{@code SNAPSHOT}: {@code "SNAPSHOT"}, if it is a snapshot</li>
   *   <li>
   *     {@code TIMESTAMP}: the timestamp of {@link ConcreteSnapshotVersion}s
   *   </li>
   *   <li>
   *     {@code BUILDNUMBER}: the buildnumber of
   *     {@link ConcreteSnapshotVersion}s
   *   </li>
   *   <li>
   *     {@code QUALIFIERS}: all qualifiers that are neither the
   *     {@link Branch}, nor timestamp or buildnumber, separated by hyphons
   *     ({@code -})
   *   </li>
   * </ul>
   *
   * Two colons ({@code ::}) can be used to escape a single one.
   *
   * <p>
   * Each character not inside a keyword is considered a literal.
   *
   * <pre>
   *    final Version version = VersionParser.DEFAULT_PARSER
   *        .parsePotentialConcreteSnapshotVersion(
   *                "1.2.3-My_Feature-build7-20121209.171545-13")
   *        .get();
   *
   *    new VersionFormatter(":MAJOR::.MINOR::-FEATURE::-SNAPSHOT:")
   *        .format(version);
   *    // results in "1.2-SNAPSHOT"
   *
   *    new VersionFormatter("qualifiers:: :QUALIFIERS:")
   *        .format(version);
   *    // results in "qualifiers: build7"
   * </pre>
   *
   * @param format the format String to be parsed
   * @throws IllegalFormatFlagsException if the format is invalid
   * @throws MissingColonFormatException if a keyword definition is unclosed
   */
  public VersionFormatter(final String format)
      throws IllegalFormatFlagsException, MissingColonFormatException {
    Objects.requireNonNull(format);
    this.formatStrings = this.parseFormat(format);
  }

  @Override
  @SuppressWarnings("checkstyle:nofinalizer")
  protected final void finalize() {
    // prevent finalizer attacks
  }

  /**
   * Parses the given {@code format} into a {@link List} of
   * {@link FormatElement}s.
   * Each is either a {@link StringElement}, which represents a String literal
   * or a {@link KeywordElement}, that resolves to the value of it's
   * corresponding field of a {@link Version} beeing formatted by this
   * instance.
   *
   * <p>
   * This method is supposed to only be called once when the instance is
   * created.
   *
   * @param format the format String to be parsed
   * @return the parsed FormatElements
   * @throws IllegalFormatFlagsException if the format is invalid
   * @throws MissingColonFormatException if a keyword definition is unclosed
   */
  private List<FormatElement> parseFormat(final String format)
      throws IllegalFormatFlagsException, MissingColonFormatException {
    final List<FormatElement> result = new ArrayList<>();
    char character;
    int openingColonIndex = -1;
    int startIndex = 0;
    for (int i = 0; i < format.length(); i++) {
      character = format.charAt(i);
      if (character != ':') {
        continue;
      }
      if (startIndex > openingColonIndex) {
        openingColonIndex = i;
        if (i == startIndex) {
          continue;
        }
        result.add(new StringElement(format.substring(startIndex, i)));
      } else {
        startIndex = i + 1;
        result.add(
            i - 1 == openingColonIndex
                ? new StringElement(":")
                : KeywordElement.parse(format.substring(openingColonIndex + 1, i)));
      }
    }
    if (startIndex <= openingColonIndex) {
      throw new MissingColonFormatException(format.substring(openingColonIndex));
    }
    result.add(new StringElement(format.substring(startIndex)));
    return result;
  }

  /**
   * Formats a {@link Version} into a String based on this instances format.
   *
   * @param version the Version to be formatted
   * @return a String representation of the given Version
   */
  public String format(final Version version) {
    final StringBuilder result = new StringBuilder();
    String formatted;
    for (final FormatElement element : this.formatStrings) {
      formatted = element.format(version);
      if (formatted != null) {
        result.append(formatted);
      }
    }
    return result.toString();
  }

  /**
   * An Element of the format of a {@link VersionFormatter}.
   * These may either be {@link StringElement} for literals or
   * {@link KeywordElement}s that resolve {@link Keyword}s.
   */
  private abstract static class FormatElement {

    /**
     * Formats a {@link Version} into a String.
     */
    abstract String format(Version version);
  }

  /**
   * An {@link FormatElement} of a {@link VersionFormatter}s format,
   * representing a String literal.
   */
  private static final class StringElement extends FormatElement {
    private final String string;

    StringElement(final String string) {
      this.string = string;
    }

    /**
     * Returns the String literal represented by this instance.
     */
    @Override
    String format(final Version version) {
      return this.string;
    }
  }

  /**
   * Ein Schlüsselwort-Element eines Formats - bestehend aus einem
   * optionalen Prefix und einem {@link VersionFormatter.Keyword Keyword}.
   */
  /**
   * An {@link FormatElement} of a {@link VersionFormatter}s format,
   * representing a String literal.
   */
  private static final class KeywordElement extends FormatElement {
    private final Keyword keyword;
    private final String prefix;

    /**
     * Parses a part of a {@link VersionFormatter}s format, representing a
     * keyword into a {@link KeywordElement}.
     *
     * @param string a format part between two colons ({@code :})
     * @return an appropriate KeywordElement
     * @throws IllegalFormatFlagsException if no such {@link Keyword} exists
     */
    static KeywordElement parse(final String string) throws IllegalFormatFlagsException {
      for (final Keyword keyword : Keyword.values()) {
        if (string.endsWith(keyword.name())) {
          return new KeywordElement(keyword, keyword.parsePrefix(string));
        }
      }
      throw new IllegalFormatFlagsException(string);
    }

    KeywordElement(final Keyword keyword, final String prefix) {
      this.prefix = prefix;
      this.keyword = keyword;
    }

    /**
     * Uses this instances {@link #keyword} to extract values form a
     * {@link Version} and possibly prepends the {@link #prefix}.
     */
    @Override
    String format(final Version version) {
      final String result = this.keyword.convert(version);
      if (result != null && this.prefix != null) {
        return this.prefix + result;
      }
      return result;
    }
  }

  /**
   * Keywords that may be used in a {@link VersionFormatter}s format.
   */
  private enum Keyword {
    MAJOR(Version::getMajor),
    MINOR(Version::getMinor),
    INCREMENTAL(Version::getIncremental),
    FEATURE(e -> e.getBranch().isFeature() ? e.getBranch() : null),
    SNAPSHOT(e -> e.isSnapshot() ? "SNAPSHOT" : null),
    TIMESTAMP(
        e ->
            e instanceof ConcreteSnapshotVersion
                ? ((ConcreteSnapshotVersion) e).getTimestamp()
                : null),
    BUILDNUMBER(
        e ->
            e instanceof ConcreteSnapshotVersion
                ? ((ConcreteSnapshotVersion) e).getBuildnumber()
                : null),
    QUALIFIERS(e -> !e.getQualifiers().isEmpty() ? String.join("-", e.getQualifiers()) : null);

    private final transient Function<Version, ? extends Object> converter;

    Keyword(final Function<Version, ? extends Object> converter) {
      this.converter = converter;
    }

    String convert(final Version version) {
      final Object result = this.converter.apply(version);
      if (result == null) {
        return null;
      }
      if (result instanceof String) {
        return (String) result;
      }
      return result.toString();
    }

    String parsePrefix(final String string) {
      final int length = string.length() - this.name().length();
      return length > 0 ? string.substring(0, length) : null;
    }
  }

  /**
   * A {@link IllegalFormatFlagsException} describing an illformed keyword
   * definition in a {@link VersionFormatter}s format.
   */
  public static class MissingColonFormatException extends IllegalFormatFlagsException {
    private static final long serialVersionUID = -1979924458503853293L;

    MissingColonFormatException(final String string) {
      super(string);
    }
  }
}
