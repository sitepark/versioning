package com.sitepark.versioning.version;

import java.text.ParseException;

/**
 * Implements the {@link ReleaseVersion}-parsing process.
 *
 * <strong>Attention:</strong> this class is meant for a single execution and
 * therefore not thread safe!
 */
class ReleaseParseExecutor extends VersionParseExecutor<ReleaseVersion> {

	ReleaseParseExecutor(final String string, final byte flags) {
		super(string, flags);
	}

	@Override
	protected ReleaseVersion buildVersion() {
		return this.versionBuilder.buildRelease();
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
				this.addBranch();
				break;
			case QUALIFIER:
				this.addQualifier();
				break;
			default:
				this.fail();
		}
	}
}
