package com.sitepark.versioning.version.specification;

import com.sitepark.versioning.version.VersionBuilder;
import com.sitepark.versioning.version.specification.element.ExplicitVersionElement;
import com.sitepark.versioning.version.specification.element.SortedElementBranchSet;
import com.sitepark.versioning.version.specification.element.SpecificationElement;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SortedElementBranchTest {

  @Test
  public void testIteratorRemoveFirst() {
    final SortedElementBranchSet set = new SortedElementBranchSet();
    set.addAll(
        Set.of(
            new ExplicitVersionElement(new VersionBuilder().setMajor(1).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(2).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(3).buildRelease())));
    final Iterator<SpecificationElement> iterator = set.iterator();
    final SpecificationElement first = iterator.next();
    iterator.remove();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertEquals(2, set.size());
    Assertions.assertFalse(set.contains(first));
  }

  @Test
  public void testIteratorRemoveMiddle() {
    final SortedElementBranchSet set = new SortedElementBranchSet();
    set.addAll(
        Set.of(
            new ExplicitVersionElement(new VersionBuilder().setMajor(1).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(2).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(3).buildRelease())));
    final Iterator<SpecificationElement> iterator = set.iterator();
    iterator.next();
    final SpecificationElement middle = iterator.next();
    iterator.remove();
    Assertions.assertTrue(iterator.hasNext());
    Assertions.assertEquals(2, set.size());
    Assertions.assertFalse(set.contains(middle));
  }

  @Test
  public void testIteratorRemoveLast() {
    final SortedElementBranchSet set = new SortedElementBranchSet();
    set.addAll(
        Set.of(
            new ExplicitVersionElement(new VersionBuilder().setMajor(1).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(2).buildRelease()),
            new ExplicitVersionElement(new VersionBuilder().setMajor(3).buildRelease())));
    final Iterator<SpecificationElement> iterator = set.iterator();
    iterator.next();
    iterator.next();
    final SpecificationElement last = iterator.next();
    iterator.remove();
    Assertions.assertFalse(iterator.hasNext());
    Assertions.assertEquals(2, set.size());
    Assertions.assertFalse(set.contains(last));
  }

  @Test
  public void testIteratorRemoveOnlyElement() {
    final SortedElementBranchSet set = new SortedElementBranchSet();
    set.add(new ExplicitVersionElement(new VersionBuilder().setMajor(3).buildRelease()));
    final Iterator<SpecificationElement> iterator = set.iterator();
    final SpecificationElement item = iterator.next();
    iterator.remove();
    Assertions.assertFalse(iterator.hasNext());
    Assertions.assertEquals(0, set.size());
    Assertions.assertFalse(set.contains(item));
  }
}
