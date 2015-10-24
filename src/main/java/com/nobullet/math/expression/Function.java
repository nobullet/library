package com.nobullet.math.expression;

/**
 *
 */
public abstract class Function extends Operation {

    public Function(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName() + "(...)";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean hasArithmeticSign() {
        return false;
    }

}
