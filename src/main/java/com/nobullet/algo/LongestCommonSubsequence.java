package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Longest subsequence of elements (LCS).<br><br>
 * X[1, M], Y[1, N] (1-based)<br>
 * LCS(Xi, Yj) = <br>
 * a. 0, if i == 0 || j == 0 (one row on top and column on left)<br>
 * b. LCS(Xi-1, Yj-1) + Xi, if Xi == Yj<br>
 * c. max(LCS(Xi-1, Yj), LCS(Xi, Yj-1)), if Xi != Yj<br><br>
 * For backtracking the following non-recursive approach is used:<br>
 * <pre>
 * function backtrack(C[0..m,0..n], X[1..m], Y[1..n], i, j)
 *   if i = 0 or j = 0
 *     return ""
 *   else if  X[i] = Y[j]
 *     return backtrack(C, X, Y, i-1, j-1) + X[i]
 *   else
 *     if C[i,j-1] > C[i-1,j]
 *       return backtrack(C, X, Y, i, j-1)
 *     else
 *       return backtrack(C, X, Y, i-1, j)
 * </pre>
 */
public final class LongestCommonSubsequence {

    /**
     * Finds common subsequence of characters from given string.
     *
     * @param source1 String 1.
     * @param source2 String 2.
     * @return String, containing one of the longest subsequences.
     */
    public static String fromStrings(String source1, String source2) {
        StringBuilder sb = new StringBuilder();
        fromIndexables(Indexables.fromChars(source1), Indexables.fromChars(source2)).stream().forEach(sb::append);
        return sb.toString();
    }

    /**
     * Finds common subsequence of characters from given lists.
     *
     * @param <T> Type
     * @param source1 List 1.
     * @param source2 List 2.
     * @return List, containing one of the longest subsequences.
     */
    public static <T> List<T> fromLists(List<T> source1, List<T> source2) {
        return fromIndexables(Indexables.fromList(source1), Indexables.fromList(source2));
    }

    /**
     * Finds common subsequence of characters from given arrays.
     *
     * @param <T> Type
     * @param source1 Array 1.
     * @param source2 Array 2.
     * @return List, containing one of the longest subsequences.
     */
    public static <T> List<T> fromArrays(T[] source1, T[] source2) {
        return fromIndexables(Indexables.fromArray(source1), Indexables.fromArray(source2));
    }

    /**
     * Finds common subsequence of elements for given indexables.
     *
     * @param <T> Type
     * @param source1 Indexable 1.
     * @param source2 Indexable 2.
     * @return List, containing one of the longest subsequences.
     */
    public static <T> List<T> fromIndexables(Indexable<T> source1, Indexable<T> source2) {
        int[][] results = new int[source1.size() + 1][source2.size() + 1];
        int i = 0, j = 0;
        for (i = 1; i <= source1.size(); i++) {
            for (j = 1; j <= source2.size(); j++) {
                if (source1.get(i - 1).equals(source2.get(j - 1))) {
                    results[i][j] = results[i - 1][j - 1] + 1;
                } else {
                    results[i][j] = Math.max(results[i][j - 1], results[i - 1][j]);
                }
            }
        }
        i = source1.size();
        j = source2.size();
        List<T> lcs = new ArrayList<>(Math.min(source1.size(), source2.size()));
        while (true) {
            if (i == 0 || j == 0) {
                break;
            }
            if (source1.get(i - 1).equals(source2.get(j - 1))) {
                lcs.add(source1.get(i - 1));
                i--;
                j--;
            } else if (results[i][j - 1] > results[i - 1][j]) {
                j--;
            } else {
                i--;
            }
        }
        Collections.reverse(lcs);
        return lcs;
    }

    private LongestCommonSubsequence() {
    }
}
