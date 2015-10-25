package com.nobullet.list;

import static com.google.common.collect.Lists.newArrayList;
import static com.nobullet.MoreAssertions.assertListsEqual;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.Test;

/**
 * Tests for {@link Lists}.
 */
public class ListsTest {

    @Test
    public void testCompact() {
        List<IntegerGroup> groups = Lists.compact(newArrayList(4, 4, 4, 1, 1, 4, 4, 5, 5), IntegerGroup::new);
        assertListsEqual(toGroups(4, 3, 1, 2, 4, 2, 5, 2), groups);
        
        groups = Lists.compact(newArrayList(4, 4, 4, 1, 1, 4, 4, 5, 5, 5), IntegerGroup::new);
        assertListsEqual(toGroups(4, 3, 1, 2, 4, 2, 5, 3), groups);
        
        groups = Lists.compact(newArrayList(4, 4, 4, 1, 1, 4, 4, 5), IntegerGroup::new);
        assertListsEqual(toGroups(4, 3, 1, 2, 4, 2, 5, 1), groups);
        
        groups = Lists.compact(newArrayList(1, 2, 3, 4, 4, 5, 5, 5, 6, 7), IntegerGroup::new);
        assertListsEqual(toGroups(1, 1, 2, 1, 3, 1, 4, 2, 5, 3, 6, 1, 7, 1), groups);
    }

    private static List<IntegerGroup> toGroups(int... args) {
        List<IntegerGroup> ig = new ArrayList<>(args.length / 2);
        for (int i = 0; i < args.length; i += 2) {
            ig.add(new IntegerGroup(args[i], args[i + 1]));
        }
        return ig;
    }

    private static class IntegerGroup {

        final Integer value;
        final int quantity;

        public IntegerGroup(Integer value, int quantity) {
            this.value = value;
            this.quantity = quantity;
        }

        public Integer getValue() {
            return value;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return "{value:" + value + ", quantity:" + quantity + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + Objects.hashCode(this.value);
            hash = 79 * hash + this.quantity;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntegerGroup other = (IntegerGroup) obj;
            return Objects.equals(this.value, other.value) && this.quantity == other.quantity;
        }
    }
}
