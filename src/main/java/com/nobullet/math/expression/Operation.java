package com.nobullet.math.expression;

import java.math.MathContext;

/**
 * Operation.
 */
public abstract class Operation implements ExpressionPart {

    final String name;

    /**
     * Constructs the operation with given name.
     *
     * @param name Name of operation.
     */
    public Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Priority of operand.
     *
     * @return Priority of operand.
     */
    public int getPriority() {
        return 0;
    }

    /**
     * Whether the operand is right associated.
     *
     * @return Whether the operand is right associated.
     */
    public boolean isRightAssociated() {
        return false;
    }

    /**
     * Whether the operation has arithmetic sign.
     *
     * @return Whether the operation has arithmetic sign.
     */
    public abstract boolean hasArithmeticSign();

    /**
     * Applies operation for the given operands.
     *
     * @param mc Math context.
     * @param o Operands.
     * @return Result of operation.
     * @throws IllegalArgumentException Whether the given arguments are illegal.
     */
    public abstract Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException;
}
