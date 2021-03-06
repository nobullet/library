package com.nobullet.interview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Longest palindrome test.
 */
public class LongestPalindromeTest {

    @Test
    public void test() {
        assertEquals("123321",
                LongestPalindrome.getLongestPalindromeON3("0912332189"));
        assertEquals("91233219",
                LongestPalindrome.getLongestPalindromeON3("0912332199"));
        assertEquals("",
                LongestPalindrome.getLongestPalindromeON3(""));
        assertEquals("1",
                LongestPalindrome.getLongestPalindromeON3("12"));
        assertEquals("12345678900987654321",
                LongestPalindrome.getLongestPalindromeON3("1 5555555555 00~~12345678900987654321  abbaabbaabbaabbba"));
    }
}
