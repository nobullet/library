package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * Tests for {@link Sorts}.
 */
public class SortsTest {

    static final List<Integer> SOURCE = Lists.newArrayList(1, 34, 63, 3, 4, 7, 89, 5, 32, 2, 5, 789, 4, 2, 23, 45);

    @Test
    public void testMergeSort() {
        List<Integer> copy = Lists.newArrayList(SOURCE);
        List<Integer> custom = Lists.newArrayList(copy);
        Sorts.mergeSort(custom);
        Collections.sort(copy);
        assertListsEqual(copy, custom);

        copy = Lists.newArrayList(SOURCE);
        copy.add(-10);
        custom = Lists.newArrayList(copy);
        Sorts.mergeSort(custom);
        Collections.sort(copy);
        assertListsEqual(copy, custom);
    }
}
