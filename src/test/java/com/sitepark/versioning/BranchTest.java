package com.sitepark.versioning;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BranchTest {

	@Test
	public void testDevelopIsDevelop() {
		Assertions.assertTrue(new Branch("develop").isDevelop());
		Assertions.assertTrue(new Branch("DEVELOP").isDevelop());
		Assertions.assertTrue(new Branch("").isDevelop());
		Assertions.assertTrue(Branch.DEVELOP.isDevelop());
	}

	@Test
	public void testFeatureIsFeature() {
		Assertions.assertTrue(new Branch("my_feature").isFeature());
		Assertions.assertTrue(new Branch("DEVE.LOP").isFeature());
		Assertions.assertTrue(new Branch("_").isFeature());
	}

	@Test
	public void testDevelopIsNotFeature() {
		Assertions.assertFalse(new Branch("develop").isFeature());
		Assertions.assertFalse(new Branch("DEVELOP").isFeature());
		Assertions.assertFalse(new Branch("").isFeature());
		Assertions.assertFalse(Branch.DEVELOP.isFeature());
	}

	@Test
	public void testFeatureIsNotDevelop() {
		Assertions.assertFalse(new Branch("my_feature").isDevelop());
		Assertions.assertFalse(new Branch("DEVE.LOP").isDevelop());
		Assertions.assertFalse(new Branch("_").isDevelop());
	}

	@Test
	public void testDevelopToString() {
		Assertions.assertEquals("develop", new Branch("develop").toString());
		Assertions.assertEquals("develop", new Branch("DEVELOP").toString());
		Assertions.assertEquals("develop", new Branch("").toString());
		Assertions.assertEquals("develop", Branch.DEVELOP.toString());
	}

	@Test
	public void testDevelopIsGreaterThanFeature() {
		Assertions.assertTrue(Branch.DEVELOP.compareTo(new Branch("z_feature")) > 0);
		Assertions.assertTrue(Branch.DEVELOP.compareTo(new Branch("a_feature")) > 0);
	}

	@Test
	public void testFeatureCompareToIsAlphabetical() {
		Assertions.assertTrue(new Branch("a").compareTo(new Branch("b")) < 0);
		Assertions.assertTrue(new Branch("c").compareTo(new Branch("b")) > 0);
	}
}
