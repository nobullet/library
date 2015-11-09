package com.nobullet.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link DisjointSet}.
 */
public class DisjointSetTest {

    @Test
    public void testBasicOperations() {
        DisjointSet ds = new DisjointSet(6);

        for (int i = 0; i < ds.size(); i++) {
            assertEquals("Initial state is expected each item in its own set.", i, ds.find(i));
            assertTrue("Initial state is expected each item in its own set.",
                    Sets.newHashSet(i).containsAll(ds.getUnionMembers(i)));
            assertEquals("Initial group size is 1.", 1, ds.getUnionSize(i));
        }

        // Group 1.
        assertEquals("Union must produce lesser result.", 0, ds.union(3, 0));
        assertEquals(0, ds.find(0));
        assertEquals(0, ds.find(3));
        assertEquals(5, ds.find(5));
        assertEquals(2, ds.getUnionSize(0));
        assertEquals(2, ds.getUnionSize(3));
        assertEquals(1, ds.getUnionSize(5));
        assertTrue("0, 3 are expected to be in union.", Sets.newHashSet(0, 3).containsAll(ds.getUnionMembers(0)));
        assertTrue("0, 3 are expected to be in union.", Sets.newHashSet(0, 3).containsAll(ds.getUnionMembers(3)));

        assertEquals(0, ds.union(3, 5));
        assertEquals(0, ds.find(0));
        assertEquals(0, ds.find(3));
        assertEquals(0, ds.find(5));
        assertEquals(3, ds.getUnionSize(0));
        assertEquals(3, ds.getUnionSize(3));
        assertEquals(3, ds.getUnionSize(5));

        // Group 2.
        assertEquals("Union must produce lesser result.", 1, ds.union(1, 2));
        assertEquals(1, ds.find(2));
        assertEquals(2, ds.getUnionSize(1));
        assertEquals(2, ds.getUnionSize(2));
        assertEquals(1, ds.getUnionSize(4));

        assertEquals(1, ds.union(2, 4));
        assertEquals(1, ds.find(2));
        assertEquals(1, ds.find(4));

        assertEquals(3, ds.getUnionSize(1));
        assertEquals(3, ds.getUnionSize(2));
        assertEquals(3, ds.getUnionSize(4));

        // Join groups 1 & 2.
        assertEquals(0, ds.union(3, 4));

        Set<Integer> allElements = Sets.newHashSet(0, 1, 2, 3, 4, 5);
        for (int i = 0; i < ds.size(); i++) {
            assertEquals("Final state is expected each item to be in the set 0.", 0, ds.find(i));
            assertTrue("Final state is expected each item in its own set.",
                    allElements.containsAll(ds.getUnionMembers(i)));
            assertEquals("Final union size is 6.", 6, ds.getUnionSize(i));
        }
    }

    @Test
    public void testBasicOperationsReverseOrder() {
        DisjointSet ds = new DisjointSet(6);

        for (int i = 0; i < ds.size(); i++) {
            assertEquals("Initial state is expected each item in its own set.", i, ds.find(i));
            assertTrue("Initial state is expected each item in its own set.",
                    Sets.newHashSet(i).containsAll(ds.getUnionMembers(i)));
            assertEquals("Initial group size is 1.", 1, ds.getUnionSize(i));
        }

        // Group 1.
        assertEquals("Union must produce lesser result.", 0, ds.union(3, 0));
        assertTrue("0, 3 are expected to be in union.", Sets.newHashSet(0, 3).containsAll(ds.getUnionMembers(3)));
        assertTrue("0, 3 are expected to be in union.", Sets.newHashSet(0, 3).containsAll(ds.getUnionMembers(0)));
        assertEquals(0, ds.find(0));
        assertEquals(0, ds.find(3));
        assertEquals(5, ds.find(5));
        assertEquals(2, ds.getUnionSize(0));
        assertEquals(2, ds.getUnionSize(3));
        assertEquals(1, ds.getUnionSize(5));

        assertEquals(0, ds.union(3, 5));
        assertTrue("0, 3, 5 are expected to be in union.", Sets.newHashSet(0, 3, 5).containsAll(ds.getUnionMembers(5)));
        assertTrue("0, 3, 5 are expected to be in union.", Sets.newHashSet(0, 3, 5).containsAll(ds.getUnionMembers(0)));
        assertTrue("0, 3, 5 are expected to be in union.", Sets.newHashSet(0, 3, 5).containsAll(ds.getUnionMembers(3)));
        assertEquals(0, ds.find(0));
        assertEquals(0, ds.find(3));
        assertEquals(0, ds.find(5));
        assertEquals(3, ds.getUnionSize(0));
        assertEquals(3, ds.getUnionSize(3));
        assertEquals(3, ds.getUnionSize(5));

        // Group 2.
        assertEquals("Union must produce lesser result.", 1, ds.union(1, 2));
        assertEquals(1, ds.find(2));
        assertEquals(2, ds.getUnionSize(1));
        assertEquals(2, ds.getUnionSize(2));
        assertEquals(1, ds.getUnionSize(4));

        assertEquals(1, ds.union(4, 2));
        assertEquals(1, ds.find(2));
        assertEquals(1, ds.find(4));

        assertEquals(3, ds.getUnionSize(1));
        assertEquals(3, ds.getUnionSize(2));
        assertEquals(3, ds.getUnionSize(4));

        // Join groups 1 & 2.
        assertEquals(0, ds.union(4, 3));

        Set<Integer> allElements = Sets.newHashSet(0, 1, 2, 3, 4, 5);
        for (int i = 0; i < ds.size(); i++) {
            assertEquals("Final state is expected each item to be in the set 0.", 0, ds.find(i));
            assertTrue("Final state is expected each item in its own set.",
                    allElements.containsAll(ds.getUnionMembers(i)));
            assertEquals("Final union size is 6.", 6, ds.getUnionSize(i));
        }
    }
    
    @Test
    public void testUnionMembers() {
        DisjointSet ds = new DisjointSet(7);
        ds.union(3, 0);
        ds.union(5, 3);
        ds.union(6, 5);
        
        ds.union(2, 4);
        ds.union(1, 2);
        ds.union(2, 0);
        
        Set<Integer> allElements = Sets.newHashSet(0, 1, 2, 3, 4, 5, 6);
        assertTrue("Contains all elements." , allElements.containsAll(ds.getUnionMembers(0)));
    }
}
