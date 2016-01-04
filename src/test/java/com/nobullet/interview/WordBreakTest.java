package com.nobullet.interview;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link WordBreak}.
 */
public class WordBreakTest {

    @Test
    public void test() {
        Set<String> dictionary = new HashSet<>(Arrays.asList("mobile", "samsung", "sam", "sung", "man", "mango",
                "icecream", "and", "go", "i", "like", "ice", "cream"));

        List<String> words = WordBreak.fromString("imangomanicemobile", dictionary);
        assertEquals("Size: " + words.size(), Arrays.asList("i", "man", "go", "man", "ice", "mobile"), words);
    }
}
