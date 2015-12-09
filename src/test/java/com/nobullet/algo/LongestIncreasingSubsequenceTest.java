package com.nobullet.algo;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;

/**
 * Tests for longest increasing subsequence (LIS).
 */
public class LongestIncreasingSubsequenceTest {

    @Test
    public void testLIS_oN2() {
        List<Integer> expected = Lists.newArrayList(1, 2, 3, 3, 4, 5);
        List<Integer> source = Lists.newArrayList(9, 10, 11, 1, 3, 2, 3, 3, 4, 6, -1, 5);
        
        assertThat(expected, contains(LongestIncreasingSubsequence.fromListInNSquare(source).toArray()));
    }
    
    @Test
    public void testLIS_oNLogN() {
        List<Integer> expected = Lists.newArrayList(1, 2, 3, 4, 5);
        List<Integer> source = Lists.newArrayList(9, 10, 11, 1, 3, 2, 3, 3, 3, 3, 3, 3, 4, 6, -1, 5);
        
        assertThat(expected, contains(LongestIncreasingSubsequence.fromList(source).toArray()));
    }
    
        @Test
    public void testLIS_oNLogNBig() {
        List<Integer> expected = Lists.newArrayList(-2, 0, 2, 6, 9, 11, 15, 16, 17, 81, 84, 99, 100);
        List<Integer> source = Lists.newArrayList(
                -2, 10, 0,  7, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15, 98, 97, 96, 16, 
                90, 91, 92, 17, 82, 81, 84, 18, 1, 2, 99, 3, -5, -6, 100, 95);
        
        assertThat(expected, contains(LongestIncreasingSubsequence.fromList(source).toArray()));
    }
}
