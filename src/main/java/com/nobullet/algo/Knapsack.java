package com.nobullet.algo;

import com.nobullet.list.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Knapsack problem and solutions.
 */
public final class Knapsack {

    /**
     * Solves the unbounded Knapsack problem in greedy manner (by George Dantzig). Optimal result is not guaranteed.
     *
     * @param weight Size of knapsack.
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
        Ratio[] ratios = new Ratio[valueWeightMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> valueWeight : valueWeightMap.entrySet()) {
            ratios[i++] = new Ratio(valueWeight.getKey(), valueWeight.getValue());
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
     * @param weight Knapsack weight (size).
     * @param weights Weights of items.
     * @param values Values of items.
     * @return Optimal solution of 0/1 Knapsack problem.
     */
    public static List<Item> zeroOne(int weight, int[] weights, int[] values) {
        if (values == null || values.length < 1 || weights == null || weights.length != values.length) {
            throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
        }
        Set<Item> items = new HashSet<>();
        for (int i = 0; i < weights.length; i++) {
            items.add(new Item(values[i], weights[i]));
        }
        return generic(weight, items);
    }

    /**
     * Solves 0/1 Knapsack problem for given arguments.
     *
     * @param weight Knapsack weight (size).
     * @param items Set of items.
     * @return Optimal solution of 0/1 Knapsack problem.
     */
    public static List<Item> zeroOne(int weight, Set<Item> items) {
        return generic(weight, items);
    }

    /**
     * Solves bounded Knapsack problem for given arguments.
     *
     * @param weight Knapsack weight (size).
     * @param weights Weights of items.
     * @param values Values of items.
     * @param quantities Quantities of items.
     * @return Optimal solution of bounded Knapsack problem.
     */
    public static List<RepeatableItem> bounded(int weight, int[] weights, int[] values, int[] quantities) {
        if (values == null || values.length < 1 || weights == null || weights.length != values.length
                || quantities.length != values.length) {
            throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
        }
        Set<RepeatableItem> items = new HashSet<>();
        for (int i = 0; i < weights.length; i++) {
            items.add(new RepeatableItem(values[i], weights[i], quantities[i]));
        }
        return bounded(weight, items);
    }

    /**
     * Solves bounded Knapsack problem for given arguments.
     *
     * @param weight Knapsack weight (size).
     * @param items Repeatable items.
     * @return Optimal solution of bounded Knapsack problem in the for of repeatable items.
     */
    public static List<RepeatableItem> bounded(int weight, Set<? extends RepeatableItem> items) {
        return Lists.compact(genericWithIterable(weight, new RepeatableItemsIterable(items)), RepeatableItem::new);
    }

    /**
     * Solves generic Knapsack problem for given arguments.
     *
     * @param weight Knapsack weight (size).
     * @param weights Weights of items.
     * @param values Values of items.
     * @param quantities Quantities of items.
     * @return Optimal solution of bounded Knapsack problem.
     */
    public static List<Item> generic(int weight, int[] weights, int[] values, int[] quantities) {
        if (values == null || values.length < 1 || weights == null || weights.length != values.length
                || quantities.length != values.length) {
            throw new IllegalArgumentException("Weights and values arrays must be of same length > 0.");
        }
        Set<RepeatableItem> items = new HashSet<>();
        for (int i = 0; i < weights.length; i++) {
            items.add(new RepeatableItem(values[i], weights[i], quantities[i]));
        }
        return genericWithIterable(weight, new RepeatableItemsIterable(items));
    }

    /**
     * Generic solution for Knapsack problem.
     *
     * @param weight Maximum weight.
     * @param items Items to place into the Knapsack. May have duplicates.
     * @return Optimal solution.
     */
    public static List<Item> generic(int weight, Collection<? extends Item> items) {
        return genericWithIterable(weight, items);
    }

