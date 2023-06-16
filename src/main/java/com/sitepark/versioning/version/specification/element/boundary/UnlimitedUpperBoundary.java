package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.version.Version;

/**
 * A {@link Boundary.Upper} for {@link Boundaries} instances.
 *
 * This {@link Boundary} does not exclude any {@link Version}s.
 * More formally, this {@code Boundary} {@code B} contains all {@code Version}s
 * {@code v} such that {@code B = {v | v}}.
 */
public final class UnlimitedUpperBoundary extends Boundary
		implements Boundary.Upper {
	private static final long serialVersionUID = -7219879790612151080L;

	/**
	 * Class Constructor.
	 */
	public UnlimitedUpperBoundary() {
	}

	@Override
	public int compareTo(final Version version) {
		return 1;
	}

	@Override
	public boolean includesVersion(final Version version) {
		return Boundary.Upper.super.includesVersion(version);
	}

	@Override
	public String toString() {
		return ")";
	}

	@Override
	public int hashCode() {
		return -1742678920;
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof UnlimitedUpperBoundary;
	}
}
