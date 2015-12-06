package com.nobullet.algo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

/**
 * Tests for {@link MinimalEditOperations}.
 */
public class MinimalEditOperationsTest {

    @Test
    public void test() {
        List<MinimalEditOperations.Operation> operations;

        operations = MinimalEditOperations.fromStrings("abcd", "abxd");
        assertEquals(1, operations.size());
        assertSameOperation("REPLACE", 2, 2, operations.get(0));

        operations = MinimalEditOperations.fromStrings("sunday", "saturday");
        assertEquals(3, operations.size());
        assertSameOperation("INSERT", 1, 1, operations.get(0));
        assertSameOperation("INSERT", 1, 2, operations.get(1));
        assertSameOperation("REPLACE", 2, 4, operations.get(2));

        operations = MinimalEditOperations.fromStrings("a123x", "a456x");
        assertEquals(3, operations.size());
        assertSameOperation("REPLACE", 1, 1, operations.get(0));
        assertSameOperation("REPLACE", 2, 2, operations.get(1));
        assertSameOperation("REPLACE", 3, 3, operations.get(2));
    }

    private static void assertSameOperation(String type, int i, int j, MinimalEditOperations.Operation op) {
        String expectedOp = String.format("{%s, (%d, %d)}", type, i, j);
        assertEquals(expectedOp, MinimalEditOperations.Type.valueOf(type), op.getType());
        assertEquals(expectedOp, i, op.getSource1Index());
        assertEquals(expectedOp, j, op.getSource2Index());
    }
}
