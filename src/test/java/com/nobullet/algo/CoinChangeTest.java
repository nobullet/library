package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static com.nobullet.algo.CoinChange.getChange;
import static org.junit.Assert.assertEquals;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for coin change problem.
 */
public class CoinChangeTest {

    private static final Set<Integer> US_COINS = Sets.newHashSet(1, 5, 10, 25);
    private static final Set<Integer> US_COINS_WO_PENNY = Sets.newHashSet(5, 10, 25);

    @Test
    public void testRecursive() {
        // Too slow!.
        assertEquals("1, 5, 10, 25 -> 100", 4, CoinChange.getMinimumNumberOfCoinsRecursive(4, US_COINS));
        //assertEquals("1, 5, 10, 25 -> 41", 4, CoinChange.getMinimumNumberOfCoinsRecursive(41, US_COINS));        
        assertEquals("5, 10, 25 -> 5", 1, CoinChange.getMinimumNumberOfCoinsRecursive(5, US_COINS_WO_PENNY));
    }

    @Test
    public void test() {
        assertListsEqual(Lists.newArrayList(25, 25, 25, 25, 25, 25, 25, 25, 25, 25), getChange(250, US_COINS));
        assertListsEqual(Lists.newArrayList(25, 25, 25, 25), getChange(100, US_COINS));
        assertListsEqual(Lists.newArrayList(1, 1, 1, 1, 10, 10, 25, 25, 25), getChange(99, US_COINS));
        assertListsEqual(Lists.newArrayList(1, 1, 1, 10, 10, 25, 25, 25), getChange(98, US_COINS));
        assertListsEqual(Lists.newArrayList(1, 1, 10, 10, 25, 25, 25), getChange(97, US_COINS));
        assertListsEqual(Lists.newArrayList(1, 10, 10, 25, 25, 25), getChange(96, US_COINS));
        assertListsEqual(Lists.newArrayList(10, 10, 25, 25, 25), getChange(95, US_COINS));
        assertListsEqual(Lists.newArrayList(1, 1, 1, 1, 5, 10, 25, 25, 25), getChange(94, US_COINS));

        assertEquals("5, 10, 25 !-> 101", 0, getChange(101, US_COINS_WO_PENNY).size());

        assertListsEqual(Lists.newArrayList(3, 3), getChange(6, Sets.newHashSet(1, 2, 3)));
    }
}
