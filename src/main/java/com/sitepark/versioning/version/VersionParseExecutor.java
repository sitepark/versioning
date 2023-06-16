package com.sitepark.versioning.version;

import java.text.ParseException;

import com.sitepark.versioning.Branch;

/**
 * Abstract implementation of the {@link Version}-parsing process.
 *
 * <strong>Attention:</strong> this class is meant for a single execution and
 * therefore not thread safe!
 *
 * @param <R> class of the {@code Version} the implementation attempts to parse
 */
abstract class VersionParseExecutor<R> {

	/**
	 * The section of a {@link Version}-String this parser may be in.
	 */
	protected enum Section {
		MAJOR,
		MINOR,
		INCREMENTAL,
		BRANCH,
		QUALIFIER;
	}

	protected final String string;
	protected final int maxIndex;
	protected final byte flags;

	protected char currentChar;
	protected int index = -1;
	protected boolean isLastChar = false;
	protected String currentItem = "";
	protected int currentItemLength = 0;
	protected Section currentSection = Section.MAJOR;

	protected final VersionBuilder versionBuilder;

	VersionParseExecutor(final String string, final byte flags) {
		this.string = string;
		this.maxIndex = string.length() - 1;
		this.flags = flags;
		this.versionBuilder = new VersionBuilder();
	}

	/**
	 * Executes the parsing process of the String specified in the constructor.
	 *
	 * @throws ParseException if the String is not compliant with the required
	 *                        format
	 */
	public R execute() throws ParseException {
		if (this.maxIndex == -1) {
			this.fail();
		}
		do {
			this.isLastChar = ++this.index == this.maxIndex;
			this.currentChar = this.string.charAt(this.index);
			this.step();
		} while (!this.isLastChar);
		return this.buildVersion();
	}

	/**
	 * Consumes a single char
	 */
	private void step() throws ParseException {
		switch (this.currentChar) {
			case ' ':
			case '\n':
			case '\t':
			case '\r':
			case '\b':
			case '\0':
			case '[':
			case ']':
			case '(':
			case ')':
			case ',':
			case '*':
				this.fail();
				break;
			case '.':
				this.handleDot();
				break;
			case '-':
				this.handleHyphen();
				break;
			default:
				this.handleNormalChar();
		}
	}

	/**
	 * Is invoked when a dot ({@code .}) is encountered.
	 * Usually this means that a {@link Section#MAJOR} or {@link Section#MINOR}
	 * Section ends, but can also be a "normal" char inside a {@code qualifier}.
	 *
	 * @throws ParseException if no dot is expected at the current position
	 */
	protected abstract void handleDot() throws ParseException;

	/**
	 * Is invoked when a hyphon ({@code -}) is encountered.
	 * Usually this means that a new {@code qualifier} beginns.
	 *
	 * @throws ParseException if no hyphon is expected at the current position
	 */
	protected abstract void handleHyphen() throws ParseException;

	/**
	 * Is invoked when a "normal" character is encountered.
	 * "Normal" meaning that it is neither a dot ({@code .}) nor a hyphon
	 * ({@code -}) or any character deemed invalid.
	 *
	 * @throws ParseException if the character is unexpected at the current
	 *                        position
	 */
	protected abstract void handleNormalChar() throws ParseException;

	/**
	 * Constructs the resulting {@link Version}.
	 *
	 * @return a Version build from the parsed properties
	 */
	protected abstract R buildVersion();

	/**
	 * Fails the parsing process by throwing a {@link ParseException} with the
	 * current {@link #index}.
	 *
	 * @throws ParseException always
	 */
	protected void fail() throws ParseException {
		throw new ParseException(this.string, this.index);
	}

	/**
	 * Adds the {@link #currentItem} as {@code major} to the
	 * {@link #versionBuilder} and advances to the {@link Section#MINOR}.
	 *
	 * @throws ParseException if the currentItem is neither empty nor a parsable
	 *                        Integer
	 * @see VersionBuilder#setMajor(int)
	 */
	protected void addMajor() throws ParseException {
		try {
			if (this.currentItemLength > 0) {
				this.versionBuilder.setMajor(
						Integer.parseInt(this.currentItem));
				this.resetCurrentItem();
			}
			this.currentSection = Section.MINOR;
		} catch (final NumberFormatException exception) {
			this.fail();
		}
	}

	/**
	 * Adds the {@link #currentItem} as {@code minor} to the
	 * {@link #versionBuilder} and advances to the {@link Section#INCREMENTAL}.
	 *
	 * @throws ParseException if the currentItem is neither empty nor a parsable
	 *                        Integer
	 * @see VersionBuilder#setMinor(int)
	 */
	protected void addMinor() throws ParseException {
		try {
			if (this.currentItemLength > 0) {
				this.versionBuilder.setMinor(
						Integer.parseInt(this.currentItem));
				this.resetCurrentItem();
			}
			this.currentSection = Section.INCREMENTAL;
		} catch (final NumberFormatException exception) {
			this.fail();
		}
	}

	/**
	 * Adds the {@link #currentItem} as {@code incremental} to the
	 * {@link #versionBuilder} and advances to the {@link Section#BRANCH}.
	 *
	 * @throws ParseException if the currentItem is neither empty nor a parsable
	 *                        Integer
	 * @see VersionBuilder#setIncremental(int)
	 */
	protected void addIncremental() throws ParseException {
		try {
			if (this.currentItemLength > 0) {
				this.versionBuilder.setIncremental(
						Integer.parseInt(this.currentItem));
				this.resetCurrentItem();
			}
			this.currentSection = Section.BRANCH;
		} catch (final NumberFormatException exception) {
			this.fail();
		}
	}

	/**
	 * Adds the {@link #currentItem} as {@code branch} to the
	 * {@link #versionBuilder} and advances to the {@link Section#QUALIFIER}.
	 * Defaults to {@link Branch#DEVELOP} if the
	 * {@link VersionParser.Characteristics#IGNORE_BRANCHES} flag is set.
	 *
	 * @see VersionBuilder#setBranch(Branch)
	 */
	protected void addBranch() {
		if (!VersionParser.Characteristics.IGNORE_BRANCHES.isSet(this.flags)
				&& !this.currentItem.equalsIgnoreCase("develop")) {
			this.versionBuilder.setBranch(new Branch(this.currentItem));
		}
		this.resetCurrentItem();
		this.currentSection = Section.QUALIFIER;
	}

	/**
	 * Adds the {@link #currentItem} as {@code qualifier} to the
	 * {@link #versionBuilder} and resets it.
	 * If the {@link VersionParser.Characteristics#IGNORE_QUALIFIERS} flag is
	 * set the adding is suppressed.
	 *
	 * @see VersionBuilder#setBranch(Branch)
	 */
	protected void addQualifier() {
		if (!VersionParser.Characteristics.IGNORE_QUALIFIERS.isSet(
				this.flags)) {
			this.versionBuilder.addQualifier(this.currentItem);
		}
		this.resetCurrentItem();
	}

	/**
	 * Adds the {@link #currentChar} to the {@link #currentItem}.
	 */
	protected void appendCharToCurrentItem() {
		this.currentItem += this.currentChar;
		this.currentItemLength++;
	}

	/**
	 * Resets the {@link #currentItem} back to it's initial state.
	 */
	protected void resetCurrentItem() {
		this.currentItem = "";
		this.currentItemLength = 0;
	}
}
