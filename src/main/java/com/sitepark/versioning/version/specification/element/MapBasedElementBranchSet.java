package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import com.sitepark.versioning.version.Version;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

abstract class MapBasedElementBranchSet<E extends SortedElementSet> implements ElementBranchSet {
  private static final long serialVersionUID = -4164300350265933240L;

  private final Map<Branch, E> branchMap;

  MapBasedElementBranchSet() {
    this(new HashMap<>());
  }

  MapBasedElementBranchSet(final Map<Branch, E> branchMap) {
    this.branchMap = branchMap;
  }

  protected abstract E createItem();

  @Override
  public boolean containsVersion(final Version version) {
    final E set = this.branchMap.get(version.getBranch());
    if (set == null) {
      return false;
    }
    for (final SpecificationElement element : set) {
      if (element.containsVersion(version)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes all {@link SpecificationElement}s.
   */
  public void clear() {
    this.branchMap.clear();
  }

  /**
   * Returns {@code true} if this instance contains the specified element.
   *
   * More formally, returns {@code true} if and only if this instance contains
   * at least one {@code element} such that
   * {@code Objects.equals(object, element)}.
   *
   * @param object the object whose presence is to be tested
   * @return {@code true} if this instance contains the specified element
   * @see #containsAll(Collection)
   */
  public boolean contains(final Object object) {
    if (!(object instanceof SpecificationElement)) {
      return false;
    }
    final SpecificationElement element = (SpecificationElement) object;
    final E list = this.branchMap.get(element.getBranch());
    return list != null ? list.contains(element) : false;
  }

  /**
   * Returns {@code true} if this instance contains all of the elements in the
   * specified {@link Collection}.
   *
   * @param objects {@code Collection} to be checked for containment
   * @return {@code true} if this instance contains all of the elements in the
   *         specified collection
   * @see #contains(Object)
   */
  public boolean containsAll(final Collection<?> objects) {
    for (final Object object : objects) {
      if (!this.contains(object)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Adds the specified {@link SpecificationElement} to this instance.
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
    E set = this.branchMap.get(element.getBranch());
    if (set != null) {
      return set.add(element);
    }
    set = this.createItem();
    if (set.add(element)) {
      this.branchMap.put(element.getBranch(), set);
      return true;
    }
    return false;
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
    if (!(other instanceof SpecificationElement)) {
      return false;
    }
    final Branch branch = ((SpecificationElement) other).getBranch();
    final SortedElementSet set = this.branchMap.get(branch);
    if (set == null || set.remove(other)) {
      return false;
    }
    if (set.isEmpty()) {
      this.branchMap.remove(branch, set);
    }
    return true;
  }

  /**
   * Removes all elements that are contained in the specified
   * {@link Collection} and this instance.
   *
   * @param objects a {@code Collection} containing elements to be removed
   *                from this instance
   * @return {@code true} if this instance changed as a result of the call
   * @see #remove(Object)
   * @see #removeIf(java.util.function.Predicate)
   * @see #contains(Object)
   */
  @Override
  public boolean removeAll(final Collection<?> objects) {
    boolean hasChanged = false;
    for (final Object object : objects) {
      hasChanged = this.remove(object) || hasChanged;
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
   * @param objects a {@code Collection} containing elements to be retained in
   *                this instance
   * @return {@code true} if this instance changed as a result of the call
   * @see #contains(Object)
   */
  @Override
  public boolean retainAll(final Collection<?> objects) {
    for (final Map.Entry<Branch, E> entry : this.branchMap.entrySet()) {
      final SortedElementSet list = entry.getValue();
      if (list.retainAll(objects) && list.isEmpty()) {
        this.branchMap.remove(entry.getKey(), list);
      }
    }
    return false;
  }

  /**
   * Returns {@code true} if this instance contains no elements.
   *
   * @return {@code true} if this instance contains no elements
   */
  public boolean isEmpty() {
    return this.branchMap.isEmpty();
  }

  /**
   * Returns an {@link Iterator} over the elements in this instance.
   *
   * The order is determined by the {@link Branch}es and
   * {@link SpecificationElement}e dieses {@link Set}s.
   *
   * <p>
   * <strong>Warning</strong>: This {@code Iterator} does not check for any
   * concurrent modifications!
   */
  @Override
  public Iterator<SpecificationElement> iterator() {
    return new Iterator<SpecificationElement>() {
      private final Iterator<E> iterator =
          MapBasedElementBranchSet.this.branchMap.values().iterator();
      private Iterator<SpecificationElement> current =
          this.iterator.hasNext() ? this.iterator.next().iterator() : null;
      private Iterator<SpecificationElement> next =
          this.iterator.hasNext() ? this.iterator.next().iterator() : null;

      @Override
      public boolean hasNext() {
        return (this.current != null && this.current.hasNext())
            || (this.next != null && this.next.hasNext());
      }

      @Override
      public SpecificationElement next() {
        if (!this.current.hasNext()) {
          this.current = this.next;
          this.next = this.iterator.hasNext() ? this.iterator.next().iterator() : null;
        }
        return this.current.next();
      }

      @Override
      public void remove() {
        if (this.current == null) {
          throw new IllegalStateException();
        }
        this.current.remove();
      }
    };
  }

  protected Iterator<SpecificationElement> unmodifiableIterator() {
    return new Iterator<SpecificationElement>() {
      private final Iterator<E> iterator =
          MapBasedElementBranchSet.this.branchMap.values().iterator();
      private Iterator<SpecificationElement> current =
          this.iterator.hasNext() ? this.iterator.next().iterator() : null;
      private Iterator<SpecificationElement> next =
          this.iterator.hasNext() ? this.iterator.next().iterator() : null;

      @Override
      public boolean hasNext() {
        return (this.current != null && this.current.hasNext())
            || (this.next != null && this.next.hasNext());
      }

      @Override
      public SpecificationElement next() {
        if (!this.current.hasNext()) {
          this.current = this.next;
          this.next = this.iterator.hasNext() ? this.iterator.next().iterator() : null;
        }
        return this.current.next();
      }
    };
  }

  /**
   * Returns the number of elements in this instance.
   *
   * @return the number of elements in this instance
   */
  public int size() {
    int size = 0;
    for (final SortedElementSet list : this.branchMap.values()) {
      size += list.size();
    }
    return size;
  }

  protected SortedElementBranchSet getIntersection(final MapBasedElementBranchSet<?> other) {
    final Set<Branch> branches = new HashSet<>(this.branchMap.keySet());
    branches.retainAll(other.branchMap.keySet());
    final Map<Branch, SortedElementSet> result = new HashMap<>();
    SortedElementSet intersection;
    for (final Branch branch : branches) {
      intersection = this.branchMap.get(branch).getIntersection(other.branchMap.get(branch));
      if (!intersection.isEmpty()) {
        result.put(branch, intersection);
      }
    }
    return new SortedElementBranchSet(result);
  }

  protected UnmodifiableSortedElementBranchSet unmodifiableClone() {
    return new UnmodifiableSortedElementBranchSet(
        this.branchMap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().unmodifiableClone())));
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
  public SpecificationElement[] toArray() {
    final SpecificationElement[] array = new SpecificationElement[this.size()];
    int index = 0;
    for (final E list : this.branchMap.values()) {
      for (final SpecificationElement element : list) {
        array[index++] = element;
      }
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
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(final T[] array) {
    final int size = this.size();
    final T[] result =
        array.length >= size
            ? array
            : (T[]) Array.newInstance(array.getClass().getComponentType(), size);
    int index = 0;
    for (final E list : this.branchMap.values()) {
      for (final SpecificationElement element : list) {
        result[index++] = (T) element;
      }
    }
    return result;
  }

  /**
   * Returns a String representation of this instance.
   *
   * The result is all {@link SpecificationElement}s sorted and grouped by
   * their {@link Branch}es, separated by commas ({@code ,}) and surrounded by
   * square brackets ({@code [}, {@code ]}).
   *
   * <p>
   * An example may look like this:
   * {@code [[1.0.0,1.5.0),(1.5.0,),1.5.0-featureA,1.4.9-featureB]}
   *
   * @return a descriptive String of this instance
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(64);
    builder.append('[');
    for (final Iterator<SpecificationElement> iterator = this.unmodifiableIterator();
        iterator.hasNext(); ) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append(',');
      }
    }
    builder.append(']');
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return this.branchMap.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }
    return this.branchMap.equals(((MapBasedElementBranchSet<?>) other).branchMap);
  }
}
