package com.sitepark.versioning.version;

import java.text.ParseException;

import com.sitepark.versioning.Branch;

/**
 * A class to parse Strings of a certain format into {@link Version}s.
 * Generally this format is as follows:<br>
 * {@code "<major>.<minor>.<incremental>-<branch>-<qualifiers>"}
 * <ul>
 *   <li>
 *     {@code major} may be omitted if not leaving the String empty, defaults
 *     to zero ({@code 0})
 *   </li>
 *   <li>
 *     {@code minor} and the leading dot ({@code .}) may be omitted, defaults
 *     to zero ({@code 0})
 *   </li>
 *   <li>
 *     {@code incremental} and the leading dot ({@code .}) may be omitted,
 *     defaults to zero ({@code 0})
 *   </li>
 *   <li>
 *     {@code branch} and the leading hyphon ({@code -}) may be omitted if no
 *     {@code qualifiers} are given, defaults to {@link Branch#DEVELOP}
 *   </li>
 *   <li>
 *     {@code qualifiers} and the leading hyphon ({@code -}) may be ommitted
 *   </li>
 * </ul>
 *
 * <p>
 * Therefore all of these are valid examples:
 * <pre>
 *    "1.0.0-develop"
 *    "1"
 *    ".2"
 *    "1.3-some_feature-release_candidate-0"
 *    "-experimental"
 * </pre>
 *
 * <p>
 * When parsing {@link PotentialSnapshotVersion}s the {@code qualifier}
 * {@code "SNAPSHOT"} denotes the {@code Version} to be a {@link
 * SnapshotVersion} if at the end of the String.
 * <pre>
 *    // SnapshotVersion
 *    parser.parsePotentialSnapshot("1.0-FEATURE-SNAPSHOT");
 *    // ReleaseVersion
 *    parser.parsePotentialSnapshot("1.0-SNAPSHOT-FEATURE");
 * </pre>
 *
 * <p>
 * For {@link PotentialConcreteSnapshotVersion}s these are instead a timestamp
 * of the form {@code yyyyMMdd.HHmmss} and a buildnumber.
 * <pre>
 *    // ConcreteSnapshotVersion
 *    parser.parsePotentialConcreteSnapshot("1.0-20230605.123612-1);
 *    // ReleaseVersion
 *    parser.parsePotentialConcreteSnapshot("1.0-1-20230605.123612);
 * </pre>
 *
 * <p>
 * A VersionParser instance may be configured by specifiying one or more
 * {@link Characteristics} in the constructor.
 * <ul>
 *   <li>
 *     {@link Characteristics#IGNORE_BRANCHES}<br>
 *     Always set the {@link Branch} to {@link Branch#DEVELOP}.  This does not
 *     cause {@code branch} keywords to be added to the {@code qualifiers}.
 *   </li>
 *   <li>
 *     {@link Characteristics#IGNORE_QUALIFIERS}<br>
 *     Do not set any {@code qualifiers}.  Does not influence the {@code branch}
 *     or the exclusive {@code qualifiers} for {@link SnapshotVersion}s
 *     ({@code "SNAPSHOT"}) or {@link ConcreteSnapshotVersion}s
 *     ({@code timestamp} and {@code buildnumber}). </li>
 * </ul>
 *
 * <p>
 * This class is immutable and thread-safe.
 *
 * @see #parseRelease(String)
 * @see #parsePotentialSnapshot(String)
 * @see #parsePotentialConcreteSnapshot(String)
 */
public class VersionParser {

	/**
	 * A default instance without {@link Characteristics}.
	 */
	public static final VersionParser DEFAULT_PARSER = new VersionParser();

	private final byte flags;

	/**
	 * Options to configure a {@link VersionParser} instance with.
	 */
	public enum Characteristics {
		/**
		 * Always set the {@link Branch} to {@link Branch#DEVELOP}.
		 * This does not cause {@code branch} keywords to be added to the
		 * {@code qualifiers}.
		 */
		IGNORE_BRANCHES((byte)0b0000_0001),
		/**
		 * Do not set any {@code qualifiers}.
		 * Does not influence the {@code branch} or the exclusive
		 * {@code qualifiers} for {@link SnapshotVersion}s ({@code "SNAPSHOT"})
		 * or {@link ConcreteSnapshotVersion}s ({@code timestamp} and
		 * {@code buildnumber}).
		 */
		IGNORE_QUALIFIERS((byte)0b0000_0010);

		private final byte mask;

		private Characteristics(final byte mask) {
			this.mask = mask;
		}

		int getMask() {
			return this.mask;
		}

		boolean isSet(final byte value) {
			return (value & this.mask) == this.mask;
		}
	}

	/**
	 * Class Constructor specifiying {@link Characteristics} to be applied as
	 * varargs array.
	 *
	 * @param characteristics the configuration options
	 */
	public VersionParser(final Characteristics... characteristics) {
		byte flags = 0;
		for (final Characteristics characteristic : characteristics) {
			flags |= characteristic.getMask();
		}
		this.flags = flags;
	}

