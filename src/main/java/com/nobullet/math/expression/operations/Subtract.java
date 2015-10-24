package com.nobullet.math.expression.operations;

import com.nobullet.math.expression.Operand;
import com.nobullet.math.expression.Operation;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 */
public class Subtract extends Operation {

    public Subtract() {
        super("-");
    }

    @Override
    public Operand apply(MathContext mc, Operand... o) {
        BigDecimal result = o[0].getValue();
        for (int i = 1; i < o.length; i++) {
            result = result.subtract(o[i].getValue(), mc);
        }
        return new Operand(result);
    }

    @Override
    public boolean hasArithmeticSign() {
        return true;
    }

}
