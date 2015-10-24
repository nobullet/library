package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Coin/bill change problem: http://stackoverflow.com/questions/2633848/dynamic-programming-coin-change-decision?rq=1
 */
public class CoinChange {

    /**
     * Returns minimal number of coins to give change. Very slow as recalculates previous results again and again.
     *
     * @param sum Sum.
     * @param denominations Available denominations. Note: for US coins greedy solution works fine.
     * @return Minimal number of coins to give change.
     */
    public static int getMinimumNumberOfCoinsRecursive(int sum, Set<Integer> denominations) {
        if (sum <= 0 || denominations == null || denominations.isEmpty()) {
            throw new IllegalArgumentException("Expected non empty set of integers and positive sum.");
        }
        if (denominations.contains(sum)) {
            return 1;
        }
        int min = Integer.MAX_VALUE;
        int result = min;
        for (Integer denomination : denominations) {
            if (sum > denomination) {
                min = 1 + getMinimumNumberOfCoinsRecursive(sum - denomination, denominations);
                if (result > min) {
                    result = min;
                }
            }
        }
        if (result == Integer.MAX_VALUE) {
            return 0;
        }
        return result;
    }

    /**
     * Returns optimal solution for minimal number of coins to give change. Internal cameFrom path could be re-used and
     * solution could be returned in O(1) from cache.
     *
     * @param sum Sum.
     * @param denominations Coin denominations.
     * @return Set of coins that builds the sum. Or empty list when there is no solution.
     */
    public static List<Integer> getChange(int sum, Set<Integer> denominations) {
        if (denominations == null || denominations.isEmpty() || sum <= 0) {
            throw new IllegalArgumentException("Expected non empty set of integers and positive sum.");
        }
        Map<Integer, Integer> cameFrom = new HashMap<>(); // Path.
        Map<Integer, Integer> solutions = new HashMap<>();
        solutions.put(0, 0);
        solutions.put(sum, Integer.MAX_VALUE);

        for (int x = 1; x <= sum; x++) {
            solutions.put(x, Integer.MAX_VALUE);
            for (Integer denomination : denominations) {
                if (x >= denomination) {
                    // Look for previous solution.
                    Integer previous = solutions.get(x - denomination);
                    if (previous != null && previous != Integer.MAX_VALUE && previous + 1 < solutions.get(x)) {
                        solutions.put(x, previous + 1);
                        cameFrom.put(x, x - denomination);
                    }
                }
            }
        }
        // Came from contains all the paths for all the sums: 1..sum
        Integer result = solutions.get(sum);
        if (result == null || result == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }
        List<Integer> change = new ArrayList<>();
        int remaining = sum;
        while (cameFrom.containsKey(remaining)) {
            int diff = remaining - cameFrom.get(remaining);
            change.add(diff);
            remaining = cameFrom.get(remaining);
        }
        Collections.sort(change);
        return change;
    }

    private CoinChange() {
    }
}
