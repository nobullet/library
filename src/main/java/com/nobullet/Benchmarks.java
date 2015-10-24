package com.nobullet;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.base.Ticker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Collects information about benchmarks measuring execution time and building representative table with: bottom/top 10
 * results, mean value, geometry mean, sum, min/max, 25, 50, 75, 90, 95, 99 percentiles statistics. Provides output like
 * this:
 * <pre>
 * --------------------------------------------------------
 * |          |   Fact10 |   Fact14 | Square10 | Square14 |
 * --------------------------------------------------------
 * |          |    0.001 |    0.008 |    0.007 |    0.000 |
 * |          |    0.008 |    0.016 |    0.012 |    0.002 |
 * |          |    0.018 |    0.016 |    0.013 |    0.004 |
 * |          |    0.025 |    0.017 |    0.015 |    0.013 |
 * |          |    0.029 |    0.019 |    0.016 |    0.017 |
 * |          |    0.033 |    0.020 |    0.017 |    0.018 |
 * |          |    0.034 |    0.024 |    0.026 |    0.019 |
 * |          |    0.041 |    0.024 |    0.033 |    0.019 |
 * |          |    0.043 |    0.027 |    0.033 |    0.020 |
 * |          |    0.045 |    0.027 |    0.033 |    0.020 |
 * |          |      ... |      ... |      ... |      ... |
 * |          |    4.973 |    4.975 |    4.980 |    4.966 |
 * |          |    4.974 |    4.975 |    4.982 |    4.969 |
 * |          |    4.975 |    4.977 |    4.983 |    4.970 |
 * |          |    4.976 |    4.979 |    4.983 |    4.974 |
 * |          |    4.976 |    4.979 |    4.987 |    4.975 |
 * |          |    4.985 |    4.981 |    4.991 |    4.984 |
 * |          |    4.988 |    4.982 |    4.991 |    4.988 |
 * |          |    4.991 |    4.983 |    4.995 |    4.989 |
 * |          |    4.992 |    4.996 |    4.996 |    4.990 |
 * |          |    4.995 |    4.997 |    4.998 |    4.999 |
 * --------------------------------------------------------
 * |      Min |    0.001 |    0.008 |    0.007 |    0.000 |
 * |      Max |    4.995 |    4.997 |    4.998 |    4.999 |
 * --------------------------------------------------------
 * |     Geom |    1.864 |    1.909 |    1.804 |    1.791 |
 * |     Mean |    2.506 |    2.543 |    2.464 |    2.458 |
 * |      Sum | 5011.664 | 5085.856 | 4928.558 | 4916.859 |
 * --------------------------------------------------------
 * |      25% |    1.278 |    1.271 |    1.164 |    1.194 |
 * |      50% |    2.513 |    2.539 |    2.483 |    2.427 |
 * |      75% |    3.752 |    3.815 |    3.720 |    3.746 |
 * |      90% |    4.469 |    4.564 |    4.484 |    4.452 |
 * |      95% |    4.713 |    4.801 |    4.762 |    4.747 |
 * |      99% |    4.955 |    4.957 |    4.957 |    4.923 |
 * --------------------------------------------------------
 * |    Total |     2000 |     2000 |     2000 |     2000 |
 * |     Fail |        0 |        0 |        0 |        0 |
 * |    Fail% |    0.000 |    0.000 |    0.000 |    0.000 |
 * --------------------------------------------------------
 * </pre>
 */
public class Benchmarks {

    // Default percentiles.
    static final int[] DEFAULT_PERCENTILES = {25, 50, 75, 90, 95, 99};

    final Map<Comparable<?>, TemporaryTagStatistics> tagsStats = Maps.newConcurrentMap();
    final Ticker ticker;

    public Benchmarks(Ticker ticker) {
        this.ticker = ticker;
    }

    public Benchmarks() {
        this.ticker = Ticker.systemTicker();
    }

