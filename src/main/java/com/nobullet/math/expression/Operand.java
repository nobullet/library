package com.nobullet.math.expression;

import java.math.BigDecimal;

/**
 * Numerical operand of the expression. Can be treated as BigDecimal, double, long, etc.
 */
public class Operand implements ExpressionPart, Comparable<Operand> {

    final BigDecimal value;

    public Operand(double v) {
        this.value = BigDecimal.valueOf(v);
    }

    public Operand(long v) {
        this.value = BigDecimal.valueOf(v);
    }
    
    public Operand(String v) {
        this.value = new BigDecimal(v);
    }

    public Operand(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public long asLong() {
        return value.longValue();
    }

    public int asInt() {
        return value.intValue();
    }

    public double asDouble() {
        return value.doubleValue();
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Operand)) {
            return false;
        }
        return this.getValue().equals(((Operand) o).getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public int compareTo(Operand o) {
        return this.getValue().compareTo(o.getValue());
    }
}
