package com.nobullet;

import com.nobullet.NestedIterators;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * Tests for {@link NestedIterators}.
 */
public class NestedIteratorsTest {

    static final Logger logger = Logger.getLogger(NestedIteratorsTest.class.getName());

    @Test
    public void testArrayIteratorWithoutNulls() {
        Iterator<Object> iterator = new NestedIterators.NestedArrayIterator(createNestedArray(false));
        assertEquals("L1:1,L1:2,L3:1,L3:2,L3:3,L2:1,L2:2,L2:3,L1:3", reduceToString(iterator));
    }

    @Test
    public void testArrayIteratorWithNulls() {
        Iterator<Object> iterator = new NestedIterators.NestedArrayIterator(createNestedArray(true));
        assertEquals("L1:1,L1:2,L3:1,L3:2,null,L3:3,L2:1,L2:2,L2:3,null,L1:3", reduceToString(iterator));
    }

    @Test
    public void testCollectionIteratorWithNulls() {
        Iterator<Object> iterator = new NestedIterators.NestedCollectionIterator(createNestedCollection(true));
        assertEquals("null,L1:1,L1:2,L1:3,L3:1,L3:2,null,L3:3,L2:1,null,L2:2,L2:3,L1:4,null", reduceToString(iterator));
    }

    @Test
    public void testCollectionIteratorWithSkipNullIterator() {
        List<Object> list = createNestedCollection(true);
        Iterator<Object> iterator = new NestedIterators.SkipNullIterator<>(
                new NestedIterators.NestedCollectionIterator(list));
        assertEquals("Iteration must produce items by DFS order (depth-first search).",
                "L1:1,L1:2,L1:3,L3:1,L3:2,L3:3,L2:1,L2:2,L2:3,L1:4", reduceToString(iterator));
    }

    @Test
    public void testCollectionIteratorWithoutNullsWithNestedIterators() {
        List<Object> list = createNestedCollection(false);
        Iterator<Object> iterator = new NestedIterators.NestedCollectionIterator(list, false);
        assertEquals("Iteration must produce items by levels, BFS (breadth-first search).", 
                "L1:1,L1:2,L1:3,L1:4,L2:1,L2:2,L2:3,L3:1,L3:2,L3:3", reduceToString(iterator));
    }

    @Test
    public void testCollectionIteratorWithoutNulls() {
        Iterator<Object> iterator = new NestedIterators.NestedCollectionIterator(createNestedCollection(false));
        assertEquals("L1:1,L1:2,L1:3,L3:1,L3:2,L3:3,L2:1,L2:2,L2:3,L1:4", reduceToString(iterator));
    }

    static List<Object> createNestedCollection(boolean withNulls) {
        List<Object> inner2 = new ArrayList<>();
        inner2.add("L3:1");
        inner2.add("L3:2");
        if (withNulls) {
            inner2.add(null);
        }
        inner2.add("L3:3");

        List<Object> inner = new ArrayList<>();
        inner.add(inner2);
        inner.add("L2:1");
        if (withNulls) {
            inner.add(null);
        }
        inner.add("L2:2");
        inner.add("L2:3");

        List<Object> list = new ArrayList<>();
        if (withNulls) {
            list.add(null);
        }
        list.add("L1:1");
        list.add("L1:2");
        list.add("L1:3");
        list.add(inner);
        list.add("L1:4");
        if (withNulls) {
            list.add(null);
        }
        return list;
    }

    static Object[] createNestedArray(boolean withNulls) {
        Object[] inner2 = new Object[withNulls ? 4 : 3];
        inner2[0] = "L3:1";
        inner2[1] = "L3:2";
        inner2[withNulls ? 3 : 2] = "L3:3";

        Object[] inner = new Object[withNulls ? 5 : 4];
        inner[0] = inner2;
        inner[1] = "L2:1";
        inner[2] = "L2:2";
        inner[3] = "L2:3";

        Object[] r = new Object[4];
        r[0] = "L1:1";
        r[1] = "L1:2";
        r[2] = inner;
        r[3] = "L1:3";
        return r;
    }

    static <E> String reduceToString(Iterator<E> iterator) {
        StringJoiner joiner = new StringJoiner(",");
        iterator.forEachRemaining(item -> joiner.add(toString(item)));
        return joiner.toString();
    }
    
    static String toString(Object o) {
        return o == null ? "null" : o.toString();
    }
}
