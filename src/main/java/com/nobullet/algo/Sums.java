package com.nobullet.algo;

import java.util.HashMap;
import java.util.Map;

/**
 * Sums problem.
 */
public final class Sums {

  private final int[] values;

  public Sums(int[] values) {
    this.values = values;
  }

  // Given a set of positive numbers, partition the set into two subsets with minimum difference between their subset\
  // sums. Example: {1, 2, 7, 1, 5} -> 0 because {1, 2, 5} & {7, 1}.
  public int minDiffSubsets() {
    Map<String, Integer> memoize = new HashMap<>();
    int result = minDiffSubsets(0, 0, 0, memoize);
    int expectedNestedArraySize = (getSum() / 2 + 1) * values.length;
    int sparseCacheSize = memoize.size();
    return result;
  }

  private int minDiffSubsets(int currentIndex, int sum1SoFar, int sum2SoFar, Map<String, Integer> memoize) {
    if (currentIndex == values.length) {
      return Math.abs(sum1SoFar - sum2SoFar);
    }
    String key = sum1SoFar + "_" + currentIndex; // Using sum1SoFar as sum2 can be identified by the remaining items.
    Integer cached = memoize.get(key);
    if (cached != null) {
      return cached;
    }
    int newSum1 = minDiffSubsets(currentIndex + 1, sum1SoFar + values[currentIndex], sum2SoFar, memoize);
    int newSum2 = minDiffSubsets(currentIndex + 1, sum1SoFar, sum2SoFar + values[currentIndex], memoize);
    int result = Math.min(newSum1, newSum2);
    memoize.put(key, result);
    return result;
  }

  public int minDiffSubsetsWMatrix() {
    checkGreaterOrEquals0();
    int fullSum = getSum();
    int maxSumToSearch = fullSum / 2 + 1;
    if (maxSumToSearch == 0) {
      return 0;
    }
    int i, currentSum;
    boolean[][] dp = new boolean[values.length][maxSumToSearch];
    for (i = 0; i < values.length; i++) {
      dp[i][0] = true; // empty set has sum of 0 => true
    }
    for (currentSum = 1; currentSum < maxSumToSearch; currentSum++) {
      dp[0][currentSum] = values[0] == currentSum; // calculate the 1st row for 1 item, true for 0 and for item itself
    }
    for (i = 1; i < values.length; i++) {
      for (currentSum = 1; currentSum < maxSumToSearch; currentSum++) {
        boolean ifNotTaken = dp[i - 1][currentSum];
        boolean ifTaken = false;
        if (values[i] <= currentSum) {
          ifTaken = dp[i - 1][currentSum - values[i]];
        }
        dp[i][currentSum] = ifTaken || ifNotTaken;
      }
    }
    currentSum = maxSumToSearch - 1;
    while (!dp[values.length - 1][currentSum]) {
      currentSum--;
    }
    return Math.abs(fullSum - 2 * currentSum);
  }

  // Given a set of positive numbers, determine if a subset exists whose sum is equal to a given number ‘S’.
  public boolean subsetExistsForSum(int sum) {
    checkGreaterOrEquals0();
    Map<String, Boolean> memoize = new HashMap<>();
    boolean solutionExists = subsetExistsForSum(sum, 0, memoize);
    int expectedNestedArraySize = (sum + 1) * values.length;
    int sparseCacheSize = memoize.size();
    int allSum = getSum();
    // Restore the solution from memoize if needed.
    return solutionExists;
  }

  private boolean subsetExistsForSum(int sumSoFar, int itemIndex, Map<String, Boolean> memoize) {
    if (sumSoFar == 0) {
      return true;
    }
    if (sumSoFar < 0 || itemIndex >= values.length) {
      return false;
    }
    String memoizeKey = sumSoFar + "_" + itemIndex;
    Boolean cachedValue = memoize.get(memoizeKey);
    if (cachedValue != null) {
      return cachedValue;
    }
    if (values[itemIndex] <= sumSoFar) {
      boolean ifTaken = subsetExistsForSum(sumSoFar - values[itemIndex], itemIndex + 1, memoize);
      memoize.put(memoizeKey, ifTaken);
      if (ifTaken) {
        return true;
      }
    }
    boolean ifNotTaken = subsetExistsForSum(sumSoFar, itemIndex + 1, memoize);
    memoize.put(memoizeKey, ifNotTaken);
    return ifNotTaken;
  }

  public int numberOfSubsetsWithSumWMatrix(int sum) {
    int[][] result = new int[values.length][sum + 1];
    for (int i = 0; i < values.length; i++) {
      result[i][0] = 1; // Always have an empty set for 0 sum!
    }
    for (int currentSum = 1; currentSum < sum + 1; currentSum++) {
      result[0][currentSum] = currentSum == values[0] ? 1 : 0;
    }
    for (int i = 1; i < values.length; i++) {
      for (int currentSum = 1; currentSum < sum + 1; currentSum++) {
        int numberOfSubsetsIfTaken = 0;
        if (values[i] <= currentSum) {
          numberOfSubsetsIfTaken = result[i - 1][currentSum - values[i]]; // take the value
        }
        int numberOfSubsetsIfNotTaken = result[i - 1][currentSum]; // previous value for same sum
        result[i][currentSum] = numberOfSubsetsIfNotTaken + numberOfSubsetsIfTaken;
      }
    }
    return result[values.length - 1][sum];
  }

  public int numberOfSubsetsWithSum(int sum) {
    Map<String, Integer> memoize = new HashMap<>();
    int result = numberOfSubsetsWithSum(sum, 0, memoize);
    //memoize.size() vs values.length * (sum + 1))
    return result;
  }

  public int numberOfSubsetsWithSum(int sumSoFar, int index, Map<String, Integer> memoize) {
    String cacheKey = sumSoFar + "_" + index;
    Integer cached = memoize.get(cacheKey);
    if (cached != null) {
      return cached;
    }
    if (sumSoFar == 0) {
      return 1;
    }
    if (sumSoFar < 0 || index >= values.length || values.length == 0) {
      return 0;
    }
    int sumWithTakingAnItem = 0;
    if (values[index] <= sumSoFar) {
      sumWithTakingAnItem = numberOfSubsetsWithSum(sumSoFar - values[index], index + 1, memoize);
    }
    int sumWithoutTakingAnItem = numberOfSubsetsWithSum(sumSoFar, index + 1, memoize);
    int result = sumWithTakingAnItem + sumWithoutTakingAnItem;
    memoize.put(cacheKey, result);
    return result;
  }

  public int getSum() {
    int sum = 0;
    for (int i = 0; i < values.length; i++) {
      sum += values[i];
    }
    return sum;
  }

  private void checkGreaterOrEquals0() {
    for (int i = 0; i < values.length; i++) {
      if (values[i] < 0) {
        throw new IllegalArgumentException("There is a negative value in the set at index: " + i);
      }
    }
  }
}
