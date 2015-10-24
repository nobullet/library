package com.nobullet.math.expression.operations;

import com.nobullet.math.expression.Operand;
import com.nobullet.math.expression.Operation;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 */
public class Pow extends Operation {

    public Pow() {
        super("^");
    }

    @Override
    public Operand apply(MathContext mc, Operand... o) {
        BigDecimal result = o[0].getValue();
        for (int i = 1; i < o.length; i++) {
            result = result.pow(o[i].asInt(), mc);
        }
        return new Operand(result);
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isRightAssociated() {
        return true;
    }

    @Override
    public boolean hasArithmeticSign() {
        return true;
    }

}
