package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nobullet.algo.Knapsack.Item;
import com.nobullet.algo.Knapsack.RepeatableItem;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for Knapsack problem.
 */
public class KnapsackTest {

    @Test
    public void testUnbounded() {
        List<Integer> result = Knapsack.greedy(26, asMap(25, 4, 10, 3, 5, 2, 1, 1));
        assertListsEqual(Lists.newArrayList(25, 1), result);

        result = Knapsack.greedy(26, asMap(25, 10, 10, 3, 5, 2, 1, 1));
        assertListsEqual(Lists.newArrayList(10, 10, 5, 1), result);

        result = Knapsack.greedy(10, asMap(25, 1, 10, 3, 5, 2, 1, 4));
        assertListsEqual(Lists.newArrayList(10), result);

        result = Knapsack.greedy(99, asMap(25, 1, 10, 3, 5, 2, 1, 4));
        assertListsEqual(Lists.newArrayList(25, 25, 25, 10, 10, 1, 1, 1, 1), result);
    }

    @Test
    public void testZeroOne1() {
        List<Item> result = Knapsack.zeroOne(80, new int[]{20, 30, 50}, new int[]{60, 90, 100});
        assertListsEqual(Lists.newArrayList(new Item(90, 30), new Item(100, 50)), result);
    }

    @Test
    public void testZeroOne_2() {
        List<Item> result = Knapsack.zeroOne(14, new int[]{5, 10, 6, 5}, new int[]{3, 5, 4, 2});
        assertListsEqual(Lists.newArrayList(new Item(3, 5), new Item(4, 6)), result);
    }

    @Test
    public void testZeroOne3() {
        List<Item> result = Knapsack.zeroOne(13, new int[]{3, 4, 5, 9, 9}, new int[]{1, 6, 6, 7, 6});
        assertListsEqual(Lists.newArrayList(new Item(1, 3), new Item(6, 4), new Item(6, 5)), result);
    }

    @Test
    public void testGeneric_1() {
        List<Item> result
                = Knapsack.generic(80, new int[]{20, 30, 50}, new int[]{60, 90, 100}, new int[]{1, 1, 1});
        assertListsEqual(Lists.newArrayList(new Item(90, 30), new Item(100, 50)), result);
    }

    @Test
    public void testGeneric2() {
        List<Item> result
                = Knapsack.generic(14, new int[]{5, 10, 6, 5}, new int[]{3, 5, 4, 2}, new int[]{1, 1, 1, 1});
        assertListsEqual(Lists.newArrayList(new Item(3, 5), new Item(4, 6)), result);
    }

    @Test
    public void testGeneric3() {
        List<Item> result
                = Knapsack.generic(13,
                        new int[]{3, 4, 5, 9, 9, 14}, new int[]{1, 6, 6, 7, 6, 45}, new int[]{1, 1, 1, 1, 1, 2});
        assertListsEqual(Lists.newArrayList(new Item(1, 3), new Item(6, 4), new Item(6, 5)), result);
    }

    @Test
    public void testGeneric3_duplicates() {
        List<Item> result
                = Knapsack.generic(17,
                        new int[]{3, 4, 5, 9, 9, 18}, new int[]{1, 6, 6, 7, 6, 45}, new int[]{1, 2, 1, 1, 1, 10});
        assertListsEqual(Lists.newArrayList(new Item(1, 3), new Item(6, 4), new Item(6, 4), new Item(6, 5)), result);
    }

    @Test
    public void testGeneric_allItems() {
        List<Item> result
                = Knapsack.generic(1000, new int[]{3, 4, 5, 9, 9}, new int[]{1, 6, 6, 7, 6}, new int[]{1, 1, 1, 1, 1});
        assertListsEqual(Lists.newArrayList(new Item(1, 3), new Item(6, 4), new Item(6, 5),
                new Item(6, 9), new Item(7, 9)), result);
    }

    @Test
    public void testBounded() {
        List<RepeatableItem> result
                = Knapsack.bounded(17,
                        new int[]{3, 4, 5, 9, 9}, new int[]{1, 6, 6, 7, 6}, new int[]{1, 2, 1, 1, 1});
        assertListsEqual(
                Lists.newArrayList(
                        new RepeatableItem(1, 3, 1),
                        new RepeatableItem(6, 4, 2),
                        new RepeatableItem(6, 5, 1)),
                result);
    }

