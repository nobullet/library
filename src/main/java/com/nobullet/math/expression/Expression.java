package com.nobullet.math.expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * Mathematical expression parser and evaluator. Supports standard functions (+,-,*,/) and variables.
 */
public class Expression {

    private static final Operand[] EMPTY_ARGS = new Operand[0];

    private final String expression;
    private final ReversePolishNotation notation;
    private final MathContext mc;
    private final boolean missingAsZero;

    /**
     * Parses and normalizes the given expression.
     *
     * @param expression Math expression as string.
     * @param mc Math context.
     * @param missingAsZero Whether to treat missing variables as zero or throw IllegalArgumentException.
     */
    public Expression(String expression, MathContext mc, boolean missingAsZero) {
        this.expression = expression;
        this.mc = mc;
        this.notation = new ReversePolishNotation.Parser(expression).toNotation().normalize(this.mc);
        this.missingAsZero = missingAsZero;
    }

    /**
     * Parses and normalizes the given expression.
     *
     * @param expression Math expression as string.
     * @param mc Math context.
     */
    public Expression(String expression, MathContext mc) {
        this.expression = expression;
        this.mc = mc;
        this.notation = new ReversePolishNotation.Parser(expression).toNotation().normalize(this.mc);
        this.missingAsZero = false;
    }

    /**
     * Parses and normalizes the given expression with default Math context DECIMAL64.
     *
     * @param expression Math expression as string.
     */
    public Expression(String expression) {
        this.expression = expression;
        this.mc = MathContext.DECIMAL64;
        this.notation = new ReversePolishNotation.Parser(expression).toNotation().normalize(this.mc);
        this.missingAsZero = false;
    }

    /**
     * Returns original expression.
     *
     * @return Original expression.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns current math context.
     *
     * @return Current math context.
     */
    public MathContext getContext() {
        return mc;
    }

    /**
     * Evaluates expression assuming that it has no variables.
     *
     * @return Result operand.
     */
    public Operand evaluate() {
        return evaluate(null);
    }

    /**
     * Evaluates current expression with given variable values as mapping: name -> value.
     *
     * @param variables Map of variable values.
     * @return Result operand.
     */
    public Operand evaluate(Map<String, BigDecimal> variables) {
        Deque<ExpressionPart> calculation = new ArrayDeque<>();
        for (ExpressionPart part : this.notation.getResult()) {
            if (part instanceof Variable) {
                String varName = ((Variable) part).getName();
                BigDecimal value = BigDecimal.ZERO;
                if (variables != null) {
                    value = variables.get(varName);
                }
                if (value == null) {
                    if (missingAsZero) {
                        value = BigDecimal.ZERO;
                    } else {
                        throw new IllegalArgumentException("Variable " + varName + " is not initialized.");
                    }
                }
                calculation.push(new Operand(value));
            } else if (part instanceof Function) {
                LinkedList<Operand> arguments = new LinkedList<>();
                ExpressionPart current;
                while (!((current = calculation.pop()) == EndOfFunctionArguments.INSTANCE)) {
                    arguments.addFirst((Operand) current);
                }
                Operand[] args = arguments.toArray(EMPTY_ARGS);
                calculation.push(((Operation) part).apply(mc, args));
            } else if (part instanceof Operation) {
                Operand o2 = (Operand) calculation.pop();
                Operand o1 = (Operand) calculation.pop();
                calculation.push(((Operation) part).apply(mc, o1, o2));
            } else {
                calculation.push(part);
            }
        }
        if (calculation.isEmpty()) {
            throw new IllegalStateException("Unable to evaluate expression. No operands in result.");
        }
        return (Operand) calculation.pop();
    }
}
