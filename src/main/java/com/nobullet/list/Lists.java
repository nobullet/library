package com.nobullet.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Utility class for lists.
 */
public final class Lists {

    /**
     * Groups duplicate elements in the given list, converting list element to something that accepts list element and
     * quantity of this list element in group. Acts like simple RLE compression.
     *
     * @param <R> Return type: type that has information about quantity of original item.
     * @param <P> Item type.
     * @param list List of items.
     * @param converter Converter to convert item and quantity to an entity that have both.
     * @return Compacted list.
     */
    public static <R, P> List<R> compact(List<P> list, BiFunction<P, Integer, R> converter) {
        int size = list.size();
        List<R> repeatableResult = new ArrayList<>(size);
        int quantity = 0;
        P prev = null;
        for (int i = 0; i < size; i++) {
            quantity++;
            P current = list.get(i);
            if (!current.equals(prev)) {
                if (prev != null) {
                    repeatableResult.add(converter.apply(prev, quantity));
                }
                quantity = 0;
                if (i + 1 == size) {
                    repeatableResult.add(converter.apply(current, 1));
                }
            } else {
                if (i + 1 == size) {
                    repeatableResult.add(converter.apply(current, quantity + 1));
                }
            }
            prev = current;
        }
        return repeatableResult;
    }

    private Lists() {
    }
}
