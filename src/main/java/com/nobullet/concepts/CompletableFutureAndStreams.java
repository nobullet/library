package com.nobullet.concepts;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream and producers example.
 */
public class CompletableFutureAndStreams {

    static final Long VALUE = 0xAEEAEEA0L;
    static final Logger logger = Logger.getLogger(CompletableFutureAndStreams.class.getName());

    public static void main(String... args) throws Exception {
        ExecutorService executor = ForkJoinPool.commonPool();

        Long result = CompletableFuture.supplyAsync(() -> {
            logger.info("Async is executed in: " + Thread.currentThread());
            return VALUE;
        }).thenApply(x -> {
            logger.info("Async is completed in: " + Thread.currentThread() + " : " + x);
            return x;
        }).get();
        if (result != VALUE) {
            throw new IllegalStateException("Expected " + VALUE);
        }

        List<String> urls = Arrays.asList("url1", "url2", "url3");

        Stream<CompletableFuture<String>> asyncStream = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> fetch(url), executor));
        Stream<CompletableFuture<Double>> relevanceFutures = asyncStream
                .map(completableFutureStage2 -> completableFutureStage2.thenApply(CompletableFutureAndStreams::parse))
                .map(completableFutureStage3
                        -> completableFutureStage3.thenCompose(CompletableFutureAndStreams::calculateRelevance));
        List<CompletableFuture<Double>> allFutures
                = relevanceFutures.collect(Collectors.<CompletableFuture<Double>>toList());

        CompletableFuture<List<Double>> f = asSingleFuture(allFutures);

        logger.info(f.join().toString());
    }

    /**
     * Takes a list of completable futures and returns a completable future of a list.
     *
     * @param <T>        Type in a list.
     * @param allFutures List of completable futures.
     * @return Completable future of a list.
     */
    public static <T> CompletableFuture<List<T>> asSingleFuture(List<CompletableFuture<T>> allFutures) {
        CompletableFuture<Void> allDone
                = CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[allFutures.size()]));
        CompletableFuture<List<T>> result = allDone.thenApply(v ->
                        allFutures.stream()
                                .map(f -> f.join())
                                .collect(Collectors.<T>toList())
        );
        return result;
    }

    public static DocumentTree parse(String content) {
        DocumentTree t = new DocumentTree();
        t.source = content;
        return t;
    }

    public static CompletableFuture<Double> calculateRelevance(DocumentTree tree) {
        CompletableFuture<Double> result = new CompletableFuture<>();
        result.complete(Math.random()); // Same as CompletableFuture.completedFuture(...)
        return result;
    }

    public static String fetch(String url) {
        for (int i = 0; i < 100; i++) {
            url += i + ",";
        }
        return url;
    }

    // Helper classes.
    public interface Param<T> {

        T get();
    }

    public interface Result<T> {

        T get();
    }

    public static class DocumentTree {

        public String source;
    }
}
