package com.nobullet.algo;

import com.nobullet.list.Indexable;
import com.nobullet.list.Indexables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Given a sequence, find Longest Repeated Subsequence. If there are more than one Longest Repeated
 * Subsequence, get any one of them.
 */
public final class LongestRepeatedSubsequence {

    /**
     * Finds longest repeated subsequence of characters for given string.
     *
     * @param source String.
     * @return String, containing one of the longest repeated subsequence.
     */
    public static String fromString(String source) {
        StringBuilder sb = new StringBuilder();
        fromIndexable(Indexables.fromChars(source)).stream().forEach(sb::append);
        return sb.toString();
    }

    /**
     * Finds longest repeated subsequence of elements for given list.
     *
     * @param <T>    Type.
     * @param source List.
     * @return List, containing one of the longest repeated subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromList(List<T> source) {
        return fromIndexable(Indexables.fromList(source));
    }

    /**
     * Finds longest repeated subsequence of elements for given array.
     *
     * @param <T>    Type.
     * @param source Array.
     * @return List, containing one of the longest repeated subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromArray(T[] source) {
        return fromIndexable(Indexables.fromArray(source));
    }

    /**
     * Finds longest repeated subsequence of elements for given indexables. Complexity is O(N^2*log(N)).
     *
     * @param <T>    Type.
     * @param source Indexables.
     * @return List, containing one of the longest repeated subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromIndexable(Indexable<T> source) {
        if (source.size() <= 1) {
            return Collections.emptyList();
        }
        Integer[] suffixes = new Integer[source.size()];
        for (int i = 0; i < source.size(); i++) {
            suffixes[i] = i;
        }
        // Sort part, O(N^2 * log(N) ).
        Comparator<Integer> comparator = (Integer index1, Integer index2) -> compare(source, index1, index2);
        Arrays.parallelSort(suffixes, comparator);
        // Scan part, O(N).
        int currentSuffixIndex;
        int previousSuffixIndex = suffixes[0];
        int length = 0;
        int bestIndex = 0;
        int bestLength = 0;
        for (int i = 1; i < source.size(); i++) {
            currentSuffixIndex = suffixes[i];
            length = commonLength(source, currentSuffixIndex, previousSuffixIndex);
            if (length > bestLength) {
                bestIndex = currentSuffixIndex;
                bestLength = length;
            }
            previousSuffixIndex = currentSuffixIndex;
        }
        if (bestLength == 0) {
            return Collections.emptyList();
        }
        List<T> results = new ArrayList<>(bestLength);
        for (int i = bestIndex; bestLength > 0; bestLength--, i++) {
            results.add(source.get(i));
        }
        return Collections.unmodifiableList(results);
    }

    private static <T extends Comparable<? super T>>
    int compare(Indexable<T> source, final int suffix1Index, final int suffix2Index) {
        int suffix1IndexCopy = suffix1Index;
        int suffix2IndexCopy = suffix2Index;
        while (source.get(suffix1IndexCopy).equals(source.get(suffix2IndexCopy))) {
            suffix1IndexCopy++;
            suffix2IndexCopy++;
            if (suffix1IndexCopy == source.size() || suffix2IndexCopy == source.size()) {
                suffix1IndexCopy--;
                suffix2IndexCopy--;
                break;
            }
        }
        return source.get(suffix1IndexCopy).compareTo(source.get(suffix2IndexCopy));
    }

    private static <T extends Comparable<? super T>>
    int commonLength(Indexable<T> source, final int suffix1Index, final int suffix2Index) {
        if (suffix1Index == suffix2Index) {
            return 0;
        }
        int length = 0;
        int suffix1IndexCopy = suffix1Index;
        int suffix2IndexCopy = suffix2Index;
        while (source.get(suffix1IndexCopy).equals(source.get(suffix2IndexCopy))) {
            suffix1IndexCopy++;
            suffix2IndexCopy++;
            length++;
            if (suffix1IndexCopy == source.size() || suffix2IndexCopy == source.size()) {
                break;
            }
        }
        return length;
    }

    private LongestRepeatedSubsequence() {
    }
}
