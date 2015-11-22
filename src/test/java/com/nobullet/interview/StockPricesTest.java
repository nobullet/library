package com.nobullet.interview;

import static com.nobullet.MoreAssertions.assertListsEqual;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link  StockPrices}.
 */
public class StockPricesTest {

    static final int[] data1 = new int[]{120, 118, 115, 113, 130, 119};
    static final int[] data2 = new int[]{120, 118, 115, 113, 130, 119, 125, 117, 110, 109, 115, 116};
    static final int[] data3 = new int[]{120, 118, 115, 113, 121, 119, 125, 117, 130, 109, 115, 129};
    static final int[] data4 = new int[]{7, 1, 3, 2, 9, 2, 5, 4, 2, 100};

    @Test
    public void testSingleLinearVsBruteForce() {
        assertEquals("Data1 is expected to be 17 of profit, indices: 3, 4.",
                StockPrices.getBestDealBruteForce(data1), StockPrices.getBestDealLinear(data1));
        assertEquals("Data2 is expected to be 17 of profit, indices: 3, 4.",
                StockPrices.getBestDealBruteForce(data2), StockPrices.getBestDealLinear(data2));
        assertEquals("Data3 is expected to be 20 of profit, indices: 3, 11.",
                StockPrices.getBestDealBruteForce(data3), StockPrices.getBestDealLinear(data3));
        assertEquals("Data3 is expected to be 20 of profit, indices: 3, 11.",
                StockPrices.getBestDealBruteForce(data4), StockPrices.getBestDealLinear(data4));
    }

    @Test
    public void testAllDeals() {
        assertListsEqual(StockPrices.Deal.fromIntegers(3, 4, 17, 5, 6, 6, 9, 11, 7),
                StockPrices.getAllDeals(data2));
        assertListsEqual(StockPrices.Deal.fromIntegers(1, 2, 2, 3, 4, 7, 5, 6, 3, 8, 9, 98),
                StockPrices.getAllDeals(data4));
    }
}
