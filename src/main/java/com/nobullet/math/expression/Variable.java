package com.nobullet.math.expression;

/**
 * Variable.
 */
public class Variable implements ExpressionPart {

    final String name;

    public Variable(String name) {
        this.name = name;
    }

    /**
     * Returns variable name.
     * @return Variable name,
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
