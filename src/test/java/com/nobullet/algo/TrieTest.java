package com.nobullet.algo;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Lists;
import com.nobullet.Benchmarks;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Trie}.
 */
public class TrieTest {

    static final String WORDS_FILE = "english-58000.txt";
    static final Logger logger = Logger.getLogger(TrieTest.class.getName());

    Trie trie;

    @Before
    public void setUp() {
        this.trie = new Trie();
    }

    @Test
    public void testBasic() {
        assertFalse(trie.add(""));
        assertFalse(trie.add(null));
        assertTrue(trie.add("123"));
        assertTrue(trie.add("124"));
        assertListsEqual(Lists.newArrayList("123", "124"), trie.find("12"));
        assertListsEqual(Lists.newArrayList("123", "124"), trie.find("1"));
        assertListsEqual(Collections.emptyList(), trie.find(""));
        assertListsEqual(Collections.emptyList(), trie.find(null));
    }

    @Test
    public void testGeeks() {
        List<String> expected = Lists.newArrayList("geeksgeeks", "geeksquiz", "geeksforgeeks");
        assertTrue(trie.addAll(expected));
        assertListsEqual(expected, trie.find("geek"));
        assertListsEqual(expected, trie.find("gee"));
        assertListsEqual(expected, trie.find("ge"));
        assertListsEqual(expected, trie.find("g"));

        assertListsEqual(Collections.emptyList(), trie.find("a"));
        assertListsEqual(Collections.emptyList(), trie.find("geez"));
    }

    @Test
    public void test58000() throws IOException {
        readWords().stream().forEach(trie::add);
        assertListsEqual(Lists.newArrayList("nirvana"), trie.find("nir"));
    }

    @Test
    public void testCompareLinearVsTrieOn58000() throws IOException {
        List<String> words = readWords();
        words.stream().forEach(trie::add);

        Benchmarks bm = new Benchmarks();
        List<String> prefixesToCheck = Lists.newArrayList("foo", "meta", "nir", "zoo");

        for (String prefix : prefixesToCheck) {
            for (int i = 0; i < 5; i++) {
                List<String> linearResults = bm.benchmark(prefix + "_LINR", () -> {
                    return find(prefix, words);
                });
                List<String> trieResults = bm.benchmark(prefix + "_TRIE", () -> {
                    return trie.find(prefix);
                });
                assertListsEqual(linearResults, trieResults);
            }
        }
        logger.info(bm.getStatistics().toString(6));
    }

    private List<String> find(String prefix, List<String> values) {
        return values.stream().filter((word) -> (word.startsWith(prefix))).collect(Collectors.toList());
    }

    private List<String> readWords() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("english-58000.txt");
        assertNotNull("Words file is expected.", is);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        List<String> words = new ArrayList<>(58000);
        while ((line = buf.readLine()) != null) {
            line = line.trim();
            words.add(line);
        }
        return words;
    }
}
