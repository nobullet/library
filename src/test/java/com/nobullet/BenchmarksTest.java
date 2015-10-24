package com.nobullet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import com.google.common.base.Ticker;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.Test;


/**
 * Tests for BenchmarksTest.
 */
public class BenchmarksTest {

    static final Logger logger = Logger.getLogger(BenchmarksTest.class.getName());

    static class TestTicker extends Ticker {

        static final Random random = new Random();
        static final long FIVE_SECONDS = (long) (5.0D * 1e9);
        long value = System.nanoTime();

        static long nextLong(long bound) {
            long res = random.nextLong();
            if (res < 0) {
                res *= -1L;
            }
            res = res % bound;
            return res;
        }

        @Override
        public long read() {
            value += nextLong(FIVE_SECONDS);
            return value++;
        }
    }

    static class Math {

        public Integer square(Integer other) {
            return other * other;
        }

        public Integer fact(Integer other) {
            if (other <= 1) {
                return 1;
            }
            return other * fact(other - 1);
        }
    }

    @Test
    public void testSingleBenchmark() {
        Benchmarks bm = new Benchmarks(new TestTicker());
        Math math = new Math();

        Integer source = 10;
        Integer result = bm.benchmark("Square", () -> {
            return math.square(source);
        });
        assertEquals(100, (int) result);

        String table = bm.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 25);
    }

    @Test
    public void testDoubleBenchmark() {
        Benchmarks bm = new Benchmarks(new TestTicker());
        Math math = new Math();

        Integer source = 10;
        Integer result = bm.benchmark("Square", () -> {
            return math.square(source);
        });
        assertEquals(100, (int) result);

        result = bm.benchmark("Fact", () -> {
            return math.fact(source);
        });
        assertEquals(3_628_800, (int) result);

        String table = bm.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 25);
    }

    @Test
    public void testDoubleBenchmarkWithError() {
        Benchmarks bm = new Benchmarks(new TestTicker());
        Math math = new Math();

        Integer source = 10;
        Integer result;

        try {
            result = bm.benchmark("Square", () -> {
                throw new IllegalArgumentException("Error");
            });
            fail("Exception is expected.");
        } catch (RuntimeException e) {
            assertEquals("Error", e.getMessage());
        }

        result = bm.benchmark("Fact", () -> {
            return math.fact(source);
        });
        assertEquals(3_628_800, (int) result);

        String table = bm.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 25);
    }

    @Test
    public void testMultipleBenchmark2Results() {
        Benchmarks benchmark = new Benchmarks(new TestTicker());

        run(benchmark, 2);

        String table = benchmark.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 26);
    }

    @Test
    public void testMultipleBenchmark10Results() {
        Benchmarks benchmark = new Benchmarks(new TestTicker());

        run(benchmark, 10);

        String table = benchmark.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 34);
    }

    @Test
    public void testMultipleBenchmark20Results() {
        Benchmarks benchmark = new Benchmarks(new TestTicker());

        run(benchmark, 20);

        String table = benchmark.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 44);
        assertFalse("Contains no skip lines (...).", table.contains("..."));
    }
    
    @Test
    public void testMultipleBenchmark21Results() {
        Benchmarks benchmark = new Benchmarks(new TestTicker());

        run(benchmark, 21);

        String table = benchmark.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 45);
        assertTrue("Contains skip lines (...).", table.contains("..."));
    }

    @Test
    public void testMultipleBenchmark2000Results() {
        Benchmarks benchmark = new Benchmarks(new TestTicker());

        run(benchmark, 2000);

        String table = benchmark.getStatistics().toString();
        logger.info(table);
        assertAllLinesInBenchmarksOfASameLength(table, 45);
    }

    void run(Benchmarks bm, int tests) {
        Math math = new Math();

        Integer source10 = 10;
        Integer source14 = 14;
        while (tests-- > 0) {
            bm.benchmark("Square10", () -> {
                return math.square(source10);
            });

            bm.benchmark("Square14", () -> {
                return math.square(source14);
            });

            bm.benchmark("Fact10", () -> {
                return math.fact(source10);
            });

            bm.benchmark("Fact14", () -> {
                return math.fact(source14);
            });
        }
    }

    static void assertAllLinesInBenchmarksOfASameLength(String benchmarkTable, int expectedNumberOfLines) {
        MoreAssertions.assertStringLinesOfASameLength(benchmarkTable, expectedNumberOfLines, true);
    }
}
