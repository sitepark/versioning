package com.sitepark.versioning;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BranchTest {

  @Test
  public void testIsMain() {
    final List<Branch> branches =
        List.of(
            new Branch("develop"),
            new Branch("DEVELOP"),
            Branch.DEVELOP,
            new Branch("main"),
            new Branch("MAIN"),
            Branch.MAIN,
            new Branch(""));
    for (final Branch branch : branches) {
      Assertions.assertTrue(branch.isDevelop());
      Assertions.assertTrue(branch.isMain());
      Assertions.assertFalse(branch.isFeature());
    }
  }

  @Test
  public void testIsFeature() {
    final List<Branch> branches =
        List.of(new Branch("my_feature"), new Branch("DEVE.LOP"), new Branch("_"));
    for (final Branch branch : branches) {
      Assertions.assertTrue(branch.isFeature());
      Assertions.assertFalse(branch.isDevelop());
      Assertions.assertFalse(branch.isMain());
    }
  }

  @Test
  public void testToString() {
    Assertions.assertEquals("main", new Branch("develop").toString());
    Assertions.assertEquals("main", new Branch("DEVELOP").toString());
    Assertions.assertEquals("main", Branch.DEVELOP.toString());
    Assertions.assertEquals("main", new Branch("main").toString());
    Assertions.assertEquals("main", new Branch("MAIN").toString());
    Assertions.assertEquals("main", Branch.MAIN.toString());
    Assertions.assertEquals("main", new Branch("").toString());
    Assertions.assertEquals("my_feature", new Branch("my_feature").toString());
  }

  @Test
  public void testMainIsGreaterThanFeature() {
    Assertions.assertTrue(Branch.MAIN.compareTo(new Branch("z_feature")) > 0);
    Assertions.assertTrue(Branch.MAIN.compareTo(new Branch("a_feature")) > 0);
  }

  @Test
  public void testFeatureCompareToIsAlphabetical() {
    Assertions.assertTrue(new Branch("a").compareTo(new Branch("b")) < 0);
    Assertions.assertTrue(new Branch("c").compareTo(new Branch("b")) > 0);
  }
}