	/**
	 * Parses a String into a {@link ReleaseVersion}.
	 *
	 * The required format is as follows:<br>
	 * {@code "<major>.<minor>.<incremental>-<branch>-<qualifiers>"}
	 * <ul>
	 *   <li>
	 *     {@code major} may be omitted if not leaving the String empty,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code minor} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code incremental} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code branch} and the leading hyphon ({@code -}) may be omitted if
	 *     no {@code qualifiers} are given, defaults to {@link Branch#DEVELOP}
	 *   </li>
	 *   <li>
	 *     {@code qualifiers} and the leading hyphon ({@code -}) may be ommitted
	 *   </li>
	 * </ul>
	 *
	 * <p>
	 * All of these are valid examples:
	 * <pre>
	 *    "1.0.0-develop"
	 *    "1"
	 *    ".2"
	 *    "1.3-some_feature-release_candidate-0"
	 *    "-experimental"
	 * </pre>
	 *
	 * <p>
	 * {@code qualifiers}, that imply the {@link Version} not beeing a
	 * {@link ReleaseVersion} in the {@link #parsePotentialSnapshot(String)} and
	 * {@link #parsePotentialConcreteSnapshot(String)} methods do not have any
	 * special meaning here.
	 *
	 * @param version the String to be parsed
	 * @return the resulting ReleaseVersion
	 * @throws ParseException when the given String is not compatible with the
	 *                        required format
	 * @see #parsePotentialSnapshot(String)
	 * @see #parsePotentialConcreteSnapshot(String)
	 */
	public ReleaseVersion parseRelease(final String version)
			throws ParseException {
		return new ReleaseParseExecutor(version, this.flags).execute();
	}


	/**
	 * Parses a String into either a {@link SnapshotVersion} or a
	 * {@link ReleaseVersion}.
	 *
	 * The required format is as follows:<br>
	 * {@code "<major>.<minor>.<incremental>-<branch>-<qualifiers>-SNAPSHOT"}
	 * <ul>
	 *   <li>
	 *     {@code major} may be omitted if not leaving the String empty,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code minor} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code incremental} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code branch} and the leading hyphon ({@code -}) may be omitted if
	 *     no {@code qualifiers} are given, defaults to {@link Branch#DEVELOP}
	 *   </li>
	 *   <li>
	 *     {@code qualifiers} and the leading hyphon ({@code -}) may be ommitted
	 *   </li>
	 *   <li>
	 *     {@code "-SNAPSHOT"} causes the result to be a {@code SnapshotVersion}
	 *     if present and a {@code ReleaseVersion} otherwise
	 *   </li>
	 * </ul>
	 *
	 * <p>
	 * All of these are valid examples:
	 * <pre>
	 *    "1.0.0-develop"
	 *    "1"
	 *    ".2"
	 *    "1.3-some_feature-release_candidate-0"
	 *    "-experimental"
	 * </pre>
	 *
	 * <p>
	 * If the {@code "SNAPSHOT"} {@code qualifier} is not located at the very
	 * end it is interpreted as any other {@code qualifier} or as
	 * {@code branch} depending on it's position.
	 *
	 * @param version the String to be parsed
	 * @return the resulting {@link Version} wrapped inside a
	 *          {@link PotentialSnapshotVersion}
	 * @throws ParseException when the given String is not compatible with the
	 *                        required format
	 * @see #parseRelease(String)
	 * @see #parsePotentialConcreteSnapshot(String)
	 */
	public PotentialSnapshotVersion parsePotentialSnapshot(
			final String version) throws ParseException {
		return new PotentialSnapshotParseExecutor(version, this.flags)
			.execute();
	}

	/**
	 * Parses a String into either a {@link ConcreteSnapshotVersion} or a
	 * {@link ReleaseVersion}.
	 *
	 * The required format is as follows:<br>
	 * {@code "<major>.<minor>.<incremental>-<branch>-<qualifiers>-<timestamp>-<buildnumber>"}
	 * <ul>
	 *   <li>
	 *     {@code major} may be omitted if not leaving the String empty,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code minor} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code incremental} and the leading dot ({@code .}) may be omitted,
	 *     defaults to zero ({@code 0})
	 *   </li>
	 *   <li>
	 *     {@code branch} and the leading hyphon ({@code -}) may be omitted if
	 *     no {@code qualifiers} are given, defaults to {@link Branch#DEVELOP}
	 *   </li>
	 *   <li>
	 *     {@code qualifiers} and the leading hyphon ({@code -}) may be ommitted
	 *   </li>
	 *   <li>
	 *     {@code timestamp} and {@code buildnumber} causes the result to be a
	 *     {@code ConcreteSnapshotVersion} if both are present (with their
	 *     hyphens ({@code -}) and a {@code ReleaseVersion} otherwise.
	 *   </li>
	 * </ul>
	 *
	 * <p>
	 * All of these are valid examples:
	 * <pre>
	 *    "1.0.0-develop"
	 *    "1"
	 *    ".2"
	 *    "1.3-some_feature-release_candidate-0"
	 *    "-experimental"
	 * </pre>
	 *
	 * <p>
	 * If only one of the {@code timestamp} and {@code buildnumber}
	 * {@code qualifiers} is present or they are not at the very end in the
	 * correct order they are interpreted as any other {@code qualifiers} or as
	 * {@code branch} depending on their position.
	 *
	 * @param version the String to be parsed
	 * @return the resulting {@link Version} wrapped inside a
	 *          {@link PotentialConcreteSnapshotVersion}
	 * @throws ParseException when the given String is not compatible with the
	 *                        required format
	 * @see #parseRelease(String)
	 * @see #parsePotentialSnapshot(String)
	 */
	public PotentialConcreteSnapshotVersion parsePotentialConcreteSnapshot(
			final String version) throws ParseException {
		return new PotentialConcreteSnapshotParseExecutor(version, this.flags)
			.execute();
	}
}
