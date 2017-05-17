package com.nobullet.interview;

/**
 * Longest palindrome.
 */
public final class LongestPalindrome {

    /**
     * Returns the longest palindrome sub-sequence from the given string in O(n^3).
     *
     * @param sequence Character sequence.
     * @return The longest palindrome sub-sequence.
     */
    public static CharSequence getLongestPalindromeON3(CharSequence sequence) {
        int length = sequence.length();
        if (length == 0) {
            return sequence;
        }
        int li = 0;
        int lj = 1;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (isPalindrome(sequence, i, j) && (j - i) > (lj - li)) {
                    li = i;
                    lj = j;
                }
            }
        }
        return sequence.subSequence(li, lj);
    }

    /**
     * Tests if the given sub-sequence of sequences is a palindrome.
     *
     * @param sequence Character sequence.
     * @param begin    Begin index.
     * @param end      End index (excluding).
     * @return True if the given sub-sequence of sequences is a palindrome.
     */
    public static boolean isPalindrome(CharSequence sequence, int begin, int end) {
        end--;
        if (begin >= end) {
            return false;
        }
        while (begin < end) {
            if (sequence.charAt(begin) != sequence.charAt(end)) {
                return false;
            }
            begin++;
            end--;
        }
        return true;
    }

    private LongestPalindrome() {
    }
}