    /**
     * Generic solution for Knapsack problem.
     *
     * @param weight Maximum weight.
     * @param items Items to place into the Knapsack. May have duplicates.
     * @return Optimal solution.
     */
    public static List<Item> genericWithIterable(int weight, Iterable<? extends Item> items) {
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
        Map<Item, Item> cameFromItems = new HashMap<>(); // Edges for the path.
        for (Item item : items) {
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
                            cameFromItems.put(new Item(currentValue, w), item);
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
        List<Item> solution = new ArrayList<>();
        Item remaining = new Item(optimalSolution, vi);
        while (cameFromItems.containsKey(remaining)) {
            Item solutionItem = cameFromItems.get(remaining);
            solution.add(solutionItem);
            remaining = new Item(remaining.value - solutionItem.value, remaining.weight - solutionItem.weight);
        }
        // Sort in ascending order.
        Collections.sort(solution);
        return solution;
    }

    /**
     * Item in the Knapsack: value and weight.
     */
    public static class Item implements Comparable<Item> {

        final int value;
        final int weight;
        final String description;

        /**
         * Constructs item from given arguments.
         *
         * @param description Description.
         * @param value Value.
         * @param weight Weight.
         */
        public Item(String description, int value, int weight) {
            if (value < 0 || weight < 0) {
                throw new IllegalArgumentException("Value and weight must be >= 0.");
            }
            this.value = value;
            this.weight = weight;
            this.description = description;
        }

        /**
         * Constructs item from given arguments.
         *
         * @param value Value.
         * @param weight Weight.
         */
        public Item(int value, int weight) {
            this(null, value, weight);
        }

        /**
         * Returns weight of the item.
         *
         * @return Item weight.
         */
        public final int getWeight() {
            return weight;
        }

        /**
         * Item value.
         *
         * @return Item value.
         */
        public final int getValue() {
            return value;
        }

        /**
         * Item description.
         *
         * @return Item description.
         */
        public String getDescription() {
            return description;
        }

        @Override
        public final int compareTo(Item o) {
            if (description != null && o.description == null) {
                return 1;
            } else if (description == null && o.description != null) {
                return -1;
            } else if (description == null && o.description == null) {
                return compareByValueAndWeight(o);
            } else if (description.equals(o.description)) {
                return compareByValueAndWeight(o);
            }
            return description.compareTo(o.description);
        }

        /**
         * Compares item to other item by value and weight only.
         *
         * @param o Other item
         * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y}; and a value
         * greater than {@code 0} if {@code x > y}
         */
        final int compareByValueAndWeight(Item o) {
            // By value then by weight.
            if (value == o.value) {
                return Integer.compare(weight, o.weight);
            }
            return Integer.compare(value, o.value);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + this.value;
            hash = 53 * hash + this.weight;
            hash = 53 * hash + Objects.hashCode(this.description);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Item)) {
                return false;
            }
            final Item other = (Item) obj;
            if (this.value != other.value) {
                return false;
            }
            if (this.weight != other.weight) {
                return false;
            }
            return Objects.equals(this.description, other.description);
        }

        @Override
        public final String toString() {
            return toStringBuilder().toString();
        }

