package com.nobullet.algo;

import com.nobullet.list.Indexable;
import com.nobullet.list.Indexables;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Given two sequences str1 and str2, find out which modifications must be performed (insert, remove, replace) in order
 * to get str2 from str1.
 */
public final class MinimalEditOperations {

    /**
     * Finds minimum set of edit operations in order to get one string from another.
     *
     * @param source1 String 1.
     * @param source2 String 2.
     * @return List, containing minimum set of edit operations.
     */
    public static List<Operation> fromStrings(String source1, String source2) {
        return fromIndexables(Indexables.fromChars(source1), Indexables.fromChars(source2));
    }

    /**
     * Finds minimum set of edit operations in order to get one list from another.
     *
     * @param <T>     Type.
     * @param source1 List 1.
     * @param source2 List 2.
     * @return List, containing minimum set of edit operations.
     */
    public static <T> List<Operation> fromLists(List<T> source1, List<T> source2) {
        return fromIndexables(Indexables.fromList(source1), Indexables.fromList(source2));
    }

    /**
     * Finds minimum set of edit operations in order to get one array from another.
     *
     * @param <T>     Type.
     * @param source1 Array 1.
     * @param source2 Array 2.
     * @return List, containing minimum set of edit operations.
     */
    public static <T> List<Operation> fromArrays(T[] source1, T[] source2) {
        return fromIndexables(Indexables.fromArray(source1), Indexables.fromArray(source2));
    }

    /**
     * Finds minimum set of edit operations in order to get one array from another.
     *
     * @param <T>     Type.
     * @param source1 Indexable 1.
     * @param source2 Indexable 2.
     * @return List, containing minimum set of edit operations.
     */
    public static <T> List<Operation> fromIndexables(Indexable<T> source1, Indexable<T> source2) {
        int i = 0, j = 0;
        int[][] distances = new int[source1.size() + 1][source2.size() + 1];
        for (i = 0; i <= source1.size(); i++) {
            for (j = 0; j <= source2.size(); j++) {
                if (i == 0) {
                    distances[i][j] = j;
                } else if (j == 0) {
                    distances[i][j] = i;
                } else if (source1.get(i - 1).equals(source2.get(j - 1))) {
                    distances[i][j] = distances[i - 1][j - 1];
                } else {
                    distances[i][j] = 1 + Math.min(distances[i - 1][j - 1],
                            Math.min(distances[i][j - 1], distances[i - 1][j]));
                }
            }
        }
        if (false) { // For debug.
            StringBuilder sb = new StringBuilder("\n");
            for (i = 0; i < distances.length; i++) {
                if (sb.length() == 0) {
                    sb.append("    ");
                    for (j = 0; j < source2.size(); j++) {
                        sb.append(source2.get(j)).append(' ');
                    }
                    sb.append("\n");
                }
                if (i > 0) {
                    sb.append(source1.get(i - 1)).append(' ');
                } else {
                    sb.append("  ");
                }
                for (j = 0; j < distances[0].length; j++) {
                    sb.append(distances[i][j]).append(' ');
                }
                sb.append("\n");
            }
            Logger.getGlobal().info(sb.toString());
        }

        int minDist = distances[source1.size()][source2.size()];
        List<Operation> result = new ArrayList<>(minDist);
        for (i = 0; i < minDist; i++) {
            result.add(null);
        }
        i = source1.size();
        j = source2.size();
        int operationIndex = minDist - 1;
        while (i > 0 && j > 0) {
            int value = distances[i][j];
            if (value == distances[i][j - 1] + 1) { // INSERTION.
                result.set(operationIndex, new Operation(Type.INSERT, i, j - 1));
                operationIndex--;
                j--;
            } else if (value == distances[i - 1][j] + 1)  { // DELETION.
                result.set(operationIndex, new Operation(Type.DELETE, i - 1, j));
                operationIndex--;
                i--;
            } else {
                if (!source1.get(i - 1).equals(source2.get(j - 1))) { // REPLACE.
                    result.set(operationIndex, new Operation(Type.REPLACE, i - 1, j - 1));
                    operationIndex--;
                }
                i--;
                j--;
            }
        }
        return result;
    }

    public static class Operation {

        final Type type;
        final int source1Index;
        final int source2Index;

        Operation(Type type, int source1Index, int source2Index) {
            this.type = type;
            this.source1Index = source1Index;
            this.source2Index = source2Index;
        }

        public Type getType() {
            return type;
        }

        public int getSource1Index() {
            return source1Index;
        }

        public int getSource2Index() {
            return source2Index;
        }

        @Override
        public String toString() {
            return String.format("{%s, (%d, %d)}", type, source1Index, source2Index);
        }
    }

    public enum Type {
        INSERT, REPLACE, DELETE;
    }

    private MinimalEditOperations() {
    }
}
