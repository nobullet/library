package com.nobullet.algo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;

/**
 * Tests for longest common subsequence (LCS).
 */
public class LongestCommonSubsequenceTest {

    @Test
    public void testLCSAsString() {
        assertEquals("", LongestCommonSubsequence.fromStrings("123", "ABC"));
        assertEquals("123", LongestCommonSubsequence.fromStrings("123", "123"));
        assertEquals("AC", LongestCommonSubsequence.fromStrings("AGCAT", "GAC"));
        assertEquals("1234567", LongestCommonSubsequence.fromStrings("1234567890", "AB1E2T3CDX1A2B3C4D5E6F7E234567EE"));
        assertEquals("АМА ЫЛА РАМ", 
                LongestCommonSubsequence.fromStrings("МАМА МЫЛА РАМУ", "АВТОПАНОРАМА БЫЛА ПРОГРАММА"));
    }
    
    @Test
    public void testLCSAsList() {
        List<Integer> expected = Lists.newArrayList(1, 2, 3, 4, 5);
        List<Integer> source1 = Lists.newArrayList(1, 7, 2, 8, 3, 9, 4, 7, 5);
        List<Integer> source2 = Lists.newArrayList(9, 10, 11, 1, 3, 2, 3, 4, 6, 6, 5);
        List<Integer> lcs = LongestCommonSubsequence.fromLists(source1, source2);
        assertThat(expected, contains(lcs.toArray()));
    }
}