        /**
         * {@link StringBuilder} with string representation of the object.
         *
         * @return String representation of the object.
         */
        StringBuilder toStringBuilder() {
            StringBuilder result = new StringBuilder("{value:")
                    .append(value)
                    .append(", weight:")
                    .append(weight);
            if (description != null) {
                result.append(", description:\"");
                result.append(description.replaceAll("[^a-zA-Z0-9\\s:\\-_]+", ""));
                result.append("\"");
            }
            result.append("}");
            return result;
        }
    }

    /**
     * Repeatable item. Contains Knapsack {@link Item} and quantity.
     */
    public static class RepeatableItem implements Comparable<RepeatableItem> {

        final Item item;
        final int quantity;

        /**
         * Constructs repeatable item from given arguments.
         *
         * @param description Description.
         * @param value Value.
         * @param weight Weight.
         * @param quantity Quantity.
         */
        public RepeatableItem(String description, int value, int weight, int quantity) {
            this(new Item(description, value, weight), quantity);
        }

        /**
         * Constructs repeatable item from given arguments.
         *
         * @param value Value.
         * @param weight Weight.
         * @param quantity Quantity.
         */
        public RepeatableItem(int value, int weight, int quantity) {
            this(new Item(value, weight), quantity);
        }

        /**
         * Constructs repeatable item from given arguments.
         *
         * @param item Item.
         * @param quantity Quantity.
         */
        public RepeatableItem(Item item, int quantity) {
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity must be > 0");
            }
            this.item = item;
            this.quantity = quantity;
        }

        /**
         * Returns the item.
         *
         * @return Item.
         */
        public final Item getItem() {
            return item;
        }

        /**
         * Quantity of the item.
         *
         * @return Quantity of the item.
         */
        public final int getQuantity() {
            return quantity;
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj == null || !(obj instanceof RepeatableItem)) {
                return false;
            }
            final RepeatableItem other = (RepeatableItem) obj;
            return Objects.equals(this.item, other.item);
        }

        @Override
        public final int hashCode() {
            return 41 * 5 + Objects.hashCode(this.item);
        }

        @Override
        public int compareTo(RepeatableItem o) {
            int itemResult = item.compareTo(o.item);
            if (itemResult == 0) {
                return Integer.compare(quantity, o.quantity);
            }
            return itemResult;
        }

        @Override
        public final String toString() {
            return toStringBuilder().toString();
        }

        /**
         * {@link StringBuilder} with string representation of the object.
         *
         * @return String representation of the object.
         */
        StringBuilder toStringBuilder() {
            StringBuilder sb = item.toStringBuilder();
            sb.setCharAt(sb.length() - 1, ',');
            return sb.append(" quantity: ").append(quantity).append('}');
        }
    }

    /**
     * Ratio for greedy solution.
     */
    private static class Ratio implements Comparable<Ratio> {

        final Item item;
        final Double ratio;

        /**
         * Constructs ratio for greedy unbounded Knapsack solution.
         *
         * @param value Value.
         * @param weight Weight.
         */
        public Ratio(int value, int weight) {
            this(new Item(value, weight));
        }

        /**
         * Constructs ratio for greedy unbounded Knapsack solution.
         *
         * @param item Item.
         */
        public Ratio(Item item) {
            this.item = item;
            this.ratio = (double) this.item.value / (double) this.item.weight;
        }

        @Override
        public int compareTo(Ratio o) {
            return ratio.compareTo(o.ratio);
        }
    }

    /**
     * {@link Iterable} that returns {@link RepeatableItemsToItemsIterator}. {@link RepeatableItemsToItemsIterator} is
     * {@link Iterator} that emits plain {@link Item} object from given set of {@link RepeatableItem}. Each {@link Item}
     * is emitted 'quantity' times.
     */
    private static final class RepeatableItemsIterable implements Iterable<Item> {

        final Set<? extends RepeatableItem> repeatableItems;

        /**
         * Constructs iterable.
         *
         * @param repeatableItems
         */
        public RepeatableItemsIterable(Set<? extends RepeatableItem> repeatableItems) {
            this.repeatableItems = repeatableItems;
        }

        @Override
        public Iterator<Item> iterator() {
            return new RepeatableItemsToItemsIterator(repeatableItems);
        }
    }

    /**
     * {@link Iterator} that emits plain {@link Item} object from given set of {@link RepeatableItem}. Each {@link Item}
     * is emitted 'quantity' times. Used for solving bounded Knapsack problem.
     */
    private static final class RepeatableItemsToItemsIterator implements Iterator<Item> {

        final Iterator<? extends RepeatableItem> repeatableItems;
        RepeatableItem currentRepeatableItem;
        int currentCount;

        /**
         * Constructs repeatable iterator.
         *
         * @param repeatableItems Repeatable items to construct from.
         */
        public RepeatableItemsToItemsIterator(Set<? extends RepeatableItem> repeatableItems) {
            this.repeatableItems = repeatableItems.iterator();
            fetchNextRepeatable();
        }

        /**
         * Switches to the next {@link RepeatableItem} when iterator has already emitted previous item 'quantity' times.s
         */
        void fetchNextRepeatable() {
            if (repeatableItems.hasNext()) {
                currentRepeatableItem = repeatableItems.next();
                currentCount = currentRepeatableItem.quantity;
            }
        }

        @Override
        public boolean hasNext() {
            return currentRepeatableItem != null && currentCount > 0;
        }

        @Override
        public Item next() {
            if (currentCount > 0) {
                Item toReturn = currentRepeatableItem.item;
                currentCount--;
                if (currentCount == 0) {
                    fetchNextRepeatable();
                }
                return toReturn;
            }
            throw new IllegalStateException("No more elements");
        }
    }

    private Knapsack() {
    }
}
