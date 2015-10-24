package com.nobullet.math.expression;

/**
 *
 */
class EndOfFunctionArguments implements ExpressionPart {

    private EndOfFunctionArguments() {
    }

    @Override
    public String toString() {
        return "%endOfArgs%";
    }
    public static final EndOfFunctionArguments INSTANCE = new EndOfFunctionArguments();

}
