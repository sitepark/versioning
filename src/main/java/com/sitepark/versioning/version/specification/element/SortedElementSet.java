package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A {@link Set} containing and ordering {@link SpecificationElement}s.
 *
 * The elements are sorted ascending as determined by
 * {@link SpecificationElement#compareTo(SpecificationElement)} and cannot
 * overlap.
 *
 * <p>
 * <strong>Warning</strong>: This class is not thread-safe and does not check
 * for concurrent modifications!
 */
public class SortedElementSet implements Set<SpecificationElement>, Serializable {
  private static final long serialVersionUID = 5866363094954901466L;

  private int size;
  private Node first;

  private static final class Node implements Serializable {
    private static final long serialVersionUID = -8350208113647286849L;

    private final SpecificationElement element;
    private Node next;

    private Node(final SpecificationElement element) {
      this.element = element;
    }

    @Override
    public int hashCode() {
      return this.element.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
      return other instanceof final Node that && this.element.equals(that.element);
    }
  }

  /**
   * Calculates if and how two {@link SortedElementSet}s intersect.
   *
   * <strong>Warning</strong>: If any of the {@code SortedElementSet}s change
   * during calculation or the same instance is invoced twice the behaviour
   * is undefined!  This class is neither thread-safe nor does it check for
   * concurrent modifications!
   * @see SortedElementSet#getIntersection(SortedElementSet)
   */
  private static final class IntersectionCalculator implements Supplier<SortedElementSet> {
    private final SortedElementSet result;
    private Node left;
    private Node right;

    private IntersectionCalculator(final SortedElementSet first, final SortedElementSet second) {
      this.left = first.first;
      this.right = second.first;
      this.result = new SortedElementSet();
    }

    /**
     * Calculates and returns a new {@link SortedElementSet} representing
     * the intersection of the two {@code SortedElementSet}s specified in
     * the Constructor.
     *
     * <p>
     * More formally, an intersection of two {@code SortedElementSet}s
     * {@code A} and {@code B} is defined for all {@link Version}s {@code v}
     * as {@code A ∩ B = {v | v ∈ A and v ∈ B}}.
     * If the two {@code SortedElementSet}s do not intersect the result
     * will be empty.
     *
     * <p>
     * <strong>Warning</strong>: If any of the {@code SortedElementSet}s
     * change during calculation or the same instance is invoced twice the
     * behaviour is undefined!  This class is neither thread-safe nor does
     * it check for concurrent modifications!
     *
     * @return a new {@code SortedElementSet} representing the intersection
     * @see SpecificationElement#getIntersection(SpecificationElement)
     */
    @Override
    public SortedElementSet get() {
      while (this.left != null && this.right != null) {
        switch (this.left.element.compareTo(this.right.element)) {
          case INTERSECTS_HIGHER:
          case INTERSECTS_COMPLETELY:
            this.addIntersection();
            this.right = this.right.next;
            break;
          case HIGHER:
            this.right = this.right.next;
            break;
          case INTERSECTS_EQUALY:
            this.addIntersection();
            this.right = this.right.next;
            this.left = this.left.next;
            break;
          case INTERSECTS_LOWER:
          case INTERSECTS_PARTIALLY:
            this.addIntersection();
            this.left = this.left.next;
            break;
          case LOWER:
            this.left = this.left.next;
            break;
          default:
            break;
        }
      }
      return this.result;
    }

    private void addIntersection() {
      final Optional<SpecificationElement> intersection =
          this.left.element.getIntersection(this.right.element);
      if (!intersection.isPresent()) {
        return; // should not be possible
      }
      this.add(intersection.get());
    }

    private void add(final SpecificationElement element) {
      this.result.add(element);
    }
  }

  /**
   * Class Constructor.
   *
   * Creates an empty instance.
   */
  public SortedElementSet() {
    this.first = null;
    this.size = 0;
  }

  /**
   * Adds a specified {@link SpecificationElement} and maintains the ascending
   * ordering.
   *
   * @param element the element to be added to this instance
   * @return {@code true} (as specified by {@link Collection#add(Object)})
   * @throws ElementsIntersectException if the specified element intersects
   *                                    with a already present one
   * @see #addAll(Collection)
   * @see SpecificationElement#getIntersection(SpecificationElement)
   */
  @Override
  public boolean add(final SpecificationElement element) {
    Node previous = null;
    loop:
    for (Node current = this.first; current != null; previous = current, current = current.next) {
      switch (current.element.compareTo(element)) {
        case LOWER:
          continue;
        case HIGHER:
          break loop;
        default:
          throw new ElementsIntersectException(
              "element " + element.toString() + " intersects with: " + this.toString());
      }
    }
    if (previous == null) {
      this.prepend(element);
    } else {
      this.insertAfter(element, previous);
    }
    return true;
  }

  /**
   * Adds all of the {@link SpecificationElement}s in the specified
   * {@link Collection} to this instance, in the order that they are returned
   * by the specified collection's {@code Iterator}.
   *
   * The behavior of this operation is undefined if the specified collection
   * is modified while the operation is in progress.  (This implies that the
   * behavior of this call is undefined if the specified collection is this
   * non-empty instance.)
   *
   * @param elements a {@code Collection} containing
   *                 {@code SpecificationElement}s to be added to this
   *                 instance
   * @return {@code true} if this instance changed as a result of the call
   * @throws ElementsIntersectException if any of the
   *                                    {@code SpecificationElement}s
   *                                    contained in the specified
   *                                    {@code Collection} intersects with a
   *                                    already present one
   * @see #add(SpecificationElement)
   * @see SpecificationElement#getIntersection(SpecificationElement)
   */
  @Override
  public boolean addAll(final Collection<? extends SpecificationElement> elements) {
    boolean hasChanged = false;
    for (final SpecificationElement element : elements) {
      hasChanged = this.add(element) || hasChanged;
    }
    return hasChanged;
  }

  /**
   * Removes all {@link SpecificationElement}s.
   */
  @Override
  public void clear() {
    this.first = null;
    this.size = 0;
  }

  /**
   * Returns {@code true} if this instance contains the specified element.
   *
   * More formally, returns {@code true} if and only if this instance contains
   * at least one {@code element} such that
   * {@code Objects.equals(object, element)}.
   *
   * @param other the object whose presence is to be tested
   * @return {@code true} if this instance contains the specified element
   * @see #containsAll(Collection)
   */
  @Override
  public boolean contains(final Object other) {
    for (Node node = this.first; node != null; node = node.next) {
      if (node.element.equals(other)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if this instance contains all of the elements in the
   * specified {@link Collection}.
   *
   * @param others {@code Collection} to be checked for containment
   * @return {@code true} if this instance contains all of the elements in the
   *         specified collection
   * @see #contains(Object)
   */
  @Override
  public boolean containsAll(final Collection<?> others) {
    for (final Object other : others) {
      if (!this.contains(other)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns {@code true} if this instance contains no elements.
   *
   * @return {@code true} if this instance contains no elements
   */
  @Override
  public boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Returns an {@link Iterator} over the elements in this instance.
   *
   * This {@code Iterator} allows for modifications via
   * {@link Iterator#remove()}.
   *
   * <p>
   * <strong>Warning</strong>: This {@code Iterator} does not check for any
   * concurrent modifications!
   */
  @Override
  public Iterator<SpecificationElement> iterator() {
    return new Iterator<SpecificationElement>() {
      private Node previous = null;
      private Node next = SortedElementSet.this.first;
      private boolean removedCurrent = false;

      @Override
      public boolean hasNext() {
        return this.next != null;
      }

      @Override
      public SpecificationElement next() throws NoSuchElementException {
        if (this.next == null) {
          throw new NoSuchElementException();
        }
        if (this.removedCurrent) {
          this.removedCurrent = false;
        } else if (this.previous != null) {
          this.previous = this.previous.next;
        } else if (this.next != SortedElementSet.this.first) {
          this.previous = SortedElementSet.this.first;
        }
        final SpecificationElement value = this.next.element;
        this.next = this.next.next;
        return value;
      }

      @Override
      public void remove() {
        if (this.next == SortedElementSet.this.first || this.removedCurrent) {
          throw new IllegalStateException();
        }
        if (this.previous == null) {
          SortedElementSet.this.first = SortedElementSet.this.first.next;
        } else {
          this.previous.next = this.next;
        }
        this.removedCurrent = true;
        SortedElementSet.this.size -= 1;
      }
    };
  }

  /**
   * Returns an {@link Iterator} over the elements in this instance.
   *
   * This {@code Iterator} does not allow for modifications via
   * {@link Iterator#remove()}.
   *
   * <p>
   * <strong>Warning</strong>: This {@code Iterator} does not check for any
   * concurrent modifications!
   */
  protected Iterator<SpecificationElement> unmodifiableIterator() {
    return new Iterator<SpecificationElement>() {
      private Node next = SortedElementSet.this.first;

      @Override
      public boolean hasNext() {
        return this.next != null;
      }

      @Override
      public SpecificationElement next() throws NoSuchElementException {
        if (this.next == null) {
          throw new NoSuchElementException();
        }
        final SpecificationElement value = this.next.element;
        this.next = this.next.next;
        return value;
      }
    };
  }

  /**
   * Removes the first occurrence of the specified element from this instance,
   * if it is present.
   *
   * If this instance does not contain the element, it is unchanged.  More
   * formally, removes the smallest {@code element} (according to
   * {@link SpecificationElement#compareTo(SpecificationElement)}) such that
   * {@code Objects.equals(other, element)} (if such an element exists).
   * Returns {@code true} if this instance contained the specified element (or
   * equivalently, if it changed as a result of the call).
   *
   * @param other the element to be removed from this instance, if present
   * @return {@code true} if this instance contained the specified element
   * @see #removeAll(Collection)
   * @see #removeIf(java.util.function.Predicate)
   * @see #contains(Object)
   */
  @Override
  public boolean remove(final Object other) {
    for (Node node = this.first, previous = null; node != null; previous = node, node = node.next) {
      if (node.element.equals(other)) {
        if (previous != null) {
          previous.next = node.next;
        } else {
          this.first = node.next;
        }
        this.size -= 1;
        return true;
      }
    }
    return false;
  }

  /**
   * Removes all elements that are contained in the specified
   * {@link Collection} and this instance.
   *
   * @param others a {@code Collection} containing elements to be removed
   *                from this instance
   * @return {@code true} if this instance changed as a result of the call
   * @see #remove(Object)
   * @see #removeIf(java.util.function.Predicate)
   * @see #contains(Object)
   */
  @Override
  public boolean removeAll(final Collection<?> others) {
    boolean hasChanged = false;
    for (final Object other : others) {
      hasChanged = this.remove(other) || hasChanged;
    }
    return hasChanged;
  }

  /**
   * Retains only the elements in this instance that are contained in the
   * specified {@link Collection}.
   *
   * In other words, removes all elements that are not contained in the
   * specified {@code Collection}.
   *
   * @param others a {@code Collection} containing elements to be retained in
   *                this instance
   * @return {@code true} if this instance changed as a result of the call
   * @see #contains(Object)
   */
  @Override
  public boolean retainAll(final Collection<?> others) {
    boolean hasChanged = false;
    for (Node node = this.first, previous = null; node != null; previous = node, node = node.next) {
      boolean retain = false;
      for (final Object other : others) {
        if (node.element.equals(other)) {
          retain = true;
          break;
        }
      }
      if (!retain) {
        if (previous != null) {
          previous.next = node.next;
        } else {
          this.first = node.next;
        }
        this.size -= 1;
        hasChanged = true;
      }
    }
    return hasChanged;
  }

  /**
   * Returns the number of elements in this instance.
   *
   * @return the number of elements in this instance
   */
  @Override
  public int size() {
    return this.size;
  }

  /**
   * Returns an array containing all of the {@link SpecificationElement}s in
   * this instance in proper sequence (ordered by their {@link Branch}es and
   * {@link SpecificationElement#compareTo(SpecificationElement)})).
   *
   * <p>
   * The returned array will be "safe" in that no references to it are
   * maintained by this instance.  (In other words, this method must allocate
   * a new array).  The caller is thus free to modify the returned array.
   *
   * @return an array containing all of the elements in this instance in
   *         proper sequence
   */
  @Override
  public SpecificationElement[] toArray() {
    final SpecificationElement[] array = new SpecificationElement[this.size];
    int i = 0;
    for (Node node = this.first; node != null; node = node.next) {
      array[i++] = node.element;
    }
    return array;
  }

  /**
   * Returns an array containing all of the {@link SpecificationElement}s in
   * this instance in proper sequence (ordered by their {@link Branch}es and
   * {@link SpecificationElement#compareTo(SpecificationElement)})).
   *
   * The runtime type of the returned array is that of the specified array.
   * If all elements fit in the specified array, it is returned therein.
   * Otherwise, a new array is allocated with the runtime type of the
   * specified array and the size of this instance.
   *
   * @param array the array into which the elements are to be stored, if it is
   *              big enough; otherwise, a new array of the same runtime type
   *              is allocated for this purpose
   * @return an array containing the elements of this instance
   * @throws ClassCastException if the runtime type of the specified array is
   *                            not a supertype of the runtime type of every
   *                            element in this list
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(final T[] array) {
    final T[] result =
        array.length >= this.size
            ? array
            : (T[]) Array.newInstance(array.getClass().getComponentType(), this.size);
    int i = 0;
    for (Node node = this.first; node != null; node = node.next) {
      result[i++] = (T) node.element;
    }
    return result;
  }

  /**
   * Returns a String representation of this instance.
   *
   * The result is all {@link SpecificationElement}s sorted and separated by
   * commas ({@code ,}) and surrounded by square brackets ({@code [},
   * {@code ]}).
   *
   * <p>
   * An example may look like this:
   * {@code [[1.0.0,1.5.0),(1.5.0,),1.5.0-featureA,1.4.9-featureB]}
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    if (this.first == null) {
      return "[]";
    }
    final StringBuilder string = new StringBuilder(64);
    string.append('[');
    string.append(this.first.element.toString());
    for (Node node = this.first.next; node != null; node = node.next) {
      string.append(',');
      string.append(node.element.toString());
    }
    string.append(']');
    return string.toString();
  }

  /**
   * Returns a new {@link UnmodifiableSortedElementSet} containing all {@link
   * SpecificationElement} this instance contains.
   *
   * The returned instance will be "safe" in that no references to it are
   * maintained by this instance or vice versa.  The caller is thus free to
   * modify this instance.
   *
   * @return a new, unmodifiable copy of this instance
   */
  public UnmodifiableSortedElementSet unmodifiableClone() {
    final UnmodifiableSortedElementSet clone = new UnmodifiableSortedElementSet();
    this.copyMembersInto(clone);
    return clone;
  }

  /**
   * Calculates and returns a new {@link SortedElementSet} representing the
   * intersection of this and a specified {@code SortedElementSet}.
   *
   * <p>
   * More formally, an intersection of two {@code SortedElementSet}s {@code A}
   * and {@code B} is defined for all {@link Version}s {@code v} as
   * {@code A ∩ B = {v | v ∈ A and v ∈ B}}.
   * If the two {@code SortedElementSet}s do not intersect the result will be
   * empty.
   *
   * @param other a {@code SortedElementSet} to calculate an intersection with
   * @return a new {@code SortedElementSet} representing the intersection
   * @see SpecificationElement#getIntersection(SpecificationElement)
   */
  public SortedElementSet getIntersection(final SortedElementSet other) {
    return new IntersectionCalculator(this, other).get();
  }

  @Override
  public int hashCode() {
    int hash = 1;
    for (Node node = this.first; node != null; node = node.next) {
      hash = 31 * hash + node.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof final SortedElementSet that)) {
      return false;
    }
    for (Node left = this.first, right = that.first;
        left != null || right != null;
        left = left.next, right = right.next) {
      if (!Objects.equals(left, right)) {
        return false;
      }
    }
    return true;
  }

  private void prepend(final SpecificationElement element) {
    final Node node = new Node(element);
    node.next = this.first;
    this.first = node;
    this.size += 1;
  }

  private void insertAfter(final SpecificationElement element, final Node node) {
    final Node newNode = new Node(element);
    newNode.next = node.next;
    node.next = newNode;
    this.size += 1;
  }

  private void copyMembersInto(final SortedElementSet set) {
    for (Node left = this.first, right = null; left != null; left = left.next) {
      final Node node = new Node(left.element);
      if (right == null) {
        set.first = node;
      } else {
        right.next = node;
      }
      right = node;
    }
    set.size = this.size;
  }
}
