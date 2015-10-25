package com.nobullet.math.expression.operations;

import com.nobullet.math.expression.Operand;
import com.nobullet.math.expression.Operation;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Multiplication.
 */
public class Multiply extends Operation {

    public Multiply() {
        super("*");
    }

    @Override
    public Operand apply(MathContext mc, Operand... o) {
        BigDecimal result = o[0].getValue();
        for (int i = 1; i < o.length; i++) {
            result = result.multiply(o[i].getValue(), mc);
        }
        return new Operand(result);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean hasArithmeticSign() {
        return true;
    }

}
