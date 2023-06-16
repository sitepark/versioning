package com.sitepark.versioning.version;

/**
 * A commonly used {@link Version}.
 * This interface refers to a high-level {@code Version}, potentially
 * supersetting many {@link ConcreteVersion}s.
 *
 * <p>
 * For example, a {@code SnapshotVersion} <code>1.2-SNAPSHOT</code>, may act as
 * an alias for the {@code ConcreteSnapshotVersion}s
 * <code>1.2-20230611.123141-1</code> and <code>1.2-20230612.130122-2</code>.
 *
 * <p>
 * {@link ReleaseVersion}s (like <code>1.0.3</code>) are always both,
 * {@link BaseVersion} and {@link ConcreteVersion}.
 */
public interface BaseVersion extends Version {
}
