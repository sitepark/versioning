package com.sitepark.versioning.version.specification.element;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * A immutable version of a {@link SortedElementSet}.
 *
 * This class is only instantiatable from
 * {@link SortedElementSet#unmodifiableClone()}.
 */
public final class UnmodifiableSortedElementSet extends SortedElementSet {
	private static final long serialVersionUID = -2308872241488154021L;

	/**
	 * Class Constructor.
	 */
	UnmodifiableSortedElementSet() {
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @param element the element to add
	 * @return {@code true} if this instance changed as a result of the call
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean add(final SpecificationElement element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @param elements the elements to add
	 * @return {@code true} if this instance changed as a result of the call
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean addAll(
			final Collection<? extends SpecificationElement> elements) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean remove(final Object object) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @param filter a filter to determine which items to remove
	 * @return {@code true} if this instance changed as a result of the call
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean removeIf(
			final Predicate<? super SpecificationElement> filter) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @param objects the objects to remove
	 * @return {@code true} if this instance contained the any of the specified
	 *         elements
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean removeAll(final Collection<?> objects) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @param objects the objects to retain
	 * @return {@code true} if this instance changed as a result of the call
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean retainAll(final Collection<?> objects) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Is not supported by this {@link SortedElementSet} and causes a
	 * {@link UnsupportedOperationException}.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
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
	@Override
	public Iterator<SpecificationElement> iterator() {
		return super.unmodifiableIterator();
	}

	/**
	 * Returns this instance.
	 *
	 * @return this instance
	 */
	@Override
	public UnmodifiableSortedElementSet unmodifiableClone() {
		return this;
	}
}