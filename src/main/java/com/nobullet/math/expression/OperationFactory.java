package com.nobullet.math.expression;

import com.nobullet.math.expression.operations.Divide;
import com.nobullet.math.expression.operations.Pow;
import com.nobullet.math.expression.operations.Subtract;
import com.nobullet.math.expression.operations.Multiply;
import com.nobullet.math.expression.operations.Sum;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Operations factory.
 */
public final class OperationFactory {

    private static final Map<String, Operation> operations = new ConcurrentHashMap<>();

    static {
        initialize();
    }

    private OperationFactory() {
    }

    /**
     * Returns operation for given name.
     *
     * @param name Name of the operations.
     * @return Operation for given name.
     */
    public static Operation forName(String name) {
        return operations.get(name);
    }

    /**
     * Registers operation.
     * @param o Operation to register.
     */
    public static void register(Operation o) {
        operations.put(o.getName(), o);
    }

    /**
     * Checks if the given operation exist.
     * @param name Name of the operation.
     * @return Whether the operation exists.
     */
    public static boolean hasOperation(String name) {
        return operations.containsKey(name);
    }

    private static void initialize() {
        OperationFactory.register(new Pow());
        OperationFactory.register(new Sum());
        OperationFactory.register(new Multiply());
        OperationFactory.register(new Subtract());
        OperationFactory.register(new Divide());
        OperationFactory.register(new Function("max") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("max() of empty value(s)?");
                }
                BigDecimal first = o[0].getValue();
                for (int i = 1; i < o.length; i++) {
                    if (first.compareTo(o[i].getValue()) < 0) {
                        first = o[i].getValue();
                    }
                }
                return new Operand(first);
            }
        });
        OperationFactory.register(new Function("abs") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("abs() of empty value(s)?");
                }
                return new Operand(o[0].getValue().abs(mc));
            }
        });
        OperationFactory.register(new Function("sqrt") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("sqrt() of empty value?");
                }
                return new Operand(new BigDecimal(Math.sqrt(o[0].getValue().doubleValue()), mc));
            }
        });
        OperationFactory.register(new Function("min") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("min() of empty value(s)?");
                }
                BigDecimal first = o[0].getValue();
                for (int i = 1; i < o.length; i++) {
                    if (first.compareTo(o[i].getValue()) > 0) {
                        first = o[i].getValue();
                    }
                }
                return new Operand(first);
            }
        });
        OperationFactory.register(new Function("sum") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("sum() of empty value(s)?");
                }
                BigDecimal sum = BigDecimal.ZERO;
                for (int i = 0; i < o.length; i++) {
                    sum = sum.add(o[i].getValue(), mc);
                }
                return new Operand(sum);
            }
        });
        OperationFactory.register(new Function("cos") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("cos() of empty value(s)?");
                }

                return new Operand(Math.cos(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("sin") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("sin() of empty value(s)?");
                }

                return new Operand(Math.sin(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("asin") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("asin() of empty value(s)?");
                }

                return new Operand(Math.asin(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("acos") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("acos() of empty value(s)?");
                }
                return new Operand(Math.acos(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("log") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("acos() of empty value(s)?");
                }
                return new Operand(Math.log(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("log10") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("logt() of empty value(s)?");
                }
                return new Operand(Math.log10(o[0].getValue().doubleValue()));
            }
        });
        OperationFactory.register(new Function("log1p") {

            @Override
            public Operand apply(MathContext mc, Operand... o) throws IllegalArgumentException {
                if (o == null || o.length == 0 || o[0] == null || o[0].getValue() == null) {
                    throw new IllegalArgumentException("logp() of empty value(s)?");
                }
                return new Operand(Math.log1p(o[0].getValue().doubleValue()));
            }
        });
    }
}
