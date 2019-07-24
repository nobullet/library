package com.nobullet.algo;

import static com.nobullet.algo.Permutations.permutations;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import com.google.common.collect.Collections2;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * Test for permutations generator.
 */
public class PermutationsTest {

    static final Logger logger = Logger.getLogger(PermutationsTest.class.getName());

    @Test
    public void testCopyAndInsertAt() {
        List<String> ins, res;

        ins = Arrays.asList("1", "2", "3");
        res = Permutations.copyAndInsertAt(ins, 0, "0");
        assertThat(Arrays.asList("0", "1", "2", "3"), is(res));

        res = Permutations.copyAndInsertAt(ins, 1, "0");
        assertThat(Arrays.asList("1", "0", "2", "3"), is(res));

        res = Permutations.copyAndInsertAt(ins, 2, "0");
        assertThat(Arrays.asList("1", "2", "0", "3"), is(res));

        res = Permutations.copyAndInsertAt(ins, 3, "0");
        assertThat(Arrays.asList("1", "2", "3", "0"), is(res));

        ins = Collections.emptyList();
        res = Permutations.copyAndInsertAt(ins, 0, "0");
        assertThat(Arrays.asList("0"), is(res));

        ins = Arrays.asList("1");
        res = Permutations.copyAndInsertAt(ins, 0, "0");
        assertThat(Arrays.asList("0", "1"), is(res));

        res = Permutations.copyAndInsertAt(ins, 1, "0");
        assertThat(Arrays.asList("1", "0"), is(res));
    }

    @Test
    public void test0Elements() {
        List<Integer> ints = Collections.emptyList();
        List<List<Integer>> result = Permutations.of(ints);

        assertEquals(0, result.size());
    }

    @Test
    public void test1Element() {
        List<Integer> ints = Arrays.asList(1);
        List<List<Integer>> result = Permutations.of(ints);

        assertEquals(1, result.size());
        assertThat(Arrays.asList(1), is(result.get(0)));
    }

    @Test
    public void test2Elements() {
        List<Integer> ints = Arrays.asList(1, 2);
        List<List<Integer>> result = Permutations.of(ints);

        assertEquals(2, result.size());
        assertThat(Arrays.asList(2, 1), is(result.get(0)));
        assertThat(Arrays.asList(1, 2), is(result.get(1)));
    }

    @Test
    public void test3Elements() {
        List<Integer> ints = Arrays.asList(5, 6, 7);
        List<List<Integer>> result = Permutations.of(ints);

        assertEquals(6, result.size());
        assertThat(Arrays.asList(7, 6, 5), is(result.get(0)));
        assertThat(Arrays.asList(6, 7, 5), is(result.get(1)));
        assertThat(Arrays.asList(6, 5, 7), is(result.get(2)));
        assertThat(Arrays.asList(7, 5, 6), is(result.get(3)));
        assertThat(Arrays.asList(5, 7, 6), is(result.get(4)));
        assertThat(Arrays.asList(5, 6, 7), is(result.get(5)));
    }
    
    @Test
    public void test4Elements() {
        List<Integer> ints = Arrays.asList(5, 6, 7, 8);
        List<List<Integer>> result = Permutations.of(ints);

        assertEquals(24, result.size());
        assertThat(Arrays.asList(8, 7, 6, 5), is(result.get(0)));
        assertThat(Arrays.asList(5, 6, 7, 8), is(result.get(23)));
    }

    @Test
    public void test10Elements() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collection<List<Integer>> result = Permutations.of(ints);

        assertEquals(362880, result.size());
    }

    @Test
    public void test10ElementsGuava() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Collection<List<Integer>> guavaResult = Collections2.orderedPermutations(ints);
        assertEquals(362880, guavaResult.size());
    }
}
