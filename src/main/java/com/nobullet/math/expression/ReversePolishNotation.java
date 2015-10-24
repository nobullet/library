package com.nobullet.math.expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Reverse polish notation.
 */
public class ReversePolishNotation {

    private final List<ExpressionPart> reverseNotation;

    public ReversePolishNotation() {
        reverseNotation = new LinkedList<>();
    }

    public void add(ExpressionPart part) {
        reverseNotation.add(part);
    }

    public List<ExpressionPart> getResult() {
        return reverseNotation;
    }

    @Override
    public String toString() {
        return reverseNotation.toString();
    }

    /**
     * Tries to remove constant expressions like (5 * 6 - x) => (30 - x)
     *
     * @param mc Math context.
     * @return Normalized reverse Polish notation.
     */
    public ReversePolishNotation normalize(MathContext mc) {
        Deque<ExpressionPart> optimized = new ArrayDeque<>(reverseNotation.size());
        for (ExpressionPart pt : reverseNotation) {
            optimized.push(pt);
            if (pt instanceof Operation && optimized.size() >= 3) {
                Operation top = (Operation) pt;
                if (top.hasArithmeticSign()) {
                    optimized.pop();
                    ExpressionPart beforeTop = optimized.pop();
                    ExpressionPart beforeBeforeTop = optimized.pop();
                    if (beforeTop instanceof Operand && beforeBeforeTop instanceof Operand) {
                        Operand newOperand = top.apply(mc, (Operand) beforeBeforeTop, (Operand) beforeTop);
                        optimized.push(newOperand);
                    } else {
                        optimized.push(beforeBeforeTop);
                        optimized.push(beforeTop);
                        optimized.push(top);
                    }
                }
            }
        }
        reverseNotation.clear();
        while (!optimized.isEmpty()) {
            reverseNotation.add(optimized.removeLast());
        }
        return this;
    }

    /**
     * Expression parser.
     */
    public static class Parser {

        private final String expression;
        private final StringBuilder numberBuilder;
        private final StringBuilder nameBuilder;
        private final ReversePolishNotation result;

        /**
         * Parses given expression.
         *
         * @param expression Expression as string.
         */
        public Parser(String expression) {
            this.expression = expression;
            this.numberBuilder = new StringBuilder();
            this.nameBuilder = new StringBuilder();
            this.result = new ReversePolishNotation();
        }

        private void processDot(char c) {
            if (numberBuilder.length() == 0) {
                numberBuilder.append('0');
            }
            numberBuilder.append(c);
        }

        private void processLeftBrace(Deque<ExpressionPart> tempStack) {
            if (nameBuilder.length() != 0) {
                if (processFunction(tempStack)) {
                    result.add(EndOfFunctionArguments.INSTANCE);
                }
            }
            tempStack.push(LeftBrace.INSTANCE);
        }

        private void processOperation(Deque<ExpressionPart> tempStack, Operation op) {
            processNumber();
            processVariable();
            ExpressionPart p;
            while (((p = tempStack.peek()) instanceof Operation)
                    && (!op.isRightAssociated() && op.getPriority() <= ((Operation) p).getPriority()
                    || op.isRightAssociated() && op.getPriority() < ((Operation) p).getPriority())) {
                result.add(tempStack.pop());
            }
            tempStack.push(op);
        }

        private void processRightBrace(Deque<ExpressionPart> tempStack) {
            processNumber();
            processVariable();
            ExpressionPart p;
            boolean foundMatching = false;
            while (!tempStack.isEmpty() && !((p = tempStack.pop()) == LeftBrace.INSTANCE && (foundMatching = true))) {
                result.add(p);
            }
            if (!foundMatching) {
                throw new IllegalStateException("The expression " + expression + " has unmatched braces.");
            }
            if (tempStack.peek() instanceof Function) {
                result.add(tempStack.pop());
            }
        }

        private void processComma(Deque<ExpressionPart> tempStack) {
            processNumber();
            processVariable();
            ExpressionPart p;
            boolean foundMatching = false;
            while (!tempStack.isEmpty() && !((p = tempStack.pop()) == LeftBrace.INSTANCE && (foundMatching = true))) {
                result.add(p);
            }
            if (!foundMatching) {
                throw new IllegalStateException("The expression " + expression + " has unmatched braces.");
            } else {
                tempStack.push(LeftBrace.INSTANCE);
            }
        }

        private boolean processVariable() {
            if (nameBuilder.length() == 0) {
                return false;
            }
            result.add(new Variable(nameBuilder.toString()));
            nameBuilder.delete(0, nameBuilder.length());
            return true;
        }

        private boolean processFunction(Deque<ExpressionPart> tempStack) {
            if (nameBuilder.length() == 0) {
                return false;
            }
            String funcName = nameBuilder.toString();
            Operation func = OperationFactory.forName(funcName);
            if (func != null) {
                tempStack.push(func);
            } else {
                throw new IllegalStateException("Can't find function " + funcName + ".");
            }
            nameBuilder.delete(0, nameBuilder.length());
            return true;
        }

        private boolean processNumber() {
            if (numberBuilder.length() == 0) {
                return false;
            }
            // ??? what's that?
            if (numberBuilder.charAt(numberBuilder.length() - 1) == '.') { // if number ends with '.' ...
                result.add(new Operand(BigDecimal.ZERO));
                numberBuilder.delete(0, numberBuilder.length());
                return true;
            }
            result.add(new Operand(BigDecimal.valueOf(Double.valueOf(numberBuilder.toString()))));
            numberBuilder.delete(0, numberBuilder.length());
            return true;
        }

        /**
         * Constructs reverse polish notation.
         *
         * @return Reverse polish notation for parsed expression.
         */
        public ReversePolishNotation toNotation() {
            Deque<ExpressionPart> tempStack = new ArrayDeque<>();
            int i = 0;
            int l = expression.length();
            boolean isName = false;
            while (i < l) {
                char c = expression.charAt(i);
                if (Character.isLetter(c) || c == '_') {
                    nameBuilder.append(c);
                    isName = true;
                } else if (Character.isDigit(c)) {
                    if (isName) {
                        nameBuilder.append(c);
                    } else {
                        numberBuilder.append(c);
                    }
                } else if (c == '(') {
                    isName = false;
                    processLeftBrace(tempStack);
                } else if (c == ')') {
                    isName = false;
                    processRightBrace(tempStack);
                } else if (c == '.') {
                    isName = false;
                    processDot(c);
                } else if (c == ',') {
                    isName = false;
                    processComma(tempStack);
                } else {
                    isName = false;
                    if (c == '-' && numberBuilder.length() == 0 && nameBuilder.length() == 0) {
                        // Unary minus.
                        result.add(new Operand(BigDecimal.ZERO));
                    }
                    Operation op = OperationFactory.forName("" + c);
                    if (op != null) {
                        processOperation(tempStack, op);
                    }
                }
                i++;
            }
            processNumber();
            processVariable();
            processFunction(tempStack);
            while (!tempStack.isEmpty()) {
                result.add(tempStack.pop());
            }
            if (result.reverseNotation.contains(LeftBrace.INSTANCE)) {
                throw new IllegalStateException("The expression " + expression + " has unmatched braces.");
            }
            return result;
        }
    }

}
