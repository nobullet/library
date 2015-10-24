package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds longest increasing subsequence (LIS).<br>
 *
 * See <a href="https://en.wikipedia.org/wiki/Longest_increasing_subsequence">LIS, wiki</a>. Good explanation:
 * <a href="http://stackoverflow.com/questions/2631726/how-to-determine-the-longest-increasing-subsequence-using-dynamic-programming">here</a>.
 */
public final class LongestIncreasingSubsequence {

    /**
     * Finds longest increasing subsequence for given list. Solves the problem in O(N^2).
     *
     * @param <T> Type.
     * @param source Source.
     * @return Longest increasing subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromListInNSquare(List<T> source) {
        return LongestCommonSubsequence.fromLists(source.stream().sorted().collect(Collectors.toList()), source);
    }

    /**
     * Finds longest increasing subsequence for given list. Solves the problem in O(N log N).
     *
     * @param <T> Type.
     * @param source Source.
     * @return Longest increasing subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromList(List<T> source) {
        int[] p = new int[source.size()];
        int[] m = new int[source.size() + 1];
        int l = 0, newL = 0, lo, hi, mid;
        for (int i = 0; i < source.size(); i++) {
            lo = 1;
            hi = l;
            // Binary search for the largest positive j ≤ L
            // such that source[M[j]] < source[i].
            while (lo <= hi) {
                mid = lo + (hi - lo) / 2;
                if (source.get(m[mid]).compareTo(source.get(i)) < 0) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            // After searching, lo is 1 greater than the
            // length of the longest prefix of X[i].
            newL = lo;

            // The predecessor of X[i] is the last index of 
            // the subsequence of length newL-1.
            p[i] = m[newL - 1];
            m[newL] = i;

            if (newL > l) {
                // If we found a subsequence longer than any we've
                // found yet, update L.
                l = newL;
            }
        }
        // Reconstruct the longest increasing subsequence.
        List<T> result = new ArrayList<>(l);
        int k = m[l];
        for (int i = l; i > 0; i--) {
            result.add(source.get(k));
            k = p[k];
        }
        Collections.reverse(result);
        return result;
    }

    private LongestIncreasingSubsequence() {
    }
}
