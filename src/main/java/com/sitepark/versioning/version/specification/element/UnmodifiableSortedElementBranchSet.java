package com.sitepark.versioning.version.specification.element;

import com.sitepark.versioning.Branch;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A immutable version of a {@link SortedElementBranchSet}.
 *
 * This class is only instantiatable from
 * {@link SortedElementBranchSet#unmodifiableClone()}.
 */
public final class UnmodifiableSortedElementBranchSet
    extends MapBasedElementBranchSet<UnmodifiableSortedElementSet> {
  private static final long serialVersionUID = 4984005150728968417L;

  private final int size;

  UnmodifiableSortedElementBranchSet(final Map<Branch, UnmodifiableSortedElementSet> branchMap) {
    super(branchMap);
    int size = 0;
    for (final SortedElementSet list : branchMap.values()) {
      size += list.size();
    }
    this.size = size;
  }

  @Override
  protected UnmodifiableSortedElementSet createItem() {
    return new UnmodifiableSortedElementSet();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param element the element to add
   * @return {@code true} if this instance changed as a result of the call
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean add(final SpecificationElement element) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param elements the elements to add
   * @return {@code true} if this instance changed as a result of the call
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean addAll(final Collection<? extends SpecificationElement> elements)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @throws UnsupportedOperationException always
   */
  @Override
  public void clear() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param object the object to remove
   * @return {@code true} if this instance contained the specified element
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean remove(final Object object) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param objects the objects to remove
   * @return {@code true} if this instance contained the any of the specified
   *         elements
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean removeAll(final Collection<?> objects) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param objects the objects to retain
   * @return {@code true} if this instance changed as a result of the call
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean retainAll(final Collection<?> objects) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  /**
   * Is not supported by this {@link ElementBranchSet} and causes a
   * {@link UnsupportedOperationException}.
   *
   * @param filter a filter to determine which items to remove
   * @return {@code true} if this instance changed as a result of the call
   * @throws UnsupportedOperationException always
   */
  @Override
  public boolean removeIf(final Predicate<? super SpecificationElement> filter)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
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

  @Override
  public Iterator<SpecificationElement> iterator() {
    return super.unmodifiableIterator();
  }

  /**
   * Returns a {@link SortedElementBranchSet} of the intersection of this and
   * another instance such that {@code A ∩ B}.
   *
   * @param other another {@code ElementBranchSet} to calculate an
   *        intersection with
   * @return a new {@code SortedElementBranchSet} depicting an intersection of
   *         this and the specified instance; May be empty if there is none
   */
  @Override
  public SortedElementBranchSet getIntersection(final ElementBranchSet other) {
    return super.getIntersection((MapBasedElementBranchSet<?>) other);
  }
}