    /**
     * Executes given callable and measures the execution time storing the time of execution as a tag.
     *
     * @param <V> Return type.
     * @param tag Tag.
     * @param callable Callable to execute.
     * @param times TImes to invoke the callable.
     * @return List of results of callable invocations.
     */
    public <V> List<V> benchmark(Comparable<?> tag, Callable<V> callable, int times) {
        List<V> result = Lists.newArrayListWithCapacity(times);
        for (int i = 0; i < times; i++) {
            result.add(benchmark(tag, callable));
        }
        return result;
    }

    /**
     * Executes given callable and measures the execution time storing the time of execution as a tag.
     *
     * @param <V> Return type.
     * @param tag Tag.
     * @param callable Callable to execute.
     * @return Result of callable invocation.
     */
    public <V> V benchmark(Comparable<?> tag, Callable<V> callable) {
        TemporaryTagStatistics tagStats = getOrCreate(tag);
        Stopwatch watch = Stopwatch.createUnstarted(this.ticker);
        Throwable throwable = null;
        try {
            watch.start();
            return callable.call();
        } catch (Throwable t) {
            throwable = t;
            tagStats.exception();
            throw Throwables.propagate(t);
        } finally {
            watch.stop();
            if (throwable == null) {
                tagStats.add(watch);
            }
        }
    }

    /**
     * Executes given runnable and measures the execution time storing the time of execution as a tag.
     *
     * @param tag Tag.
     * @param runnable Callable to execute.
     * @param times Times to execute the runnable.
     */
    public void benchmark(Comparable<?> tag, Runnable runnable, int times) {
        Callable<?> callable = () -> {
            runnable.run();
            return null;
        };
        for (int i = 0; i < times; i++) {
            benchmark(tag, callable);
        }
    }

