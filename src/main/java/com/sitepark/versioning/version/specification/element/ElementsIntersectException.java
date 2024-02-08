package com.sitepark.versioning.version.specification.element;

/**
 * A {@link Exception} signaling that two {@link SpecificationElement}s
 * intesect where they were not expected to.
 *
 * @see SpecificationElement#getIntersection(SpecificationElement)
 */
public final class ElementsIntersectException extends RuntimeException {
  private static final long serialVersionUID = -3959646053406817174L;

  /**
   * Class Constructor specifying this {@link Exception}s detail message.
   *
   * @param message the detail message
   */
  public ElementsIntersectException(final String message) {
    super(message);
  }
}
