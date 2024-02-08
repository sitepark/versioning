package com.sitepark.versioning;

import com.sitepark.versioning.version.Version;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Class, that aims to represent "feature-branches" found in
 * {@link Version}s.
 * By default a {@code Version} does not explicitly define a {@code Branch}
 * (for example {@code "1.0.3"}), in which cases the "non-feature-branch"
 * {@link #DEVELOP} is used.
 *
 * <p>
 * Branches are compared alphabetically, although for "non-feature-branches"
 * the following rules apply:
 * <ul>
 *   <li>{@code non-feature-branch > feature-branch}</li>
 *   <li>{@code non-feature-branch == non-feature-branch}</li>
 * </ul>
 */
public class Branch implements Comparable<Branch>, Serializable {
  private static final long serialVersionUID = 7052868613896268596L;

  private static final String DEVELOP_VALUE = "develop";

  /**
   * Denotes the absense of a "feature-branch".
   */
  public static final Branch DEVELOP = new Branch(Branch.DEVELOP_VALUE);

  private final String value;

  /**
   * Class Constructor specifiying a String representation of the Branch.
   *
   * If an empty String is given or
   * {@code value.equalsIgnoreCase("develop") == true} the Branch will be
   * considered a "non-feature-branch".  In this case one may want to use the
   * {@link #DEVELOP} constant instead.
   *
   * @param value a String representation of a Branch
   * @throws IllegalArgumentException when the value contains spaces or
   *                                  hyphens
   * @throws NullPointerException     when value is <em>null</em>
   */
  public Branch(final String value) {
    if (value.indexOf(' ') != -1) {
      throw new IllegalArgumentException("Branches cannot contain spaces");
    }
    if (value.indexOf('-') != -1) {
      throw new IllegalArgumentException("Branches cannot contain hyphens");
    }
    this.value =
        value.equalsIgnoreCase(Branch.DEVELOP_VALUE) || value.length() == 0
            ? Branch.DEVELOP_VALUE
            : value;
  }

  /**
   * Returns wether the Branch is considered a "non-feature-branch".
   *
   * @return {@code true} if the Branch is equal to {@link #DEVELOP},
   *         {@code false} otherwise
   */
  public boolean isDevelop() {
    return this.value == Branch.DEVELOP_VALUE;
  }

  /**
   * Returns wether the Branch is considered a "feature-branch" or not.
   *
   * @return {@code true} if the Branch is not equal to {@link #DEVELOP},
   *         {@code false} otherwise
   */
  public boolean isFeature() {
    return this.value != Branch.DEVELOP_VALUE;
  }

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public int compareTo(final Branch other) {
    switch ((this.isDevelop() ? 1 : 0) + (other.isDevelop() ? 2 : 0)) {
      case 1: // only this is develop
        return 1;
      case 2: // only other is develop
        return -1;
      case 3: // both are develop
        return 0;
      default: // neither is develop
        return this.value.compareTo(other.value);
    }
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof Branch)) {
      return false;
    }
    return this.value.equals(((Branch) other).value);
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

  @Override
  @SuppressWarnings("checkstyle:nofinalizer")
  protected final void finalize() {
    // prevent finalizer attacks
  }

  /**
   * Assures that the Serializable interface does not create a new instance
   * of {@code Branch.DEVELOP_VALUE}, which would mess up comparisons in
   * {@code isDevelop} and {@code isFeature}.
   */
  private Object readResolve() throws ObjectStreamException {
    if (this.value.equalsIgnoreCase(Branch.DEVELOP_VALUE) || value.length() == 0) {
      return Branch.DEVELOP;
    }
    return this;
  }
}