    /**
     * Executes given runnable and measures the execution time storing the time of execution as a tag.
     *
     * @param tag Tag.
     * @param runnable Callable to execute.
     */
    public void benchmark(Comparable<?> tag, Runnable runnable) {
        benchmark(tag, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Returns benchmark statistics.
     *
     * @return Statistics object.
     */
    public Statistics getStatistics() {
        Map<Comparable<?>, TagStatistics> data = Maps.newHashMapWithExpectedSize(tagsStats.size());
        tagsStats.entrySet().stream().forEach((entry) -> {
            data.put(entry.getKey(), entry.getValue().toTagStatistics());
        });
        return new Statistics(data);
    }

    TemporaryTagStatistics getOrCreate(Comparable<?> tag) {
        TemporaryTagStatistics existing = tagsStats.get(tag);
        if (existing == null) {
            TemporaryTagStatistics newTag = new TemporaryTagStatistics(tag);
            existing = tagsStats.putIfAbsent(tag, newTag);
            if (existing == null) {
                return newTag;
            }
        }
        return existing;
    }

    void checkSize() {
        if (tagsStats.isEmpty()) {
            throw new IllegalStateException("At least one benchmark is expected.");
        }
    }

    /**
     * Statistics class for benchmarks.
     */
    public static class Statistics {

        final Map<Comparable<?>, TagStatistics> tagsStats;
        final Set<Comparable<?>> tags;

        Statistics(Map<Comparable<?>, TagStatistics> tagsStats) {
            this.tagsStats = tagsStats;
            this.tags = Collections.unmodifiableSet(tagsStats.keySet());
        }

        /**
         * Returns all tags for current benchmarks.
         *
         * @return All tags.
         */
        public Set<Comparable<?>> getTags() {
            return tags;
        }

        /**
         * Returns statistics for given tag.
         *
         * @param tag Tag.
         * @return Statistics for given tag.
         */
        public TagStatistics getTagStatistics(Comparable<?> tag) {
            return stats(tag);
        }

        /**
         * Returns number of successful benchmarks (for all tags).
         *
         * @return Number of successful benchmarks.
         */
        public int getSuccessful() {
            return tagsStats.values().stream().mapToInt(TagStatistics::getSuccessful).sum();
        }

        /**
         * Returns number of failed benchmarks (for all tags).
         *
         * @return Number of failed benchmarks.
         */
        public int getFailed() {
            return tagsStats.values().stream().mapToInt(TagStatistics::getFailed).sum();
        }

        /**
         * Returns total number of benchmarks (for all tags).
         *
         * @return Total number of benchmarks.
         */
        public int getTotal() {
            return tagsStats.values().stream().mapToInt((el) -> el.getFailed() + el.getSuccessful()).sum();
        }

        /**
         * Returns percent of failed benchmarks (for all tags).
         *
         * @return Percent of failed benchmarks (for all tags).
         */
        public double getFailedPercent() {
            return 100.0D * getFailed() / getTotal();
        }

        TagStatistics stats(Comparable<?> tag) {
            Preconditions.checkNotNull(tag);
            TagStatistics stats = tagsStats.get(tag);
            Preconditions.checkNotNull(tag, "There is no statistics for tag %s.", tag);
            return stats;
        }

        @Override
        public String toString() {
            return toString(3);
        }

        public String toString(int precision) {
            double max = tagsStats.values().stream()
                    .mapToDouble(TagStatistics::getMax)
                    .filter(Double::isFinite)
                    .summaryStatistics().getMax();
            int maxDigits = Math.max(1, (int) Math.ceil(Math.log10(max)));
            return append(new StringBuilder(), DEFAULT_PERCENTILES, 10, maxDigits, precision, '|', '-').toString();
        }

        /**
         * Appends benchmarks report to given {@link Appendable}.
         *
         * @param appendable {@link Appendable} to append.
         * @param results Number of the smallest and the largest results to produce in report.
         * @param percentiles Sorted array of percentiles.
         * @param width Width of the double format.
         * @param precision Precision for the double format.
         * @param columnSeparator Column separator.
         * @param rowSeparator Row separator.
         * @return {@link Appendable} argument.
         */
        public Appendable append(Appendable appendable, int[] percentiles, int results, int width, int precision,
                char columnSeparator, char rowSeparator) {
            Preconditions.checkArgument(precision >= 0 && precision < 10, "Precision must be between 0 and 10 (excl).");
            Preconditions.checkArgument(width >= 1, "Width must be >= 1.");

            // Define row formatting options.
            int maxTagNameLength = tags.stream().mapToInt((tag) -> tag.toString().length()).max().getAsInt();

            RowFormattingOptions options = new RowFormattingOptions(tags, width, precision, maxTagNameLength,
                    columnSeparator, rowSeparator);

            // Calculate ranges.
            int maxSuccessful = tagsStats.values().stream().mapToInt(TagStatistics::getSuccessful).max().getAsInt();
            int firstRangeStart = 0, firstRangeEnd = Math.min(results, maxSuccessful);
            int lastRangeStart = maxSuccessful - results, lastRangeEnd = maxSuccessful, rowIndex;

            if (lastRangeStart < firstRangeEnd) {
                lastRangeStart = 0;
                lastRangeEnd = 0;
                firstRangeEnd = maxSuccessful;
            }

            // Format tag names.
            Map<Object, String> tagNames = asFormattedTags(options);

            StringBuilder headerBuilder = buildHeader(options, tagNames);
            String lineSeparator = StringUtils.repeat(rowSeparator, headerBuilder.length()) + options.newLine;

            try {
                appendable.append(options.newLine).append(lineSeparator)
                        .append(headerBuilder).append(options.newLine).append(lineSeparator);

                for (rowIndex = firstRangeStart; rowIndex < firstRangeEnd; rowIndex++) {
                    appendRow(rowIndex, options, appendable);
                }
                if (lastRangeEnd != 0 && lastRangeStart != firstRangeEnd) {
                    appendRow(-rowIndex, options, appendable);
                }
                for (rowIndex = lastRangeStart; rowIndex < lastRangeEnd; rowIndex++) {
                    appendRow(rowIndex, options, appendable);
                }
                appendable.append(lineSeparator);

                appendSummary("Min", TagStatistics::getMin, options, appendable);
                appendSummary("Max", TagStatistics::getMax, options, appendable);
                appendable.append(lineSeparator);

                appendSummary("Geom", TagStatistics::getGeometricMean, options, appendable);
                appendSummary("Mean", TagStatistics::getMean, options, appendable);
                appendSummary("Sum", TagStatistics::getSum, options, appendable);
                appendable.append(lineSeparator);

                for (int p : percentiles) {
                    double dp = (double) p;
                    appendSummary(String.format("%d%%", p), st -> st.getPercentile(dp), options, appendable);
                }
                if (percentiles != null && percentiles.length > 0) {
                    appendable.append(lineSeparator);
                }

                appendSummary("Total", TagStatistics::getTotal, options, appendable);
                appendSummary("Fail", TagStatistics::getFailed, options, appendable);
                appendSummary("Fail%", TagStatistics::getFailedPercent, options, appendable);
                appendable.append(lineSeparator);
                return appendable;
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }

        void appendSummary(String summaryName, Function<TagStatistics, Number> valueFunction,
                RowFormattingOptions formattingOptions, Appendable appendable) throws IOException {
            String column = StringUtils.leftPad(summaryName + " ", formattingOptions.maxColumnLength);
            appendable.append(formattingOptions.columnSeparator).append(column);
            for (Comparable<?> tag : formattingOptions.sortedTags) {
                appendable.append(formattingOptions.columnSeparator);
                column = formattingOptions.format(valueFunction.apply(tagsStats.get(tag)));
                column = StringUtils.leftPad(column, formattingOptions.maxColumnLength);
                appendable.append(column);
            }
            appendable.append(formattingOptions.columnSeparator).append(formattingOptions.newLine);
        }

        void appendRow(int rowIndex, RowFormattingOptions formattingOptions, Appendable appendable) throws IOException {
            String column = formattingOptions.emptyValue;
            appendable.append(formattingOptions.columnSeparator).append(column);
            for (Comparable<?> tag : formattingOptions.sortedTags) {
                appendable.append(formattingOptions.columnSeparator);
                TagStatistics stats = tagsStats.get(tag);
                if (rowIndex >= 0) {
                    if (rowIndex < stats.getSuccessful()) {
                        column = String.format(formattingOptions.doubleFormat, stats.getSuccessfulResult(rowIndex));
                        column = StringUtils.leftPad(column, formattingOptions.maxColumnLength);
                    }
                } else if (rowIndex < 0) {
                    if (Math.abs(rowIndex) < stats.getSuccessful()) {
                        column = formattingOptions.skipValue;
                    }
                }
                appendable.append(column);
                column = formattingOptions.emptyValue;
            }
            appendable.append(formattingOptions.columnSeparator).append(formattingOptions.newLine);
        }

        Map<Object, String> asFormattedTags(RowFormattingOptions formattingOptions) {
            Map<Object, String> tagNames = Maps.newHashMapWithExpectedSize(tags.size());
            for (Comparable<?> tag : tags) {
                String name = tag.toString() + " "; // 1 Space to the right.
                name = StringUtils.leftPad(name, formattingOptions.maxColumnLength);
                tagNames.put(tag, name);
            }
            return tagNames;
        }

        static StringBuilder buildHeader(RowFormattingOptions formattingOptions, Map<Object, String> tagNames) {
            StringBuilder headerBuilder = new StringBuilder();
            headerBuilder.append(formattingOptions.columnSeparator).append(formattingOptions.emptyValue);
            for (Comparable<?> tag : formattingOptions.sortedTags) {
                headerBuilder.append(formattingOptions.columnSeparator).append(tagNames.get(tag));
            }
            return headerBuilder.append(formattingOptions.columnSeparator);
        }
    }

    static class RowFormattingOptions {

        final int maxColumnLength;
        final TreeSet<Comparable<?>> sortedTags;
        final String doubleFormat;
        final String intFormat;
        final String emptyValue;
        final String skipValue;
        final char columnSeparator;
        final char rowSeparator;
        final String newLine;

        RowFormattingOptions(Set<Comparable<?>> tags, int width, int precision, int maxColumnLength,
                char columnSeparator, char rowSeparator) {
            this.doubleFormat = "%" + width + "." + precision + "f ";
            this.intFormat = "%d ";
            // 2 spaces before and after.
            this.maxColumnLength = Math.max(5, Math.max(width + 2, maxColumnLength + 2));
            this.skipValue = StringUtils.leftPad("... ", this.maxColumnLength);
            this.emptyValue = StringUtils.center("", this.maxColumnLength);
            this.sortedTags = Sets.newTreeSet(tags);
            this.columnSeparator = columnSeparator;
            this.rowSeparator = rowSeparator;
            this.newLine = System.lineSeparator();
        }

        String format(Number value) {
            if (value instanceof Integer || value instanceof Long) {
                return format(value.longValue());
            }
            return format(value.doubleValue());
        }

        String format(long value) {
            return String.format(intFormat, value);
        }

        String format(double value) {
            return String.format(doubleFormat, value);
        }
    }

    public static class TagStatistics {

        final Object tag;
        final double[] data;
        final DescriptiveStatistics statistics;
        final int failed;

        TagStatistics(Object tag, double[] data, int failed) {
            Arrays.sort(data);
            this.tag = tag;
            this.data = data;
            this.statistics = new DescriptiveStatistics(this.data);
            this.failed = failed;
        }

        public int getSuccessful() {
            return data.length;
        }

        public int getFailed() {
            return failed;
        }

        public int getTotal() {
            return data.length + failed;
        }

        public double getFailedPercent() {
            return 100.0D * failed / getTotal();
        }

        public double getMin() {
            return statistics.getMin();
        }

        public double getMax() {
            return statistics.getMax();
        }

        public double getSum() {
            return statistics.getSum();
        }

        public double getMean() {
            return statistics.getMean();
        }

        public double getGeometricMean() {
            return statistics.getGeometricMean();
        }

        public double getPercentile(double p) {
            return statistics.getPercentile(p);
        }

        public double getSuccessfulResult(int k) {
            return data[k];
        }

        public double[] getFastestResults(int n) {
            checkSize(n);
            return Arrays.copyOfRange(data, 0, n);
        }

        public double[] getSlowestResults(int n) {
            checkSize(n);
            return Arrays.copyOfRange(data, data.length - n, n);
        }

        void checkSize(int n) {
            if (n < 1 || n >= data.length) {
                throw new IllegalArgumentException("Number of results is expected to be > 0 and < " + data.length);
            }
        }
    }

    private static class TemporaryTagStatistics {

        static final double NANOSECONDS_IN_SECOND = 1e9;

        final Queue<Stopwatch> results = new ConcurrentLinkedQueue<>();
        final AtomicInteger exceptionsCounter = new AtomicInteger();
        final Object tag;

        TemporaryTagStatistics(Object tag) {
            this.tag = tag;
        }

        void add(Stopwatch watch) {
            results.add(watch);
        }

        public int exception() {
            return exceptionsCounter.incrementAndGet();
        }

        TagStatistics toTagStatistics() {
            double[] seconds = results.stream().mapToDouble(TemporaryTagStatistics::toDouble).toArray();
            return new TagStatistics(tag, seconds, exceptionsCounter.get());
        }

        static double toDouble(Stopwatch watch) {
            return watch.elapsed(TimeUnit.NANOSECONDS) / NANOSECONDS_IN_SECOND;
        }
    }
}
