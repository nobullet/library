package com.nobullet.interview;

import static com.nobullet.interview.WordTransformation.transformationFor;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Tests for {@link WordTransformation}.
 */
public class WordTransformationTest {

    @Test
    public void testSmall() {
        Set<String> dictionary = Sets.newHashSet("cat", "bat", "bet", "bed", "at", "ad", "ed");
        List<String> result = transformationFor("cat", "bed", dictionary);
        assertThat(Lists.newArrayList("cat", "bat", "bet", "bed"), contains(result.toArray()));
        
        assertTrue("Empty path for not a word.", transformationFor("cat", "ggg", dictionary).isEmpty());
        assertTrue("Empty path for not a word.", transformationFor("eee", "bat", dictionary).isEmpty());
    }
    
    @Test
    public void testNoPath() {
        Set<String> dictionary = Sets.newHashSet("tog", "rog", "smog", "doll", "dog", "bog", "mog", "rod", "roll");
        List<String> result = transformationFor("roll", "dog", dictionary);
        assertThat(Lists.newArrayList("roll"), contains(result.toArray()));
    }
    
    @Test
    public void testSmogToDog() {
        Set<String> dictionary = Sets.newHashSet("tog", "rog", "smog", "doll", "dog", "bog", "mog", "log", "clog");
        List<String> result = transformationFor("smog", "dog", dictionary);
        assertThat(Lists.newArrayList("smog", "mog", "dog"), contains(result.toArray()));
    }
    
    @Test
    public void testSmogTolog() {
        Set<String> dictionary = Sets.newHashSet("smog", "doll", "dog", "bog", "mog", "log", "clog");
        List<String> result = transformationFor("smog", "clog", dictionary);
        assertThat(Lists.newArrayList("smog", "mog", "log", "clog"), contains(result.toArray()));
    }
}
