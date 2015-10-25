package com.nobullet.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * HashBidiMap tests.
 */
public class HashBidiMapTest {

    @Test
    public void test() {
        HashBidiMap<String, Integer> map = new HashBidiMap<>();

        map.put("1", 2);
        assertEquals(Integer.valueOf(2), map.get("1"));
        assertEquals("1", map.getByValue(2));

        map.put("2", 3);
        assertEquals(Integer.valueOf(3), map.get("2"));
        assertEquals("2", map.getByValue(3));

        map.put("1", 4); // Remap existing pair.
        assertEquals(Integer.valueOf(4), map.get("1"));
        assertEquals("1", map.getByValue(4));

        map.remove("2");
        assertNull(map.get("2"));
        assertNull(map.getByValue(3));

        map.removeByValue(4);
        assertNull(map.get("1"));
        assertNull(map.getByValue(4));
    }
}
