package calverter;// Maths Expressions Parser
// Copyright (C) <2016>  Danh Thanh Nguyen
//
// This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.

import java.lang.*;
import java.util.*;
/**
 * The {@code calverter.Operator} class represents mathematical operators that are provided. All
 * operators used in the program are instances of this class.
 *
 * <p>
 * The class provides singleton static instances of operators provided by the applications which
 * can be retrieved through {@link  Operator#get(int)} method with appropriate parameter.
 * </p>
 *
 * <p>
 * The fields of class consists of properties that uniquely define what the operator object does,
 * the string representation, the priority level, the number of operations required (or whether the
 * operator is binary or unary) and the direction of application. <br/>
 * These properties are used by numerous means i.e. by {@code calverter.Operation} objects to determine the
 * computation.
 * </p>
 *
 * @see Operation
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         <br> Date created: 19/03/2016
 */

  class Operator implements Expressible, Comparable<Operator>, Cloneable {

    // IDs used for public operator retrievals

    public static final int NEGATION = 0;

    public static final int BITWISE_NOT = 1;

    public static final int BITWISE_OR = 2;

    public static final int BITWISE_AND = 3;

    public static final int BITWISE_XOR = 4;

    public static final int ADDITION = 5;

    public static final int SUBTRACTION = 6;

    public static final int MULTIPLICATION = 7;

    public static final int DIVISION = 8;

    public static final int MODULO = 9;

    public static final int SHIFT_LEFT = 10;

    public static final int SHIFT_RIGHT = 11;


    public static final int BINARY_COUNT = 2;

    public static final int UNARY_COUNT = 1;

    // Where to look for candidate associated operands
    // i.e. with binary operator it would be LEFT_RIGHT, unary operator's would either be LEFT or RIGHT
    public enum AssociatingDirection  {LEFT, RIGHT, LEFT_RIGHT, NOT_AVAILABLE};

    /**
     * Priorities of supported operators.
     * Higher values implies higher priorities.
     * If two operators have the sample priority then the left hand side operator takes precedence.
     *
     * The priorities are based on C programming language. Look online for more details.
     */

    // bitwise or has the lowest priority. (binary)
    private static final int BITWISE_OR_PRIORITY = 1;

    // bitwise xor operator (binary)
    private static final int BITWISE_XOR_PRIORITY = 2;

    // bitwise and operator (binary)
    private static final int BITWISE_AND_PRIORITY = 3;

    // leftshit operator (binary)
    private static final int SHIFT_LEFT_PRIORITY = 4;

    // rigthshift operator (binary)
    private static final int SHIFT_RIGHT_PRIORITY = 4;

    // addition operator (binary)
    private static final int ADDITION_PRIORITY = 5;

    // subtraction operator (binary)
    private static final int SUBTRACTION_PRIORITY = 5;

    // multiplication operator (binary)
    private static final int MULTIPLICATION_PRIORITY = 6;

    // division operator (binary)
    private static final int DIVISION_PRIORITY = 6;

    // modulo operator (binary)
    private static final int MODULO_PRIORITY = 6;

    // bitwise not operator (unary)
    private static final int BITWISE_NOT_PRIORITY = 7;

    // negation operator (unary)
    private static final int NEGATION_PRIORITY = 7;

    // parentheses enclosed operator got promoted and are guaranteed to have a higher priority than the unenclosed operators.
    static final int JUMP_PRIORITY = 8;


    /**
     * Operators string representations.
     * An operator could have many string representations as long as they are unique amongst all other operators'
     *
     * //todo context aware collision resolve
     */
    private static final String[] ADDITION_REPS = {"+"};

    private static final String[] SUBTRACTION_REPS = {"-"};

    private static final String[] MULTIPLICATION_REPS = {"*"};

    private static final String[] DIVISION_REPS = {"/"};

    private static final String[] MODULO_REPS = {"%"};

    private static final String[] BITWISE_OR_REPS = {"OR"};

    private static final String[] BITWISE_AND_REPS = {"AND"};

    private static final String[] BITWISE_XOR_REPS = {"XOR"};

    private static final String[] SHIFT_LEFT_REPS = {"<<"};

    private static final String[] SHIFT_RIGHT_REPS = {">>"};

    private static final String[] BITWISE_NOT_REPS = {"NOT"};

    private static final String[] NEGATION_REPS = {"~"};

    /**
     * Constructor
     * @param name the name of the operator
     * @param reps the available string representations for this operator
     * @param priority the priority of the operator.
     * @param operandsCount the number of operands required
     * @param action the action performed by the operator.
     */
    private Operator(String name, Collection<String> reps, int priority, int operandsCount, AssociatingDirection direction, Action action) {
        this.name = name;
        this.reps = new ArrayList<>(reps);
        this.priority = priority;
        this.operandsCount = operandsCount;
        this.direction = direction;
        this.action = action;
    }

    /**
     * The name of the operator. The name should be succinct, comprehensive and not ambiguous.
     */
    private String name;

    /**
     * The string representations of the operator.
     * Any operator must have more than one string representation.
     * The string representations must be unique amongst all operators.
     */
    private ArrayList<String> reps;

    /**
     * The default priority of the operand in the context of an expression in which there's no priority changing components
     * such as parentheses.
     *
     * An operator with higher priority in a context of an expression will get executed before lower priority operators.
     *
     * The priority of an operator does not always have to be the default value.
     * i.e An operator enclosed in parentheses may have a higher priority than another identical operator not enclosed or
     * enclosed at a lower nesting level.
     */
    private int priority;

    /**
     * The number of operands required.
     * i.e. for binary operators, two operands are required. It's one for unary operators.
     */
    private int operandsCount;

    /**
     * The direction that the operator should look for operands
     */
    private AssociatingDirection direction;

    /**
     * The action that is to be applied to operands by this operator. An action is usually about, but not limited to
     * performing computations on operands and return a single result. (Such action is called discreteAction - actions that
     * return a single value)
     */
    private final Action action;

    /**
     * Apply operator on to operands and return a single value. The number of operands must match the {@code operandsCount}.
     * An {@code IllegalArgumentException} will be thrown otherwise.
     *
     * @param terms the operands onto which the operator is to be applied.
     *
     * @return the computed value.
     *
     * @throws IllegalArgumentException
     */
    public Evaluable actionDiscrete(Evaluable... terms) {
        return action.actionDiscrete(terms);
    }

    @Override
    public String toRep() {
        return name + "[" + priority + "]";
    }

    /**
     * @return the simplified form which in many case is the notation of the calverter.Expressible
     */
    @Override
    public String toSimplifiedRep() {
        return reps.get(0);
    }

    @Override
    public Category toCategory() {
        return Category.OPERATOR;
    }

    @Override
    public Evaluable toEvaluable() throws PBEXPException {
        throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "calverter.Operator objects cannot be cast to calverter.Evaluable.");
    }

    /**
     * Attempts to cast the calverter.Expressible to an calverter.Operator
     *
     * @return the cast calverter.Operator
     * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Operator
     */
    @Override
    public Operator toOperator() {
        return this;
    }

    /**
     * @return
     */
    @Override
    public boolean isOperand() {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public int compareTo(Operator o) {
        return priority - o.getPriority();
    }

    @Override
    public String toString() {
        return reps.get(0);
    }
    /**
     * The interface Action represents the action carried by the operators. An action should, but does not have to
     * return a result.
     *
     * The use of this interface could help in quickly and neatly setting and modifying an operator's behaviour.
     */
    interface Action {
        /**
         * Describe an action that returns nothing.
         *
         * @param terms input operands.
         */
        void actionVoid(Evaluable... terms);

        /**
         * Describe an action that returns a single result.
         *
         * @param terms input operands.
         *
         * @return the result returned by the action.
         */
        Evaluable actionDiscrete(Evaluable... terms);

        /**
         * Describe an action that returns multiple values.
         *
         * @param terms operands to be input.
         *
         * @return List of values returned.
         */
        List<Evaluable> actionNonDiscrete(Evaluable... terms);
    }

    static void throwMismatchOperandCount(String operatorName, int require, int actual) {
        throw new PBEXPException(PBEXPException.OPERANDS_COUNT_MISMATCH, "calverter.Operator " + operatorName + " requires" + require +" operands. "
                + actual + " given in action implementation.");

    }


    @Override
    public Expressible clone() {
        return new Operator(this.name, this.reps, this.priority, this.operandsCount, this.direction, this.action);
    }

    public ArrayList<String> getReps() {
        return reps;
    }

    private void setReps(ArrayList<String> reps) {
        this.reps = reps;
    }

    public int getPriority() {
        return priority;
    }

    void setPriority(int priority) {
        this.priority = priority;
    }

    public int getOperandsCount() {
        return operandsCount;
    }

    private void setOperandsCount(int operandsCount) {
        this.operandsCount = operandsCount;
    }

    public Action getAction() {
        return action;
    }

/*    private void setAction(Action action) {
        this.action = action;
    }*/

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public AssociatingDirection getDirection() {
        return direction;
    }

    private void setDirection(AssociatingDirection direction) {
        this.direction = direction;
    }

    /**
     * Public retrievals of operators
     * @param operator the operator index
     * @return a clone of the operator
     *
     */
    public static Operator get(int operator) {
        try {
            return (Operator) SUPPORTED_OPERATORS[operator].clone();
        } catch (IndexOutOfBoundsException e) {
            Debug.warn("calverter.Operator index %d is not valid.\n", operator);
            return null;
        }
    }

    boolean isUnary() {
        return getOperandsCount() == 1;
    }

    boolean isBinary() {
        return getOperandsCount() == 2;
    }

    static boolean outFloat(Evaluable... terms) {
        for (Evaluable n : terms) {
            Number o = (Number) n;
            if (NumberHelper.isFloat(o.toSimplifiedRep()))
                return true;
        }
        return false;
    }


    /*******************************************************************************************************************
     * OPERATOR objects
     *
     * Following are the objects that represent supported operators. All of these objects can be obtained indirectly from
     * outside through {@link Operator#get(int)}
     *
     * //todo is there a better way to collectively define these supported operators?
     ******************************************************************************************************************/

    /**
     * Negation operator
     */
    private static final Operator NEGATION_OPERATOR =
            new Operator("NEGATION", new HashSet<String>(Arrays.asList(NEGATION_REPS)),
                        NEGATION_PRIORITY, UNARY_COUNT, AssociatingDirection.RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != UNARY_COUNT)
                throwMismatchOperandCount("NEGATION", UNARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            return new Number(0 - terms[0].toInteger().longValue());
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Not operator
     */
    private static final Operator NOT_OPERATOR =
            new Operator("BITWISE NOT", new HashSet<String>(Arrays.asList(BITWISE_NOT_REPS)),
                        BITWISE_NOT_PRIORITY, UNARY_COUNT, AssociatingDirection.RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != UNARY_COUNT)
                throwMismatchOperandCount("BITWISE NOT", UNARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            return new Number(~terms[0].toInteger().longValue());
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Shift Left operator
     */
    private static final Operator SHIFT_LEFT_OPERATOR =
            new Operator("SHIFT LEFT", new HashSet<String>(Arrays.asList(SHIFT_LEFT_REPS)),
                        SHIFT_LEFT_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("SHIFT LEFT", BINARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            long iValue = terms[0].toInteger().longValue() << terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Shift Left operator
     */
    private static final Operator SHIFT_RIGHT_OPERATOR =
            new Operator("SHIFT RIGHT", new HashSet<String>(Arrays.asList(SHIFT_RIGHT_REPS)),
                        SHIFT_RIGHT_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("SHIFT RIGHT", BINARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            long iValue = terms[0].toInteger().longValue() >> terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Bitwise XOr operator
     */
    private static final Operator BITWISE_XOR_OPERATOR =
            new Operator("BITWISE XOR", new HashSet<String>(Arrays.asList(BITWISE_XOR_REPS)),
                        BITWISE_XOR_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("BITWISE XOR", BINARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            long iValue = terms[0].toInteger().longValue() ^ terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Bitwise And operator
     */
    private static final Operator BITWISE_AND_OPERATOR =
            new Operator("BITWISE AND", new HashSet<String>(Arrays.asList(BITWISE_AND_REPS)),
                        BITWISE_AND_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("BITWISE AND", BINARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            long iValue = terms[0].toInteger().longValue() & terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Bitwise Or operator
     */
    private static final Operator BITWISE_OR_OPERATOR =
            new Operator("BITWISE OR", new HashSet<String>(Arrays.asList(BITWISE_OR_REPS)),
                        BITWISE_OR_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("BITWISE OR", BINARY_COUNT, terms.length);

            // the numerical value (not the representation) matters
            long iValue = terms[0].toInteger().longValue() | terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * Modulo operator
     */
    private static final Operator MODULO_OPERATOR =
            new Operator("MODULO", new HashSet<String>(Arrays.asList(MODULO_REPS)),
                        MODULO_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("MODULO", BINARY_COUNT, terms.length);

            long iValue = terms[0].toInteger().longValue() % terms[1].toInteger().longValue();

            return new Number(iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * division operator
     *
     * the integer quotient is the integer part of the actual result
     */
    private static final Operator DIVISION_OPERATOR =
            new Operator("DIVISION", new HashSet<String>(Arrays.asList(DIVISION_REPS)),
                        DIVISION_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("DIVISION_OPERATOR", BINARY_COUNT, terms.length);

            long iValue = terms[0].toInteger().longValue() / terms[1].toInteger().longValue();
            double fValue = terms[0].toDecimal().doubleValue() / terms[1].toDecimal().doubleValue();

            return new Number(fValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * multiplication operator
     */
    private static final Operator MULTIPLICATION_OPERATOR =
            new Operator("MULTIPLICATION", new HashSet<String>(Arrays.asList(MULTIPLICATION_REPS)),
                        MULTIPLICATION_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("MULTIPLICATION_OPERATOR", BINARY_COUNT, terms.length);

            long iValue = terms[0].toInteger().longValue() * terms[1].toInteger().longValue();
            double fValue = terms[0].toDecimal().doubleValue() * terms[1].toDecimal().doubleValue();

            return new Number(outFloat(terms) ? fValue : iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * subtraction operator
     */
    private static final Operator SUBTRACTION_OPERATOR =
            new Operator("SUBTRACTION", new HashSet<String>(Arrays.asList(SUBTRACTION_REPS)),
                        SUBTRACTION_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("SUBTRACTION_OPERATOR", BINARY_COUNT, terms.length);

            long iValue = terms[0].toInteger().longValue() - terms[1].toInteger().longValue();
            double fValue = terms[0].toDecimal().doubleValue() - terms[1].toDecimal().doubleValue();

            return new Number(outFloat(terms) ? fValue : iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * addition operator
     */
    private static final Operator ADDITION_OPERATOR =
            new Operator("ADDITION", new HashSet<String>(Arrays.asList(ADDITION_REPS)),
                        ADDITION_PRIORITY, BINARY_COUNT, AssociatingDirection.LEFT_RIGHT, new Action() {
        @Override
        public void actionVoid(Evaluable... terms) {
            // do nothing.
        }

        @Override
        public Evaluable actionDiscrete(Evaluable... terms) {
            if (terms.length != BINARY_COUNT)
                throwMismatchOperandCount("ADDITION_OPERATOR", BINARY_COUNT, terms.length);

            long iValue = terms[0].toInteger().longValue() + terms[1].toInteger().longValue();
            double fValue = terms[0].toDecimal().doubleValue() + terms[1].toDecimal().doubleValue();

            return new Number(outFloat(terms) ? fValue : iValue);
        }

        @Override
        public List<Evaluable> actionNonDiscrete(Evaluable... terms) {
            // return the list wrapped result;
            return Collections.singletonList(actionDiscrete(terms));
        }
    });

    /**
     * calverter.Operator array
     */
    private static final Operator[] SUPPORTED_OPERATORS = {
            NEGATION_OPERATOR,
            NOT_OPERATOR,
            BITWISE_OR_OPERATOR,
            BITWISE_AND_OPERATOR,
            BITWISE_XOR_OPERATOR,
            ADDITION_OPERATOR,
            SUBTRACTION_OPERATOR,
            MULTIPLICATION_OPERATOR,
            DIVISION_OPERATOR,
            MODULO_OPERATOR,
            SHIFT_LEFT_OPERATOR,
            SHIFT_RIGHT_OPERATOR
    };
}