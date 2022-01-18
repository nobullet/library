package com.nobullet.algo;

import com.nobullet.list.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Knapsack problem and solutions.
 */
public final class Knapsack {

  public int calls = 0;
  private int unboundedCalls;
  private int cacheHits;

  public Knapsack() {
  }

  // All complexity: O(2^n) as decision tree has height of n (with each element on the level to take or not):
  // 1 + 2 + 4 + ... + 2^(n) => 2^(n+1) - 1
  // The space complexity is O(n) (recursive calls are depth first with max number of stacks == n).
  public int bruteForce(int capacity, int[] weights, int[] profits) {
    return bruteForce(capacity, weights, profits, 0);
  }

  private int bruteForce(int capacity, int[] weights, int[] profits, int currentIndex) {
    if (currentIndex >= weights.length || capacity <= 0) {
      return 0;
    }
    calls++;
    int profitIfTake = 0;
    if (weights[currentIndex] <= capacity) {
      profitIfTake =
          profits[currentIndex] +
              bruteForce(capacity - weights[currentIndex], weights, profits, currentIndex + 1);
    }
    int profitIfSkip = bruteForce(capacity, weights, profits, currentIndex + 1);
    return Math.max(profitIfSkip, profitIfTake);
  }

  // All complexity: O(n * C) because there will be no more problems than n * C.
  // The space complexity is O(n * C) - cache size.
  public int bruteForceWMemoization(int capacity, int[] weights, int[] profits) {
    Integer[][] cache = new Integer[profits.length][capacity + 1];
    return bruteForceWMemoization(cache, capacity, weights, profits, 0);
  }

  private int bruteForceWMemoization(Integer[][] cache, int capacity, int[] weights, int[] profits, int currentIndex) {
    if (currentIndex >= weights.length || capacity <= 0) {
      return 0;
    }
    if (cache[currentIndex][capacity] != null) {
      return cache[currentIndex][capacity];
    }
    calls++;
    int profitIfTake = 0;
    if (weights[currentIndex] <= capacity) {
      profitIfTake =
          profits[currentIndex] +
              bruteForceWMemoization(cache, capacity - weights[currentIndex], weights, profits, currentIndex + 1);
    }
    int profitIfSkip = bruteForceWMemoization(cache, capacity, weights, profits, currentIndex + 1);
    return (cache[currentIndex][capacity] = Math.max(profitIfSkip, profitIfTake));
  }

  public int unboundedRecursive(int capacity, int[] weights, int[] profits) {
    this.unboundedCalls = 0;
    this.cacheHits = 0;
    Map<String, Integer> cache = new HashMap<>();
    int result = unboundedRecursive(capacity, weights, profits, 0, cache);
    // cache.size()
    return result;
  }

  // Will not have more than N*CN∗C subproblems (where ‘N’ is the number of items and ‘C’ is the knapsack capacity).
  // This means that our time complexity will be O(N*C).
  // Space: O(N) space for the recursion call-stack + O(N*C) space for the memoization array.
  // => O(N*C)
  private int unboundedRecursive(int capacity, int[] weights, int[] profits,
                                 int currentItemIndex,
                                 Map<String, Integer> cache) {
    this.unboundedCalls++;
    if (capacity <= 0 || currentItemIndex >= weights.length) {
      this.cacheHits++;
      return 0;
    }
    String key = capacity + "_" + currentItemIndex;
    Integer cached = cache.get(key);
    if (cached != null) {
      this.cacheHits++;
      return cached;
    }
    int profitIfTaken = 0;
    if (weights[currentItemIndex] <= capacity) {
      // Note how currentItemIndex stays the same -> not incremented but capacity is reduced, profits
      // are increased.
      profitIfTaken = profits[currentItemIndex]
          + unboundedRecursive(capacity - weights[currentItemIndex],
          weights, profits, currentItemIndex, cache);
    }
    int profitIfNotTaken = unboundedRecursive(capacity,
        weights, profits, currentItemIndex + 1, cache);
    int result = Math.max(profitIfNotTaken, profitIfTaken);
    cache.put(key, result);
    return result;
  }

