package com.nobullet.algo;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Item in the Knapsack: value and weight.
 */
public class KnapsackItem implements Comparable<KnapsackItem> {

  final int value;
  final int weight;
  final String description;

  /**
   * Constructs item from given arguments.
   *
   * @param description Description.
   * @param value       Value.
   * @param weight      Weight.
   */
  public KnapsackItem(String description, int value, int weight) {
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
   * @param value  Value.
   * @param weight Weight.
   */
  public KnapsackItem(int value, int weight) {
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
  public final int compareTo(KnapsackItem o) {
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
  final int compareByValueAndWeight(KnapsackItem o) {
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

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof KnapsackItem)) {
      return false;
    }
    final KnapsackItem other = (KnapsackItem) obj;
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
   * Repeatable item. Contains Knapsack {@link KnapsackItem} and quantity.
   */
  public static class RepeatableItem implements Comparable<RepeatableItem> {

    final KnapsackItem item;
    final int quantity;

    /**
     * Constructs repeatable item from given arguments.
     *
     * @param description Description.
     * @param value       Value.
     * @param weight      Weight.
     * @param quantity    Quantity.
     */
    public RepeatableItem(String description, int value, int weight, int quantity) {
      this(new KnapsackItem(description, value, weight), quantity);
    }

    /**
     * Constructs repeatable item from given arguments.
     *
     * @param value    Value.
     * @param weight   Weight.
     * @param quantity Quantity.
     */
    public RepeatableItem(int value, int weight, int quantity) {
      this(new KnapsackItem(value, weight), quantity);
    }

    /**
     * Constructs repeatable item from given arguments.
     *
     * @param item     Item.
     * @param quantity Quantity.
     */
    public RepeatableItem(KnapsackItem item, int quantity) {
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
    public final KnapsackItem getItem() {
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
  }

  /**
   * Ratio for greedy solution.
   */
  static class Ratio implements Comparable<Ratio> {

    final KnapsackItem item;
    final Double ratio;

    /**
     * Constructs ratio for greedy unbounded Knapsack solution.
     *
     * @param value  Value.
     * @param weight Weight.
     */
    public Ratio(int value, int weight) {
      this(new KnapsackItem(value, weight));
    }

    /**
     * Constructs ratio for greedy unbounded Knapsack solution.
     *
     * @param item Item.
     */
    public Ratio(KnapsackItem item) {
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
   * {@link Iterator} that emits plain {@link KnapsackItem} object from given set of {@link RepeatableItem}. Each {@link KnapsackItem}
   * is emitted 'quantity' times.
   */
  static final class RepeatableItemsIterable implements Iterable<KnapsackItem> {

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
    public Iterator<KnapsackItem> iterator() {
      return new RepeatableItemsToItemsIterator(repeatableItems);
    }
  }

  /**
   * {@link Iterator} that emits plain {@link KnapsackItem} object from given set of {@link RepeatableItem}. Each {@link KnapsackItem}
   * is emitted 'quantity' times. Used for solving bounded Knapsack problem.
   */
  static final class RepeatableItemsToItemsIterator implements Iterator<KnapsackItem> {

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
    public KnapsackItem next() {
      if (currentCount > 0) {
        KnapsackItem toReturn = currentRepeatableItem.item;
        currentCount--;
        if (currentCount == 0) {
          fetchNextRepeatable();
        }
        return toReturn;
      }
      throw new IllegalStateException("No more elements");
    }
  }
}
