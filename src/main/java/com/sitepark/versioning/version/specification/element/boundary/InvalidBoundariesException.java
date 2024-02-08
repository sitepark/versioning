package com.sitepark.versioning.version.specification.element.boundary;

import com.sitepark.versioning.Branch;

/**
 * A {@link Exception} signaling that {@link Boundary}s of a {@link Boundaries}
 * instance do not relate to each other as expected.
 *
 * This may be due to the {@link Boundary.Lower} one beeing considered larger
 * than the {@link Boundary.Upper} one, both beeing limit to different
 * {@link Branch}es or that both beeing "unlimited"
 * ({@link UnlimitedLowerBoundary} and {@link UnlimitedUpperBoundary}).
 *
 * @see Boundaries#getBranch()
 */
public final class InvalidBoundariesException extends IllegalArgumentException {
  private static final long serialVersionUID = 5568950780006484682L;

  /**
   * Class Constructor specifying this {@link Exception}s detail message.
   *
   * @param message the detail message
   */
  public InvalidBoundariesException(final String message) {
    super(message);
  }

  /**
   * Class Constructor specifying this {@link Exception}s detail message and
   * cause.
   *
   * @param message the detail message
   * @param cause the cause of this {@link Exception}
   */
  public InvalidBoundariesException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
