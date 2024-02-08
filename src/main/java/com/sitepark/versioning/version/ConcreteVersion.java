package com.sitepark.versioning.version;

/**
 * A fully-specified, unique {@link Version}.
 * Implementations of this interface may be used to identify exactly one
 * artifact/package/revision or similar.  Usually this is used internally while
 * {@link BaseVersion}s take precedence in external APIs.
 *
 * <p>
 * For example, an artifact may be refered to by a
 * {@link ConcreteSnapshotVersion} like <code>1.2-20230611.123141-1</code>,
 * which identifies this exact {@code Version} of it.  When instead refering to
 * it by the {@link SnapshotVersion} <code>1.2-SNAPSHOT</code> <em>any</em>
 * {@code ConcreteSnapshotVersion} may be meant.  Depending on the usecase and
 * implementation this usually is the latest one.
 *
 * <p>
 * {@link ReleaseVersion}s (like <code>1.0.3</code>) are always both,
 * {@link BaseVersion} and {@link ConcreteVersion}.
 */
public interface ConcreteVersion extends Version {

  /**
   * Returns a {@link BaseVersion} representing this instance.
   *
   * @return this instance as a {@code BaseVersion}
   */
  public BaseVersion asBaseVersion();
}