  public int unboundedWMatrix(int capacity, int[] weights, int[] profits) {
    int[][] dp = new int[weights.length][capacity + 1];
    // Fill 0 capacity column.
    for (int itemIndex = 0; itemIndex < weights.length; itemIndex++) {
      dp[itemIndex][0] = 0; // Profit for capacity 0 is 0.
    }
    // Fill 1st (0) item row.
    for (int currentCapacity = 1; currentCapacity < capacity + 1; currentCapacity++) {
      if (weights[0] <= currentCapacity) {
        // weight: 3, capacity: 16 => item can be taken floor(16 / 3) times
        dp[0][currentCapacity] = profits[0] * (currentCapacity / weights[0]);
      }
    }
    for (int itemIndex = 1; itemIndex < weights.length; itemIndex++) {
      for (int currentCapacity = 1; currentCapacity < capacity + 1; currentCapacity++) {
        int profitIfTaken = 0;
        if (weights[itemIndex] <= currentCapacity) {
          profitIfTaken = profits[itemIndex] +
              dp[itemIndex][currentCapacity - weights[itemIndex]]; //
        }
        int profitIfNotTaken = dp[itemIndex - 1][currentCapacity];
        dp[itemIndex][currentCapacity] = Math.max(profitIfTaken, profitIfNotTaken);
      }
    }
    return dp[weights.length - 1][capacity];
  }

  /**
   * Solves the unbounded Knapsack problem in greedy manner (by George Dantzig). Optimal result is not guaranteed.
   *
   * @param weight         Size of knapsack.
   * @param valueWeightMap Value (key) / weight (value) map.
   * @return Values of items with sub-optimal solution of unbounded knapsack problem.
   */
  public static List<Integer> greedy(int weight, Map<Integer, Integer> valueWeightMap) {
    if (weight <= 0) {
      throw new IllegalArgumentException("Weight must be positive.");
    }
    if (valueWeightMap.isEmpty()) {
      throw new IllegalArgumentException("Weights and values must not be empty.");
    }
    KnapsackItem.Ratio[] ratios = new KnapsackItem.Ratio[valueWeightMap.size()];
    int i = 0;
    for (Map.Entry<Integer, Integer> valueWeight : valueWeightMap.entrySet()) {
      ratios[i++] = new KnapsackItem.Ratio(valueWeight.getKey(), valueWeight.getValue());
    }
    Arrays.sort(ratios);
    List<Integer> result = new ArrayList<>();
    i = ratios.length - 1;
    while (i >= 0) {
      int value = ratios[i].item.value;
      while (weight >= value) {
        weight -= value;
        result.add(value);
      }
      if (weight == 0) {
        break;
      }
      i--;
    }
    return result;
  }

  /**
   * Solves 0/1 Knapsack problem for given arguments.
   *
   * @param weight  Knapsack weight (size).
   * @param weights Weights of items.
   * @param values  Values of items.
   * @return Optimal solution of 0/1 Knapsack problem.
   */
  public static List<KnapsackItem> zeroOne(int weight, int[] weights, int[] values) {
    if (values == null || values.length < 1 || weights == null || weights.length != values.length) {
      throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
    }
    Set<KnapsackItem> items = new HashSet<>();
    for (int i = 0; i < weights.length; i++) {
      items.add(new KnapsackItem(values[i], weights[i]));
    }
    return generic(weight, items);
  }

  /**
   * Solves 0/1 Knapsack problem for given arguments.
   *
   * @param weight Knapsack weight (size).
   * @param items  Set of items.
   * @return Optimal solution of 0/1 Knapsack problem.
   */
  public static List<KnapsackItem> zeroOne(int weight, Set<KnapsackItem> items) {
    return generic(weight, items);
  }

  /**
   * Solves bounded Knapsack problem for given arguments.
   *
   * @param weight     Knapsack weight (size).
   * @param weights    Weights of items.
   * @param values     Values of items.
   * @param quantities Quantities of items.
   * @return Optimal solution of bounded Knapsack problem.
   */
  public static List<KnapsackItem.RepeatableItem> bounded(int weight, int[] weights, int[] values, int[] quantities) {
    if (values == null || values.length < 1 || weights == null || weights.length != values.length
        || quantities.length != values.length) {
      throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
    }
    Set<KnapsackItem.RepeatableItem> items = new HashSet<>();
    for (int i = 0; i < weights.length; i++) {
      items.add(new KnapsackItem.RepeatableItem(values[i], weights[i], quantities[i]));
    }
    return bounded(weight, items);
  }

  /**
   * Solves bounded Knapsack problem for given arguments.
   *
   * @param weight Knapsack weight (size).
   * @param items  Repeatable items.
   * @return Optimal solution of bounded Knapsack problem in the for of repeatable items.
   */
  public static List<KnapsackItem.RepeatableItem> bounded(int weight, Set<? extends KnapsackItem.RepeatableItem> items) {
    return Lists.compact(genericWithIterable(weight, new KnapsackItem.RepeatableItemsIterable(items)), KnapsackItem.RepeatableItem::new);
  }

