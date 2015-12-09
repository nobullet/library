package com.nobullet.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds longest increasing subsequence (LIS).<br>
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Longest_increasing_subsequence">LIS, wiki</a>. Good explanation:
 * <a href="http://stackoverflow.com/questions/2631726/how-to-determine-the-longest-increasing-subsequence-using
 * -dynamic-programming">here</a>.
 */
public final class LongestIncreasingSubsequence {

    /**
     * Finds longest non-decreasing subsequence for given list. Solves the problem in O(N^2).
     *
     * @param <T>    Type.
     * @param source Source.
     * @return Longest non-decreasing subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromListInNSquare(List<T> source) {
        return LongestCommonSubsequence.fromLists(source.stream().sorted().collect(Collectors.toList()), source);
    }

    /**
     * Finds longest increasing subsequence for given list. Solves the problem in O(N log N).
     *
     * @param <T>    Type.
     * @param source Source.
     * @return Longest increasing subsequence.
     */
    public static <T extends Comparable<? super T>> List<T> fromList(List<T> source) {
        // Stores the index k of the smallest value source[k] such that there is an increasing subsequence of length j
        // ending at source[k] on the range k ≤ i. Note that j ≤ k ≤ i, because j represents the length of the
        // increasing
        // subsequence, and k represents the index of its termination.
        int[] listSoFar = new int[source.size() + 1];
        // Stores the index of the predecessor of X[k] in the longest increasing subsequence ending at source[k].
        int[] cameFromIndices = new int[source.size()];
        int lengthOfLisSoFar = 0, newLengthSoFar = 0, lo, hi, mid;
        for (int i = 0; i < source.size(); i++) {
            lo = 1;
            hi = lengthOfLisSoFar;
            // Binary search for the largest positive j ≤ L
            // such that source[lisOfLength[j]] < source[i].
            while (lo <= hi) {
                mid = lo + (hi - lo) / 2;
                // Replace with <= to get non-descreasing.
                if (source.get(listSoFar[mid]).compareTo(source.get(i)) < 0) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            // After searching, lo is 1 greater than the
            // length of the longest prefix of source[i].
            newLengthSoFar = lo;

            // The predecessor of source[i] is the last index of
            // the subsequence of length newL-1.
            cameFromIndices[i] = listSoFar[newLengthSoFar - 1];
            listSoFar[newLengthSoFar] = i;

            if (newLengthSoFar > lengthOfLisSoFar) {
                // If we found a subsequence longer than any we've
                // found yet, update length so far.
                lengthOfLisSoFar = newLengthSoFar;
            }
        }
        // Reconstruct the longest increasing subsequence.
        List<T> result = new ArrayList<>(lengthOfLisSoFar);
        int k = listSoFar[lengthOfLisSoFar];
        for (int i = lengthOfLisSoFar; i > 0; i--) {
            result.add(source.get(k));
            k = cameFromIndices[k];
        }
        Collections.reverse(result);
        return result;
    }

    private LongestIncreasingSubsequence() {
    }
}