    @Test
    public void testBounded_repeatableItems() {
        List<RepeatableItem> result
                = Knapsack.bounded(17,
                        Sets.newHashSet(
                                new RepeatableItem(1, 3, 1),
                                new RepeatableItem(6, 4, 2),
                                new RepeatableItem(6, 5, 1),
                                new RepeatableItem(7, 9, 1),
                                new RepeatableItem(6, 9, 1)
                        ));
        assertListsEqual(
                Lists.newArrayList(
                        new RepeatableItem(1, 3, 1),
                        new RepeatableItem(6, 4, 2),
                        new RepeatableItem(6, 5, 1)),
                result);
    }

    @Test
    public void testBounded_big() {
        // Test is from here: http://www.codeproject.com/Articles/706838/Bounded-Knapsack-Algorithm
        List<RepeatableItem> result
                = Knapsack.bounded(1000,
                        Sets.newHashSet(
                                new RepeatableItem("Apple", 40, 39, 4),
                                new RepeatableItem("Banana", 60, 27, 4),
                                new RepeatableItem("Beer", 10, 52, 12),
                                new RepeatableItem("Book", 10, 30, 2),
                                new RepeatableItem("Camera", 30, 32, 1),
                                new RepeatableItem("Cheese", 30, 23, 4),
                                new RepeatableItem("Chocolate Bar", 60, 15, 10),
                                new RepeatableItem("Compass", 35, 13, 1),
                                new RepeatableItem("Jeans", 10, 48, 1),
                                new RepeatableItem("Map", 150, 9, 1),
                                new RepeatableItem("Notebook", 80, 22, 1),
                                new RepeatableItem("Sandwich", 160, 50, 4),
                                new RepeatableItem("Ski Jacket", 75, 43, 1),
                                new RepeatableItem("Ski Pants", 70, 42, 1),
                                new RepeatableItem("Socks", 50, 4, 2),
                                new RepeatableItem("Sunglasses", 20, 7, 1),
                                new RepeatableItem("Suntan Lotion", 70, 11, 1),
                                new RepeatableItem("T-Shirt", 15, 24, 1),
                                new RepeatableItem("Tin", 45, 68, 1),
                                new RepeatableItem("Towel", 12, 18, 1),
                                new RepeatableItem("Umbrella", 40, 73, 1),
                                new RepeatableItem("Water", 200, 153, 1)
                        ));
        assertListsEqual(
                Lists.newArrayList(
                        new RepeatableItem("Apple", 40, 39, 3),
                        new RepeatableItem("Banana", 60, 27, 4),
                        new RepeatableItem("Cheese", 30, 23, 4),
                        new RepeatableItem("Chocolate Bar", 60, 15, 10),
                        new RepeatableItem("Compass", 35, 13, 1),
                        new RepeatableItem("Map", 150, 9, 1),
                        new RepeatableItem("Notebook", 80, 22, 1),
                        new RepeatableItem("Sandwich", 160, 50, 4),
                        new RepeatableItem("Ski Jacket", 75, 43, 1),
                        new RepeatableItem("Ski Pants", 70, 42, 1),
                        new RepeatableItem("Socks", 50, 4, 2),
                        new RepeatableItem("Sunglasses", 20, 7, 1),
                        new RepeatableItem("Suntan Lotion", 70, 11, 1),
                        new RepeatableItem("T-Shirt", 15, 24, 1),
                        new RepeatableItem("Water", 200, 153, 1)
                ),
                result);
    }

    /**
     * Builds a map from given arguments. Treats arguments as key, value, key, value...
     *
     * @param args Arguments. Even number of integers is expected.
     * @return Map that treats arguments as key, value, key, value...
     */
    private static Map<Integer, Integer> asMap(int... args) {
        if (args.length == 0 || args.length % 2 == 1) {
            throw new IllegalArgumentException("Expected value/weight pairs.");
        }
        Map<Integer, Integer> result = Maps.newHashMapWithExpectedSize(args.length / 2);
        for (int i = 0; i < args.length; i += 2) {
            result.put(args[i], args[i + 1]);
        }
        return result;
    }
}
