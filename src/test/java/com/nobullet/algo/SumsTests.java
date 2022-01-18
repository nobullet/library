package com.nobullet.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class SumsTests {

  @Test
  public void testMinDiffSubsets() {
    Sums sums = new Sums(new int[] {1, 2, 3, 9});
    assertEquals(3, sums.minDiffSubsets()); //{1, 2, 3} & {9}.
    assertEquals(3, sums.minDiffSubsetsWMatrix()); //{1, 2, 3} & {9}.


    sums = new Sums(new int[] {1, 3, 100, 4});
    assertEquals(92, sums.minDiffSubsets()); // {1, 3, 4} & {100}.
    assertEquals(92, sums.minDiffSubsetsWMatrix()); // {1, 3, 4} & {100}.


    sums = new Sums(new int[] {1, 2, 7, 1, 5});
    assertEquals(0, sums.minDiffSubsets()); // {1, 2, 5} & {7, 1}.
    assertEquals(0, sums.minDiffSubsetsWMatrix()); // {1, 2, 5} & {7, 1}.
  }

  @Test
  public void testsSubsetsSum() {
    Sums sums = new Sums(new int[] {0, 2, 1, 2, 16, 38, 11, 7});

    assertTrue(sums.subsetExistsForSum(10));
    assertTrue(sums.subsetExistsForSum(3));
    assertTrue(sums.subsetExistsForSum(27));
    assertTrue(sums.subsetExistsForSum(23));
    assertTrue(sums.subsetExistsForSum(16 + 38));
    assertTrue(sums.subsetExistsForSum(5));
    assertTrue(sums.subsetExistsForSum(18));
    assertTrue(sums.subsetExistsForSum(0));
    assertTrue(sums.subsetExistsForSum(sums.getSum()));

    assertFalse(sums.subsetExistsForSum(6));
    for (int sum = sums.getSum(), i = sum + 1; i < sum + 10; i++) {
      assertFalse("Doesn't exist for sum: " + i, sums.subsetExistsForSum(i));
    }
  }

  @Test
  public void testNumberOfSubsets() {
    Sums sums = new Sums(new int[] {1, 1, 2, 3});
    assertEquals(sums.numberOfSubsetsWithSum(4), 3);
    assertEquals(sums.numberOfSubsetsWithSumWMatrix(4), 3);

    sums = new Sums(new int[] {1, 2, 7, 1, 5});
    assertEquals(sums.numberOfSubsetsWithSum(9), 3);
    assertEquals(sums.numberOfSubsetsWithSumWMatrix(9), 3);
  }
}
