package com.nobullet.algo;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link LongestRepeatedSubsequence}.
 */
public class LongestRepeatedSubsequenceTest {

    @Test
    public void testStrings() {
        assertEquals("", LongestRepeatedSubsequence.fromString(""));
        assertEquals("", LongestRepeatedSubsequence.fromString("A"));
        assertEquals("A", LongestRepeatedSubsequence.fromString("AA"));
        assertEquals("AA", LongestRepeatedSubsequence.fromString("AAA"));
        assertEquals("AAA", LongestRepeatedSubsequence.fromString("AAAA"));
        assertEquals("GEEKS", LongestRepeatedSubsequence.fromString("GEEKSFORGEEKS"));
        assertEquals("ana", LongestRepeatedSubsequence.fromString("banana"));
        assertEquals("bananas", LongestRepeatedSubsequence.fromString("ILIKEbananasandbananasLIKEME"));
    }

    @Test
    public void testIntegers() {
        List<Integer> lrs = LongestRepeatedSubsequence.fromArray(new Integer[] {1,2,3,1,2,3});
        assertEquals(Lists.newArrayList(1, 2, 3), lrs);
    }
}
