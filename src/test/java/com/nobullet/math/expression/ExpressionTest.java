package com.nobullet.math.expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Expression test.
 */
public class ExpressionTest {

    static final Logger logger = Logger.getLogger(ExpressionTest.class.getName());
    static final BigDecimal COMPARISON_DELTA = new BigDecimal("0.0000000001");

    @Test
    public void testMultiArgumentFunctions() {
        String ex = "-sin(min(sum(-1, 1, -5, -5, 10, 100, 2 * (-50)), -" + Math.PI + "/2))";
        Operand o = new Expression(ex).evaluate();
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.ONE);
    }

    @Test
    public void testUnaryMinus2() {
        String ex = "-(-5) + 6 - (-7)";
        Operand o = new Expression(ex).evaluate();
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(18.0D));
    }

    @Test
    public void testUnaryMinus() {
        String ex = "-28 - sum(-3,-1, -a * (-7 + 12), sum(sum(5, -5, -1, 1), max(-2, -1), max(0, -1, 1)))";

        Map<String, BigDecimal> parameters = new HashMap<>();
        parameters.put("a", BigDecimal.valueOf(2.0));
        Operand o = new Expression(ex).evaluate(parameters);
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(-14.00));
    }

    @Test
    public void testArithmetics() {
        Expression e = new Expression("max(0, 1, 10 * (sum(1, 2, 3, sum(4, 5)) + 100) / (60 - 10))");
        Operand o = e.evaluate();
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(23.0));
    }

    @Test
    public void testFunctions() {
        Expression e = new Expression("sum(1000, 10000, max(1,2,3, abs(z)) )");
        Operand o = e.evaluate();
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(11003.0));

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("z", BigDecimal.valueOf(-532.5d));
        o = e.evaluate(variables);
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(11532.5d));
    }

    @Test
    public void testPow() {
        Expression e = new Expression("(a + b)^z");

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("a", BigDecimal.valueOf(255L));
        variables.put("b", BigDecimal.valueOf(253L));
        variables.put("z", BigDecimal.valueOf(2L));
        Operand o = e.evaluate(variables);

        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(258064));
    }

    @Test
    public void testSqrt() {
        Expression e = new Expression("sqrt(27+(((((-1-1))))))");
        assertBigDecimalCloseTo(e.evaluate().getValue(), BigDecimal.valueOf(5));
    }

    @Test
    public void testSumMax() {
        String ex = "- sum( max(1,2,3,4,-6,7,-(-8), (2-(3))^(5-5)), min(-8, 8, 10, -sqrt(81), sin(0.011), cos(0.01)) )";
        Operand o = new Expression(ex).evaluate();
        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(1.0D));
    }

    @Test
    public void testNames() {
        Expression e = new Expression("(everything_is_fine_and_GOOD + b3 + a_2)^z_1");

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("everything_is_fine_and_GOOD", BigDecimal.valueOf(255L));
        variables.put("a_2", BigDecimal.valueOf(253L));
        variables.put("b3", BigDecimal.ZERO);
        variables.put("z_1", BigDecimal.valueOf(2L));
        Operand o = e.evaluate(variables);

        assertBigDecimalCloseTo(o.getValue(), BigDecimal.valueOf(258064));
    }

    @Test
    public void testMissingVariable() {
        Expression e = new Expression("(a + b)^z");

        Map<String, BigDecimal> variables = new HashMap<>();
        variables.put("z", BigDecimal.valueOf(3L));
        try {
            e.evaluate(variables);
            fail("Exception expected.");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("Variable a is not initialized.", iae.getMessage());
        }
    }

    @Test
    public void testParsingAndOptimization() {
        ReversePolishNotation st = new ReversePolishNotation.Parser("1 * (2 + 3)").toNotation();
        assertEquals("[1.0, 2.0, 3.0, +, *]", st.toString());
        st.normalize(MathContext.DECIMAL128);
        assertEquals("[5.00]", st.toString());

        st = new ReversePolishNotation.Parser("1 * (2 + 3)^6").toNotation();
        assertEquals("[1.0, 2.0, 3.0, +, 6.0, ^, *]", st.toString());
        st.normalize(MathContext.DECIMAL128);
        assertEquals("[15625.0000000]", st.toString());

        st = new ReversePolishNotation.Parser("a * (2 + 3)^b").toNotation();
        assertEquals("[a, 2.0, 3.0, +, b, ^, *]", st.toString());
        st.normalize(MathContext.DECIMAL128);
        assertEquals("[a, 5.0, b, ^, *]", st.toString());

        st = new ReversePolishNotation.Parser(" a * sum(x, 4.5, 5 + 5 , max(z,8,9)) ^b").toNotation();
        assertEquals("[a, %endOfArgs%, x, 4.5, 5.0, 5.0, +, %endOfArgs%, z, 8.0, 9.0, max(...), sum(...), b, ^, *]", 
                st.toString());
        st.normalize(MathContext.DECIMAL128);
        assertEquals("[a, %endOfArgs%, x, 4.5, 10.0, %endOfArgs%, z, 8.0, 9.0, max(...), sum(...), b, ^, *]", 
                st.toString());

        st = new ReversePolishNotation.Parser("0-5+6").toNotation();
        assertEquals("[0.0, 5.0, -, 6.0, +]", st.toString());
        st.normalize(MathContext.DECIMAL128);
        assertEquals("[1.0]", st.toString());

        st = new ReversePolishNotation.Parser("-1+5").toNotation().normalize(MathContext.DECIMAL128);
        assertEquals("[4.0]", st.toString());

        st = new ReversePolishNotation.Parser("(sum((1+1)*3, ,(99 + 1) * 3))")
                .toNotation().normalize(MathContext.DECIMAL128);
        assertEquals("[%endOfArgs%, 6.00, 300.00, sum(...)]", st.toString());
    }

    public static void assertBigDecimalCloseTo(BigDecimal b1, BigDecimal b2) {
        assertThat(b1, is(closeTo(b2, COMPARISON_DELTA)));
    }
}
