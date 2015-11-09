package com.nobullet.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link ObjectDisjointSet}.
 */
public class ObjectDisjointSetTest {

    static final Set<String> STRINGS = Sets.newHashSet("0", "1", "2", "3", "4", "5", "6", "7");
    
    @Test
    public void testBasics() {
        ObjectDisjointSet<String> ods = new ObjectDisjointSet<>(STRINGS);

        for (String s : STRINGS) {
            assertTrue("Initial state is expected each item in its own set.",
                    Sets.newHashSet(s).containsAll(ods.getUnionMembers(s)));
            assertEquals("Initial group size is 1.", 1, ods.getUnionSize(s));
        }

        ods.union("3", "0");
        ods.union("5", "3");
        ods.union("6", "5");

        ods.union("2", "4");
        ods.union("1", "2");
        //ods.union("2", "0");

        assertTrue(Sets.newHashSet("0", "3", "5", "6").containsAll(ods.getUnionMembers("6")));
        assertTrue(Sets.newHashSet("1", "2", "4").containsAll(ods.getUnionMembers("2")));
    }
    
    @Test
    public void testBasicsBigUnion() {
        ObjectDisjointSet<String> ods = new ObjectDisjointSet<>(STRINGS);

        ods.union("3", "0");
        ods.union("5", "3");
        ods.union("6", "5");

        ods.union("2", "4");
        ods.union("1", "2");
        ods.union("2", "0");

        assertTrue(Sets.newHashSet("1", "2", "4", "0", "3", "5", "6").containsAll(ods.getUnionMembers("6")));
    }
}
