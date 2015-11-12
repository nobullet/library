package com.nobullet.interview;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link NumberAsLinkedList}.
 */
public class NumberAsLinkedListTest {

    NumberAsLinkedList numberll;

    @Test
    public void testCreate() {
        numberll = new NumberAsLinkedList(0L);
        assertEquals("0", numberll.toString());
        
        numberll = new NumberAsLinkedList(12L);
        assertEquals("12", numberll.toString());
        
        numberll = new NumberAsLinkedList(123456L);
        assertEquals("123456", numberll.toString());
    }
    
    @Test
    public void testReverse() {
        numberll = new NumberAsLinkedList(123456L);
        assertEquals("654321", numberll.reverse().toString());
        
        numberll = new NumberAsLinkedList(1L);
        assertEquals("1", numberll.reverse().toString());
        
        numberll = new NumberAsLinkedList(0L);
        assertEquals("0", numberll.reverse().toString());
    }
    
    @Test
    public void testSum() {
        numberll = new NumberAsLinkedList(111111L);
        assertEquals("111456", numberll.add(new NumberAsLinkedList(345L)).toString());
        
        numberll = new NumberAsLinkedList(111111L);
        assertEquals("333333", numberll.add(new NumberAsLinkedList(222222L)).toString());
        
        numberll = new NumberAsLinkedList(999L);
        assertEquals("1998", numberll.add(new NumberAsLinkedList(999L)).toString());
        
        numberll = new NumberAsLinkedList(9L);
        assertEquals("18", numberll.add(new NumberAsLinkedList(9L)).toString());
    }
}
