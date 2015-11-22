package com.nobullet.interview;

import java.util.ArrayList;
import java.util.List;

/**
 * Given an array of stock prices by days, find the best day to buy and best day to sell the stock. Can't buy and sell
 * on the same day.
 */
public final class StockPrices {

    /**
     * Solves the stock price task in linear time.
     *
     * @param stockPrices Stock prices for days.
     * @return Deal information with index to buy, sell and profit.
     */
    public static Deal getBestDealLinear(int[] stockPrices) {
        if (stockPrices.length < 2) {
            throw new IllegalArgumentException("Too small data: size must be >= 2.");
        }

        int currentMinIndex = 0;
        int buyIndex = 0;
        int sellIndex = 0;
        int profitSoFar = 0;

        for (int i = 0; i < stockPrices.length; i++) {
            if (stockPrices[i] < stockPrices[currentMinIndex]) {
                currentMinIndex = i;
            }
            if (stockPrices[i] - stockPrices[currentMinIndex] > profitSoFar) {
                profitSoFar = stockPrices[i] - stockPrices[currentMinIndex];
                buyIndex = currentMinIndex;
                sellIndex = i;
            }
        }
        return new Deal(buyIndex, sellIndex, profitSoFar);
    }

    /**
     * Solves the stock price task in O(N^2) time.
     *
     * @param stockPrices Stock prices for days.
     * @return Deal information with index to buy, sell and profit.
     */
    public static Deal getBestDealBruteForce(int[] stockPrices) {
        if (stockPrices.length < 2) {
            throw new IllegalArgumentException("Too small data: size must be >= 2.");
        }
        int buyIndex = 0;
        int sellIndex = 0;
        int profitSoFar = 0;

        for (int i = 0; i < stockPrices.length; i++) {
            for (int j = i + 1; j < stockPrices.length; j++) {
                if (stockPrices[j] - stockPrices[i] > profitSoFar) {
                    sellIndex = j;
                    buyIndex = i;
                    profitSoFar = stockPrices[j] - stockPrices[i];
                }
            }
        }
        return new Deal(buyIndex, sellIndex, profitSoFar);
    }

    /**
     * Same problem but multiple subsequent buy/sell pairs allowed. Finds all pairs of local minimums and maximums,
     * remembering it as a part of solution.
     *
     * @param stockPrices Stock prices for days.
     * @return Deal information with index to buy, sell and profit.
     */
    public static List<Deal> getAllDeals(int[] stockPrices) {
        if (stockPrices.length < 2) {
            throw new IllegalArgumentException("Too small data: size must be >= 2.");
        }
        List<Deal> result = new ArrayList<>(stockPrices.length / 2 + 1);
        int localMinimumIndex = 0;
        int localMaximumIndex = -1;
        int i = 1;
        while (i < stockPrices.length) {
            // Find local minimum.
            while (i < stockPrices.length && stockPrices[i - 1] >= stockPrices[i]) {
                i++;
            }
            if (i - 1 > localMinimumIndex) {
                localMinimumIndex = i - 1;
            }
            // Find local maximum.
            while (i < stockPrices.length && stockPrices[i - 1] <= stockPrices[i]) {
                i++;
            }
            if (i - 1 > localMaximumIndex) {
                localMaximumIndex = i - 1;
                if (localMaximumIndex > localMinimumIndex) {
                    result.add(new Deal(
                            localMinimumIndex,
                            localMaximumIndex,
                            stockPrices[localMaximumIndex] - stockPrices[localMinimumIndex]));
                }
            }
        }
        return result;
    }

    /**
     * Deal information: buy index, sell index, profit.
     */
    public static class Deal {

        final int buyIndex;
        final int sellIndex;
        final int maxProfit;

        public Deal(int buyIndex, int sellIndex, int maxProfit) {
            this.buyIndex = buyIndex;
            this.sellIndex = sellIndex;
            this.maxProfit = maxProfit;
        }

        public int getBuyIndex() {
            return buyIndex;
        }

        public int getSellIndex() {
            return sellIndex;
        }

        public int getMaxProfit() {
            return maxProfit;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + this.buyIndex;
            hash = 41 * hash + this.sellIndex;
            hash = 41 * hash + this.maxProfit;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Deal other = (Deal) obj;
            return this.sellIndex == other.sellIndex && this.buyIndex == other.buyIndex
                    && this.maxProfit == other.maxProfit;
        }

        @Override
        public String toString() {
            return "{buy:" + buyIndex + ", sell:" + sellIndex + ", profit:" + maxProfit + '}';
        }

        /**
         * Creates list of deals from list of intergers.
         *
         * @param values Values.
         * @return List of deals.
         */
        public static List<Deal> fromIntegers(Integer... values) {
            int size = values.length / 3;
            List<Deal> result = new ArrayList<>(size);
            for (int i = 0; i < values.length; i += 3) {
                result.add(new Deal(values[i], values[i + 1], values[i + 2]));
            }
            return result;
        }
    }

    private StockPrices() {
    }
}
