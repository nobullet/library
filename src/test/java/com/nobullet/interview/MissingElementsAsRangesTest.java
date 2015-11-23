package com.nobullet.interview;

import static org.junit.Assert.assertEquals;
import java.util.function.BiConsumer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MissingElementsAsRanges}.
 */
public class MissingElementsAsRangesTest {

    GroupPrinter printer;

    @Before
    public void setUp() {
        this.printer = new GroupPrinter();
    }

    @Test
    public void test() {
        MissingElementsAsRanges.find(0, 99, new int[]{88, 105, 3, 2, 4, 200, 0, 10}, printer);
        assertEquals("1,5-9,11-87,89-99", printer.toString());
    }

    @Test
    public void test2() {
        MissingElementsAsRanges.find(0, 99, new int[]{88, 105, 3, 2, 4, 200, 0, 10, 99}, printer);
        assertEquals("1,5-9,11-87,89-99", printer.toString());
    }

    @Test
    public void test3() {
        MissingElementsAsRanges.find(0, 99, new int[]{88, 105, 3, 2, 4, 200, 0, 10, 99, 98}, printer);
        assertEquals("1,5-9,11-87,89-97", printer.toString());
    }
    
    @Test
    public void test4() {
        MissingElementsAsRanges.find(5, 99, new int[]{88, 105, 3, 2, 4, 200, 0, 10, 99, 98}, printer);
        assertEquals("5-9,11-87,89-97", printer.toString());
    }

    private static class GroupPrinter implements BiConsumer<Integer, Integer> {

        final StringBuilder result = new StringBuilder();

        @Override
        public void accept(Integer a, Integer b) {
            if (!a.equals(b)) {
                if (a + 1 != b) {
                    result.append(a).append('-').append(b).append(",");
                } else {
                    result.append(a).append(',').append(b).append(",");
                }
            } else {
                result.append(a).append(",");
            }
        }

        @Override
        public String toString() {
            result.setLength(result.length() - 1);
            return result.toString();
        }
    }
}
