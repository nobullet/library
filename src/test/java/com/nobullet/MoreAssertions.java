package com.nobullet;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.Assert;

/**
 * More assertions.
 */
public final class MoreAssertions {

    /**
     * Asserts that two string contains same characters (equal). Shows detailed report about failure index / length.
     *
     * @param s1 String 1.
     * @param s2 String 2.
     */
    public static void assertStringsSame(String s1, String s2) {
        assertStringsSame("", s1, s2);
    }

    /**
     * Asserts that two string contains same characters (equal). Shows detailed report about failure index / length.
     *
     * @param message Message to use in failure.
     * @param s1 String 1.
     * @param s2 String 2.
     */
    public static void assertStringsSame(String message, String s1, String s2) {
        message = message.trim();
        if (message.length() != 0 && message.charAt(message.length() - 1) != '.') {
            message += '.';
        }
        int maxIndex = Math.min(s1.length(), s2.length());
        char ch1 = ' ', ch2 = ' ';
        for (int i = 0; i < maxIndex; i++) {
            ch1 = s1.charAt(i);
            ch2 = s2.charAt(i);
            if (ch1 != ch2) {
                String failure;
                if (s1.length() == s2.length()) {
                    failure = String.format("%s Character index: %d ('%s' != '%s').", message, i, ch1, ch2);
                } else {
                    failure = String.format("%s Character index: %d ('%s' != '%s'). Lengths differ: %d != %d.",
                            message, i, ch1, ch2, s1.length(), s2.length());
                }
                Assert.fail(failure.trim());
            }
        }
        if (s1.length() != s2.length()) {
            Assert.fail(String.format("%s Lengths differ: %d != %d. Last chars: ('%s' == '%s')",
                    message, s1.length(), s2.length(), ch1, ch2).trim());
        }
    }

    /**
     * Asserts that all lines in string has same length.
     *
     * @param content Content to check.
     * @param expectedNumberOfLines Expected number of lines.
     */
    public static void assertStringLinesOfASameLength(String content, int expectedNumberOfLines) {
        assertStringLinesOfASameLength(content, expectedNumberOfLines, false);
    }

    /**
     * Asserts that all lines in string has same length.
     *
     * @param content Content to check.
     * @param expectedNumberOfLines Expected number of lines.
     * @param ignoreFirstLine Whether to ignore the first line.
     */
    public static void assertStringLinesOfASameLength(String content, int expectedNumberOfLines,
            boolean ignoreFirstLine) {
        int lines = 2;
        try (BufferedReader br = new BufferedReader(new StringReader(content))) {
            String newLine;
            if (ignoreFirstLine) {
                newLine = br.readLine();
                assertTrue("First line must be empty.", newLine.isEmpty());
            }
            int prevLineLength = -1;
            String prevLine = null;
            while ((newLine = br.readLine()) != null) {
                lines++;
                if (prevLineLength < 0) {
                    prevLineLength = newLine.length();
                    prevLine = newLine;
                } else {
                    if (prevLineLength != newLine.length()) {
                        fail(String.format("'%s' is not the same length as '%s' (expected %d)",
                                prevLine, newLine, prevLineLength));
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error while reading string.", ex);
        }
        assertEquals(String.format("Expected %d lines, got %d.", expectedNumberOfLines, lines),
                expectedNumberOfLines, lines);
    }

    /**
     * Asserts that two given lists contain same elements.
     *
     * @param <T> Type.
     * @param list1 List 1.
     * @param list2 List 2.
     */
    public static <T> void assertListsEqual(List<? extends T> list1, List<? extends T> list2) {
        assertThat(list1).containsExactlyElementsIn(list2);
    }

    private MoreAssertions() {
    }
}
