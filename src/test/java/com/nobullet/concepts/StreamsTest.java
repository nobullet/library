package com.nobullet.concepts;

import static org.junit.Assert.assertEquals;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * Tests for streams.
 */
public class StreamsTest {

    @Test
    public void testStreams() {
        String result;
        BinaryOperator<String> accumulator = (a, b) -> a + ", " + b;
        
        for (int i = 0; i < 10000; i++) {
            result = Stream.of("1", "2", "3").parallel().reduce(accumulator).get();
            assertEquals("1, 2, 3", result);

            result = Stream.of("1", "2", "3").parallel().reduce("A", accumulator);
            assertEquals("A, 1, A, 2, A, 3", result);

            result = Stream.of("1", "2", "3").reduce("A", accumulator);
            assertEquals("A, 1, 2, 3", result);
        }
    }
}