  /**
   * Solves generic Knapsack problem for given arguments.
   *
   * @param weight     Knapsack weight (size).
   * @param weights    Weights of items.
   * @param values     Values of items.
   * @param quantities Quantities of items.
   * @return Optimal solution of bounded Knapsack problem.
   */
  public static List<KnapsackItem> generic(int weight, int[] weights, int[] values, int[] quantities) {
    if (values == null || values.length < 1 || weights == null || weights.length != values.length
        || quantities.length != values.length) {
      throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
    }
    Set<KnapsackItem.RepeatableItem> items = new HashSet<>();
    for (int i = 0; i < weights.length; i++) {
      items.add(new KnapsackItem.RepeatableItem(values[i], weights[i], quantities[i]));
    }
    return genericWithIterable(weight, new KnapsackItem.RepeatableItemsIterable(items));
  }

  /**
   * Generic solution for Knapsack problem.
   *
   * @param weight Maximum weight.
   * @param items  Items to place into the Knapsack. May have duplicates.
   * @return Optimal solution.
   */
  public static List<KnapsackItem> generic(int weight, Collection<? extends KnapsackItem> items) {
    return genericWithIterable(weight, items);
  }

  /**
   * Generic solution for Knapsack problem.
   *
   * @param weight Maximum weight.
   * @param items  Items to place into the Knapsack. May have duplicates.
   * @return Optimal solution.
   */
  public static List<KnapsackItem> genericWithIterable(int weight, Iterable<? extends KnapsackItem> items) {
    if (weight <= 0) {
      throw new IllegalArgumentException("Weight must be positive.");
    }
    if (items == null) {
      throw new IllegalArgumentException("Items collection might not be null.");
    }
    //int i;
    int wi;
    int vi;
    int previousValueForLessItems;
    int previousValueForLessItemsAndWeight;
    int currentValue;
    //int[][] matrix = new int[weight + 1][itemsSize + 1];
    int[] previousRow = new int[weight + 1];
    int[] currentRow = new int[weight + 1];
    int[] temp;
    Map<KnapsackItem, KnapsackItem> cameFromItems = new HashMap<>(); // Edges for the path.
    for (KnapsackItem item : items) {
      for (int w = 0; w <= weight; w++) {
        vi = item.value;
        wi = item.weight;
        // maximum value for (i - 1) items and weight w.
        //#previousValueForLessItems = matrix[w][i - 1];
        previousValueForLessItems = previousRow[w];
        if (wi > w) {
          //#matrix[w][i] = previousValueForLessItems;
          currentRow[w] = previousValueForLessItems;
        } else {
          // maximum value for (i - 1) items and weight (w - wi).
          //#previousValueForLessItemsAndWeight = matrix[w - wi][i - 1];
          previousValueForLessItemsAndWeight = previousRow[w - wi];
          currentValue = previousValueForLessItemsAndWeight + vi;
          if (currentValue > previousValueForLessItems) {
            //#matrix[w][i] = currentValue;
            currentRow[w] = currentValue;
            // If the current value is improved previous result remember the improvement in possible paths.
            if (w > 0 && currentValue > currentRow[w - 1]) {
              cameFromItems.put(new KnapsackItem(currentValue, w), item);
            }
          } else {
            //#matrix[w][i] = previousValueForLessItems;
            currentRow[w] = previousValueForLessItems;
          }
        }
      }
      temp = previousRow;
      previousRow = currentRow;
      currentRow = temp;
      //i++;
    }
    // Now find the optimal total weight for the optimal total value.
    vi = weight;
    int optimalSolution = previousRow[vi];
    while (vi > 0 && previousRow[vi - 1] == optimalSolution) {
      vi--;
    }
    // Restore the solution.
    List<KnapsackItem> solution = new ArrayList<>();
    KnapsackItem remaining = new KnapsackItem(optimalSolution, vi);
    while (cameFromItems.containsKey(remaining)) {
      KnapsackItem solutionItem = cameFromItems.get(remaining);
      solution.add(solutionItem);
      remaining = new KnapsackItem(remaining.value - solutionItem.value, remaining.weight - solutionItem.weight);
    }
    // Sort in ascending order.
    Collections.sort(solution);
    return solution;
  }
}
