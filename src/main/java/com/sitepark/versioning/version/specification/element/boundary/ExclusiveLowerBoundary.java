package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.Version;
import com.sitepark.versioning.version.BaseVersion;

/**
 * A {@link Boundary.Lower} for {@link Boundaries} instances.
 *
 * This {@link Boundary} includes all {@link Version}s, that are less than the
 * {@link BaseVersion} of this instance.
 * More formally, this {@code Boundary} {@code B} with the {@code BaseVersion}
 * <code>v<sub>B</sub></code> contains all {@code Version}s {@code v} such that
 * <code>B = {v | v &lt; v<sub>B</sub>}</code>.
 */
public final class ExclusiveLowerBoundary extends Boundary.WithVersion
		implements Boundary.Lower {
	private static final long serialVersionUID = 4471709629791867641L;

	/**
	 * Class Constructor specifying the {@link BaseVersion} to compare other
	 * {@link Version}s to when determining wether or not it is included by
	 * this instance.
	 *
	 * @param version the {@code BaseVersion} for this instance
	 */
	public ExclusiveLowerBoundary(final BaseVersion version) {
		super(version);
	}

	@Override
	public int compareTo(final Version version) {
		final int cmp = this.version.compareTo(version);
		return cmp != 0 ? cmp : 1;
	}

	@Override
	public boolean includesVersion(final Version version) {
		return Boundary.Lower.super.includesVersion(version);
	}

	@Override
	public String toString() {
		return "(" + this.version.toString();
	}

	@Override
	public int hashCode() {
		return 41 + this.version.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ExclusiveLowerBoundary)) {
			return false;
		}
		return this.version.equals(((ExclusiveLowerBoundary)other).version);
	}
}
