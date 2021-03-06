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


import java.util.*;

/**
 * WARN! calverter.Operator objects cannot be reused. It is recommended that {@link Operator#get(int)} method is used to get a new
 * instance of some operator. calverter.Operator reuses might cause erroneous behaviours.
 *
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 21/03/2016
 */
public class Expression {

    private LinkedList<Expressible> internals_original;

    private LinkedList<Expressible> internals_mutated;

    private ListIterator<Expressible> iterator;

    private String rep;

    private boolean resolved = false;

    private Evaluable tempResult;

    private String humanReadable;

    // an expression can only be computed if the innerLevel is 0
    private int innerLevel;


    // card float points
    private int floatLevel;

    private boolean suppress;

    /**
     * todo doc
     * @param rep
     */
    public Expression(String rep, boolean suppress) {
        this.rep = rep;
        internals_original = new LinkedList<>();
        internals_mutated = new LinkedList<>(internals_original);
        iterator = internals_mutated.listIterator();
        resolved = isResolved_internal();
        innerLevel = 0;
        floatLevel = 0;
        humanReadable = "";
        this.suppress = suppress;
    }

    private static String appendExpressibleHumanReadable(Expressible e, String r) {
        switch (e.toCategory()) {
            case PARENTHESIS_CLOSE:
            case PARENTHESIS_OPEN:
            case FLOAT: {
                if (r.endsWith(" "))
                    r = r.trim();
                r += e.toSimplifiedRep();
                break;
            }
            case CLEAR_ALL: {
                r = "";
                break;
            }
            case CLEAR: {
                int index = r.length() - 1;
                while (Character.isSpaceChar(r.charAt(index))) index--;
                r = r.substring(0, index);
                break;
            }
            default: {
                r += e.toSimplifiedRep() + " ";
                break;
            }
        }
    return r;
    }

    /**
     * Return a human-readble expression as per convention
     * @param expressibles
     * @return
     */
    static String humanRead(LinkedList<Expressible> expressibles) {
        String r = "";
        for (Expressible e : expressibles) {
            r = appendExpressibleHumanReadable(e, r);
        }
            return r;
    }

    public String getHumanReadable() {
        return humanReadable;
    }

    /**
     * todo doc
     * @param e
     * @return
     */
    public static Expression makeCopy(Expression e) {
        Expression t = new Expression(e.rep, e.suppress);
        t.humanReadable = e.humanReadable;
        for (Expressible expressible : e.internals_original)
            t.internals_original.add(expressible.clone());
        t.internals_mutated = new LinkedList<>(t.internals_original);
        return t;
    }

    private boolean inFloatMode(){
        return floatLevel > 0;
    }

    private Expressible last() {
        return internals_mutated.getLast();
    }

    private int canCloseP() {
        return innerLevel <= 0 ?
                PBEXPException.PARENTHESES_NOT_OPENED_BEFORE_CLOSED : last().toCategory() == Expressible.Category.OPERATOR
                         ? PBEXPException.PARENTHESES_EXPRESSION_INCOMPLETE : 0; // todo
    }


    LinkedList<Expressible> getInternals_mutated() {
        return internals_mutated;
    }

    LinkedList<Expressible> getInternals_original() {
        return internals_original;
    }

    public Expressible getLastComponent() {
        return internals_original.getLast();
    }

    public void removeLastComponent() {
        invalidatePrevComputation();
        internals_original.removeLast();
    }

    public LinkedList<Expressible> getComponentList() {
        return internals_original;
    }

    private void invalidatePrevComputation() {
        internals_mutated = new LinkedList<>(internals_original);
    }

    public boolean isEmpty() {
        return internals_original.size() == 0;
    }
    public static class Clearance implements Expressible {
        private Category type;

        private String rep;

        private Clearance(Category type, String rep) {
            this.rep = rep;
            this.type = type;
        }

        private static final String CLEAR = "x";

        private static final String CLEAR_ALL = "#";

        public static Clearance getClear() {
            return new Clearance(Category.CLEAR, CLEAR);
        }

        public static Clearance getClearAll() {
            return new Clearance(Category.CLEAR_ALL, CLEAR_ALL);
        }


        /**
         * @return the string representation of the calverter.Expressible
         */
        @Override
        public String toRep() {
            return rep;
        }

        /**
         * @return the simplified form which in many case is the notation of the calverter.Expressible
         */
        @Override
        public String toSimplifiedRep() {
            return toRep();
        }

        /**
         * @return the category of the calverter.Expressible.
         */
        @Override
        public Category toCategory() {
            return type;
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Evaluable
         *
         * @return the cast calverter.Evaluable
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Evaluable
         */
        @Override
        public Evaluable toEvaluable() throws PBEXPException {
            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Clearance class does not implement calverter.Evaluable.");
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Operator
         *
         * @return the cast calverter.Operator
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Operator
         */
        @Override
        public Operator toOperator() {

            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Clearance objects cannot be cast to calverter.Operator class.");
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
            return false;
        }

        @Override
        public Expressible clone() {
            if (type == Category.CLEAR)
                return getClear();
            return getClearAll();
        }
    }

    public static class FloatPoint implements Expressible {
        private final String rep = ".";

        private final Category type = Category.FLOAT;

        private FloatPoint() {}

        /**
         * @return the string representation of the calverter.Expressible
         */
        @Override
        public String toRep() {
            return ".";
        }

        /**
         * @return the simplified form which in many case is the notation of the calverter.Expressible
         */
        @Override
        public String toSimplifiedRep() {
            return ".";
        }

        /**
         * @return the category of the calverter.Expressible.
         */
        @Override
        public Category toCategory() {
            return Category.FLOAT;
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Evaluable
         *
         * @return the cast calverter.Evaluable
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Evaluable
         */
        @Override
        public Evaluable toEvaluable() throws PBEXPException {
            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Float point cannot be cast to Evaluable");
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Operator
         *
         * @return the cast calverter.Operator
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Operator
         */
        @Override
        public Operator toOperator() {
            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Float point cannot be cast to Evaluable");
        }

        /**
         * @return
         */
        @Override
        public boolean isOperand() {
            return true;
        }

        /**
         * @return
         */
        @Override
        public boolean isOperator() {
            return false;
        }

        @Override
        public Expressible clone() {
            return new FloatPoint();
        }

        public static Expressible get() {
            return new FloatPoint();
        }
    }

    public static class Parenthesis implements Expressible {

        private Category type;

        private String rep;

        private Parenthesis(Category type, String rep) {
            this.rep = rep;
            this.type = type;
        }

        private static final String OPENING_REP = "(";

        private static final String CLOSING_REP = ")";

        public static Parenthesis getOpening() {
            return new Parenthesis(Category.PARENTHESIS_OPEN, OPENING_REP);
        }

        public static Parenthesis getClosing() {
            return new Parenthesis(Category.PARENTHESIS_CLOSE, CLOSING_REP);
        }


        /**
         * @return the string representation of the calverter.Expressible
         */
        @Override
        public String toRep() {
            return rep;
        }

        /**
         * @return the simplified form which in many case is the notation of the calverter.Expressible
         */
        @Override
        public String toSimplifiedRep() {
            return toRep();
        }

        /**
         * @return the category of the calverter.Expressible.
         */
        @Override
        public Category toCategory() {
            return type;
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Evaluable
         *
         * @return the cast calverter.Evaluable
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Evaluable
         */
        @Override
        public Evaluable toEvaluable() throws PBEXPException {
            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Parenthesis class does not implement calverter.Evaluable.");
        }

        /**
         * Attempts to cast the calverter.Expressible to an calverter.Operator
         *
         * @return the cast calverter.Operator
         * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Operator
         */
        @Override
        public Operator toOperator() {

            throw new PBEXPException(PBEXPException.CLASS_CAST_EXCEPTION, "Parenthesis objects cannot be cast to calverter.Operator class.");
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
            return false;
        }

        @Override
        public Expressible clone() {
            if (type == Category.PARENTHESIS_CLOSE)
                return getClosing();
            return getOpening();
        }
    }

    private boolean isResolved_internal() {
        Debug.gib1("Called isResolvedInternal? : operand: %d, operator: %d, inner level: %s\n", getExpressibleCount(Expressible.Category.OPERAND),
                getExpressibleCount(Expressible.Category.OPERATOR), innerLevel);
        return  getExpressibleCount(Expressible.Category.OPERAND) == 1
                && getExpressibleCount(Expressible.Category.OPERATOR) == 0
                && innerLevel == 0;
    }

    private int getExpressibleCount(Expressible.Category category) {
        int count = 0;
        for (Expressible e : internals_mutated)
            if (e.toCategory() == category)
                count++;
        return count;
    }

    public boolean isResolved() {
        return isResolved_internal();
    }

    private void locateExpressible(Expressible e) {
        iterator = internals_mutated.listIterator(internals_mutated.indexOf(e));
    }

    /**
     *
     * @param o the operation that is to be replaced by the computed result in the expression
     * @return the index of the newly evaluated result.
     */
    private int replaceOperationByResult(Operation o) {
        if (!o.isOperationCompleted()) {
            throw new UnsupportedOperationException("calverter.Operation " + o + "is not completed. Hence the substitution can't proceed.");
        }
        ArrayList<Integer> indices = new ArrayList<>();

        // get indices of involved calverter.Expressible including the operator and all operands.
        //todo javac1.7
//        indices.add(internals_mutated.indexOf(o.getOperator()));
//            indices.addAll(o.getOperands().stream().map(e -> internals_mutated.indexOf(e)).collect(Collectors.toList()));

        indices.add(internals_mutated.indexOf(o.getOperator()));
        for (Evaluable e: o.getOperands()) {
            indices.add(internals_mutated.indexOf(e));
        }

    //    Debug.gib1("Indices of calverter.Expressible obs are: %s\n", Arrays.toString(indices.toArray()));
        Collections.sort(indices);
        Collections.reverse(indices);

    //    Debug.gib1("Indices of calverter.Expressible obs sorted are: %s\n", Arrays.toString(indices.toArray()));
        // pivot
        int i = indices.get(indices.size() - 1);
        // add the result...
        internals_mutated.add(i, (Expressible) ((Number) o.getResult()));
        // we need to remove indices in descending order to prevent dynamic indices reallocation
        for (Integer k: indices)
            internals_mutated.remove(k + 1);
      //  Debug.gib1("After operation is replaced: %s\n",toString());
        return i;
    }

    private static String toMode(boolean isFloatMode, Evaluable e) {
        return isFloatMode ? e.toDecimal().toString() : e.toInteger().toString();
    }

    public boolean isSuppress() {
        return suppress;
    }

    public void setSuppress(boolean suppress) {
        this.suppress = suppress;
    }

    public int getFloatLevel() {
        return floatLevel;
    }

    public void setFloatLevel(int floatLevel) {
        this.floatLevel = floatLevel;
    }

    public int getInnerLevel() {
        return innerLevel;
    }

    public void setInnerLevel(int innerLevel) {
        this.innerLevel = innerLevel;
    }

    public String compute() {
       return compute(suppress);
    }

    /**
     * Computes an valid expression
     * @param suppress Suppress errors caused by "incomplete" expression rather than fatal errors.
     * @return the result
     *
     * @throws ClassCastException
     * @throws IllegalStateException
     */
     public String compute(boolean suppress) throws PBEXPException {
        Debug.gib2("INPUT EXPRESSION: %s\n", toString());

        if(getExpressibleCount(Expressible.Category.OPERATOR) == 0 && getExpressibleCount(Expressible.Category.OPERAND) == 0) {
            if (suppress)
                return "0";
            throw new PBEXPException(PBEXPException.EXPRESSION_EMPTY, toString_internals(internals_original, false));
        }
        if (isResolved())
            return toMode(inFloatMode(), getOperands(internals_mutated).get(0));

         // order of suppressing operations matter!

         // simplify on suppress demand: remove the last pending operator if needs be
         if (suppress) {
             if (getLastComponent().isOperator()) {
                 Operator r = getLastComponent().toOperator();
                 if (r.isBinary() || (r.isUnary() || r.getDirection() == Operator.AssociatingDirection.RIGHT)) {
                     Debug.gib1("Suppress demand: Removing %s", r.toString());
                     internals_mutated.removeLast();
                 }
             }
             Debug.debug("POST SUPPRESS LAST REMOVAL: %s", toExpressionString(internals_mutated, true));

         }

        if (innerLevel > 0) {
            // suppress then we do this
            if (suppress) {
                for (int i = innerLevel; i > 0; i--) {
                    Debug.gib1("Suppress demand: Adding closing parenthesis to reduce innerLevel to %s", innerLevel);
                    addExpressible(Parenthesis.getClosing());
                }
            }
            else {
                throw new PBEXPException(PBEXPException.PARENTHESES_NOT_CLOSED);
            }
            }
        else if (innerLevel < 0) {
            throw new PBEXPException(PBEXPException.PARENTHESES_NOT_OPENED_BEFORE_CLOSED);
        }



         // from this point onwards, if it's an error it means the expression is syntactically wrong... todo ?

        // makes mapping of operators
        HashMap<Operator, Operation> opsMap = new HashMap<>();
        for (Expressible expressible : internals_mutated) {
            if (expressible.toCategory() == Expressible.Category.OPERATOR) {
                opsMap.put((Operator) expressible, new Operation.OperationBuilder().setOperator((Operator) expressible).build());
            }
        }

        Expressible current = internals_mutated.getFirst();
        List<Expressible> neighbours = new ArrayList<>();
        //ListIterator<Expressible> iterator = internals_mutated.listIterator(0);

        while (!isResolved()) {
            // warn: identity problems
            int index = internals_mutated.indexOf(current);
          //  Debug.gib1("Next index at top: %s\n", index);
            Debug.gib2("**********Current calverter.Expressible: %s[%d]\n", current.toRep(), internals_mutated.indexOf(current));
            switch (current.toCategory()) {
                case PARENTHESIS_OPEN: case PARENTHESIS_CLOSE: {
                    // advance and wrap around (this is part of the algorithm). calverter.Number of repeating rounds not known at the start
                    current = internals_mutated.get(++index % internals_mutated.size());
                    continue;
                }
                case OPERAND: {
                    simplify(index);


                    neighbours = neighbours(internals_mutated.indexOf(current));
                    for (int i = 0; i < neighbours.size(); i++) {
                        Expressible expressible = neighbours.get(i);
                        // get index of the neighbour (operator)
                        int nIndex = internals_mutated.indexOf(expressible);
                        if (expressible.toCategory() == Expressible.Category.OPERAND)
                            throw new PBEXPException(PBEXPException.OPERANDS_NEIGHBOURING, "Operand " + current+"["+index+"]");
                        // if the operand cannot be associated with this operator, then we discard it from the promising candidate list
                        //todo caching this could help in performance
                        else if (!canAssociate(expressible.toOperator(),
                                nIndex, index))
                        {
                            Debug.gib1("calverter.Operator %s{%d}[%d] cannot be associate-able with Operand %s[%d]. Removing from promising list\n",
                                    expressible.toOperator().toRep(), expressible.toOperator().getPriority(), nIndex,
                                    current.toEvaluable().toDecimal(), index);
                            neighbours.remove(expressible);
                        }
                    }

                    Operator winner = null;

                    try {
                        switch (neighbours.size()) {
                            case 0:
                                continue;
                            case 1: {
                                winner = neighbours.get(0).toOperator();
                                break;
                            }
                            case 2: {
                                Operator left = neighbours.get(0).toOperator();
                                Operator right = neighbours.get(1).toOperator();
                                int c = left.compareTo(right);
                                winner = c > 0 ? left : c < 0 ? right : left; // left takes precedence on equality
                                break;
                            }
                            default:
                                throw new IllegalArgumentException("Something wrong with calverter.Expression at " + internals_mutated.indexOf(current) + ": " + neighbours.size() + " neighbours");
                        }
                    }   catch (ClassCastException exception) {
                            exception.printStackTrace();
                            Debug.error(true, "Some neighbor of Operand %s[%d] is not calverter.Operator. Check again.\n", current.toRep(), internals_mutated.indexOf(current));
                    }
                    Debug.gib1("Winner at Operand %s[%d] is: calverter.Operator %s[%d]\n", current.toRep(), internals_mutated.indexOf(current), winner.toRep(), internals_mutated.indexOf(winner));

                    Operation operationWinner = opsMap.get(winner);
                    if (operationWinner.checkOperandAssociability(current.toEvaluable())) {
                        operationWinner.associateOperand(current.toEvaluable());
                    }
                    else {
                        Debug.warn("Operand %s[%d] cannot be associated to calverter.Operation %s. Ignored.\n",
                                current.toEvaluable(), internals_mutated.indexOf(current), operationWinner);
                    }

                    if (operationWinner.isOperationComputable()) {
                        Debug.gib1("calverter.Operation of %s[%d] is ready to be computed. %s\n", winner.toRep(), internals_mutated.indexOf(winner), operationWinner.toString());
                        Evaluable r = operationWinner.compute();
                        Debug.gib2("calverter.Operation of %s[%d] is computed. R is: %s\n", winner.toRep(), internals_mutated.indexOf(winner), r.toDecimal());
                        replaceOperationByResult(operationWinner);
                        Debug.gib2("NEW EXPRESSION after operation is replaced: %s\n", toString());
                    }

                    // advance to the next calverter.Expressible or cycle back
                    current = internals_mutated.get(++index % internals_mutated.size());
                    continue;
                }
                case OPERATOR: {
                    Operator o = current.toOperator();
                    if (o.getOperandsCount() == Operator.UNARY_COUNT)
                        neighbours = Collections.singletonList(
                                        canAssociate(o, index, index + 1) ?
                                            internals_mutated.get(index + 1) : internals_mutated.get(index - 1));

                    if (current.toOperator().getOperandsCount() == Operator.BINARY_COUNT) {
                        neighbours = neighbours(index);
                        for (Expressible operator : neighbours) {
                            if (operator.toCategory() == Expressible.Category.OPERATOR && operator.toOperator().isBinary())
                                throw new PBEXPException(PBEXPException.BINARY_OPERATORS_NEIGHBOURING, "calverter.Operator " + current + "[" + index + "]");
                        // neighbours to binary operator could be operands or unary operators
                        }
                    }

                    if (neighbours.size() != current.toOperator().getOperandsCount())
                        throw new PBEXPException(PBEXPException.INSUFFICIENT_NEIGHBOR_FOR_OPERATOR,
                                "calverter.Operator " + current + "[" +index + "]");


                    Operation operationWinner = opsMap.get((Operator) current);

                    if (operationWinner.isOperationComputable()) {
                        Evaluable r = operationWinner.compute();
                        Debug.gib1("calverter.Operation of %s[%d] is computed. R is: %s\n", current.toRep(), internals_mutated.indexOf(current), r.toDecimal());
                        replaceOperationByResult(operationWinner);
                        Debug.gib1("NEW EXPRESSION: %s", toString());
                    }
                    // advance to the next calverter.Expressible or cycle back
                    current = internals_mutated.get(++index % internals_mutated.size());
                    Debug.gib1("next index: %s\n", index);
                    break;
                }
                default: {
                    throw new PBEXPException(PBEXPException.ELEMENT_NOT_RECOGNISABLE, " Index: " + index);
                }
            }
        }
        Expressible candidate = (Expressible) getOperands(internals_mutated).get(0);
        simplify(internals_mutated.indexOf(candidate));
        Debug.gib2("FINISHED: %s: \n", toShortString());
        if (isResolved())
            return toMode(inFloatMode(), candidate.toEvaluable()) ;
        else
            throw new IllegalStateException("Something went wrong after the computation: " + toString_internals(internals_mutated, false));
    }

    private static List<Evaluable> getOperands(LinkedList<Expressible> e) {
//        return e.stream().filter(t -> t.toCategory() == Expressible.Category.OPERAND)
//                .map(Expressible::toEvaluable).collect(Collectors.toList());
        //todo javac1.7
        List<Evaluable> l = new LinkedList<>();
        for (Expressible o : e) {
           if (o.toCategory() == Expressible.Category.OPERAND)
               l.add(o.toEvaluable());
        }
        return l;
    }

    private Evaluable getTempResult() {
        if (tempResult != null)
            return tempResult;
        return new Evaluable() {
            @Override
            public Double toDecimal() {
                return 0d;
            }

            @Override
            public Long toInteger() {
                return Long.valueOf(0);
            }
        };
    }

    private void simplify(int operationIndex) {
        Expressible.Category category = internals_mutated.get(operationIndex).toCategory();
        if (category != Expressible.Category.OPERAND) {
            Debug.warn("Only Operand objects can be simplified. %s given\n", category);
            return;
        }
        Debug.gib1("Attempting to simplify for operand index: %d\n", operationIndex);
        if (isOutOfBound(operationIndex)) {
            Debug.warn("calverter.Operation Index %s is out of bound: %d\n", operationIndex, internals_mutated.size());
            return;
        }
        int open = operationIndex-1;
        int close = operationIndex+1;
        while (!isOutOfBound(open) && !isOutOfBound(close) && internals_mutated.get(open).toCategory() == Expressible.Category.PARENTHESIS_OPEN
                && internals_mutated.get(close).toCategory() == Expressible.Category.PARENTHESIS_CLOSE) {

            // order of removals matters due to dynamic resize
            internals_mutated.remove(close);
            internals_mutated.remove(open);
            open-=3;
            close-=1;
        }
        Debug.gib2("After simplification: %s\n", toString_internals(internals_mutated, true));
    }

    private boolean isOutOfBound(int operationIndex) {
        return operationIndex < 0 || operationIndex >= internals_mutated.size();
    }

    /**
     *
     * @param index the index from which the neighbor operand or operator is to be located
     * @param forward true if search forward, false if search backward
     *
     * @return the index of the next operand or operator, or -1 if none is found.
     *
     * @throws IllegalArgumentException if the index is negative.
     */
    private int neighborOperandOrOperatorIndex(int index, boolean forward) {
        if (index < 0)
            throw new IllegalArgumentException("Index cannot be negative." + index +" given");
        int i = index + (forward ? 1 : -1);
        while (i < internals_mutated.size() && i >= 0 &&
                (internals_mutated.get(i).toCategory() != Expressible.Category.OPERAND
                        && internals_mutated.get(i).toCategory() != Expressible.Category.OPERATOR))
            i += forward ? 1 : -1;
        return i == internals_mutated.size() || i < 0 ? -1 : i;
    }

    private int nextOperandOrOperatorIndex(int index) {
        return neighborOperandOrOperatorIndex(index, true);
    }

    private int prevOperandOrOperatorIndex(int index) {
        return neighborOperandOrOperatorIndex(index, false);
    }

    private boolean canAssociate(Operator o, int operatorIndex, int operandIndex) {
        int next = nextOperandOrOperatorIndex(operatorIndex);
        int prev = prevOperandOrOperatorIndex(operatorIndex);

        Debug.gib1("For operator index %d, next is: %d, prev is: %d\n", operatorIndex, next, prev);

        // out of bound
        if (operandIndex < 0 || operandIndex >= internals_mutated.size())
            return false;

        if (o.getDirection() == Operator.AssociatingDirection.LEFT_RIGHT)
            return operandIndex == next || operandIndex == prev;

        if (o.getDirection() == Operator.AssociatingDirection.LEFT)
            return operandIndex == prev;

        return o.getDirection() == Operator.AssociatingDirection.RIGHT && operandIndex == next;
    }

    private List<Expressible> neighbours(int index) {
        List<Expressible> neighbors = new ArrayList<>();
            if (index > 0) {
                int prevIndex = prevOperandOrOperatorIndex(index);
                if (!isOutOfBound(prevIndex))
                    neighbors.add(internals_mutated.get(prevIndex));
            }
            // advance past the current
            //iterator.next();
            if (index < internals_mutated.size() - 1) {
                int nextIndex = nextOperandOrOperatorIndex(index);
                if (!isOutOfBound(nextIndex))
                    neighbors.add(internals_mutated.get(nextIndex));
                //iterator.previous();
            }
            return neighbors;
    }


   public Expression addExpressible(Expressible e) {
       //todo performance
       invalidatePrevComputation();
        if (internals_mutated.contains(e)) {
            Debug.warn("calverter.Expressible %s already in the expression. Proceed with caution.\n", e.toRep());
        }
        Debug.gib2("Adding %s to index %s", e.toString(), internals_mutated.size());
        if (e.toCategory() == Expressible.Category.CLEAR) {
            if (!internals_mutated.isEmpty())
            {
                //todo
                Expressible l = internals_mutated.getLast();
                if (l.toCategory() == Expressible.Category.OPERAND && l.toRep().length() > 1){
                    Number n = (Number) l;
                    String tmp = n.getUserInput();
                    tmp = tmp.substring(0, tmp.length() - 1);
                    if (tmp.charAt(tmp.length() - 1) == '.')
                        tmp = tmp.substring(0, tmp.length() - 1); // remove float point if no decimal portion is found
                    // if n after tripped is empty then remove it from exp
                    if (tmp.isEmpty())
                        internals_mutated.removeLast();
                    // else update it
                    else
                        n.setUserInput(tmp);
                }
                else {
                    internals_mutated.removeLast();
                }

            }
            else
                Debug.warn("Warn. Clearing empty Expression.");
        }

        else if (e.toCategory() == Expressible.Category.CLEAR_ALL) {
            if (!internals_mutated.isEmpty()) {
                internals_original.clear();
                invalidatePrevComputation();
            }
            else
                Debug.warn("Warn. Clearing All empty Expression");
        }

        else {

            if (e.toCategory() == Expressible.Category.PARENTHESIS_OPEN) {
                innerLevel++;
                Debug.gib1("Opening parenthesis. New inner level %d\n", innerLevel);
            }
            if (e.toCategory() == Expressible.Category.PARENTHESIS_CLOSE) {
                int closeOk = canCloseP();
                if (closeOk == 0) {
                    innerLevel--;
                    Debug.gib1("Closing parenthesis. New inner level %d\n", innerLevel);
                } else
                    throw new PBEXPException(closeOk, "Index of closing parenthesis: " + (internals_mutated.size()));
            }

            // raise priority if calverter.Expressible is calverter.Operator
            if (e.toCategory() == Expressible.Category.OPERATOR) {
                Operator o = e.toOperator();
                o.setPriority(o.getPriority() + innerLevel * Operator.JUMP_PRIORITY);
            }

            if (internals_mutated.isEmpty() || Rules.validate(internals_mutated.getLast(), e)) {
           //     Debug.gib1("Validation passed first here. %s", internals_mutated.size());
                //merge last operand if arg is numeric
                if (!internals_mutated.isEmpty() && internals_mutated.getLast().isOperand() && e.isOperand()) {
                    Expressible last = internals_mutated.getLast();

                    // check float point
                    if (e.toCategory() == Expressible.Category.FLOAT &&
                            last.toCategory() == Expressible.Category.OPERAND) {
                        Debug.warn("Found float point adding case.");
                        if (((Number) last.toEvaluable()).isFloatMode())
                        {
                            throw new PBEXPException(PBEXPException.NEIGHBOURING_RULES_VIOLATED,
                                    ": Addition of " + e.toSimplifiedRep() + " to " +
                                            toExpressionString(internals_mutated, false) + " failed. Floating point cannot be added to " +
                                            "double operands.");
                        }
                        else {
                            Debug.gib2("Floating point %s is being added to " + toExpressionString(internals_mutated, false), e.toRep());
                            Number n = (Number) internals_mutated.getLast().toEvaluable();
                            if (!n.appendString(e.toRep())) {
                                throw new PBEXPException(PBEXPException.NEIGHBOURING_RULES_VIOLATED,
                                        ": Addition of " + e.toSimplifiedRep() + " to " + toExpressionString(internals_mutated, false) + " failed.");
                            } else {
                                Debug.gib1("Merge complete: %s\n", n.toString());
                                floatLevel++;
                            }
                        }
                    } else {

                        Debug.gib1("[%d] MERGING %s\n", internals_mutated.size(), e.toRep());
                        Number n = (Number) internals_mutated.getLast().toEvaluable();
                        //float point rule violation is included too.
                        if (!n.appendString(e.toRep())) {
                            throw new PBEXPException(PBEXPException.NEIGHBOURING_RULES_VIOLATED,
                                    ": Addition of " + e.toSimplifiedRep() + " to " + toExpressionString(internals_mutated, false) + " failed.");
                        } else {
                            Debug.gib1("Merge complete: %s\n", n.getUserInput());
                        }
                    }
                }

                else {
                    internals_original.add(e);
                    internals_mutated.add(e);
                }

            } else throw new PBEXPException(PBEXPException.NEIGHBOURING_RULES_VIOLATED,
                    ": Addition of " + e.toSimplifiedRep() + " to " + toExpressionString(internals_mutated, false) + " failed.");

        }
       humanReadable = appendExpressibleHumanReadable(e, humanReadable);
       Debug.debug("POST ADDEXPRESSIBLE: %s", humanReadable);
       return this;
    }

   public Expression addExpressibles(Expressible... e) {
        for (Expressible t : e)
            addExpressible(t);
        return this;
    }

    /**
     *
     * @return returns -1 if rules are met. Otherwise returns the index where the rule is first violated.
     */
    int validate() {
        Debug.gib2("VALIDATING INPUT EXPRESSION: %s\n", toString());
         return Rules.validateByRules(internals_mutated);
    }

    private static String toString_internals(LinkedList<Expressible> e, boolean verbose) {
//        return Arrays.toString(e.stream().
//                map(verbose ? Expressible::toRep : Expressible::toSimplifiedRep).collect(Collectors.toList()).toArray());
        //todo javac1.7
        String[] r = new String[e.size()];
        for (int i = 0; i < e.size(); i++) {
             r[i] = "";
            r[i] += verbose ? e.get(i).toRep() : e.get(i).toSimplifiedRep();

        }
        return Arrays.toString(r);
    }

    @Override
    public String toString() {
        return "calverter.Expression{" +
                "Original = " + toString_internals(internals_original, true) +
                ", Final = " + toString_internals(internals_mutated, true) +
                ", resolved = " + isResolved() +
                "}";
    }

    public String toShortString() {
        return "calverter.Expression{" +
                "Original = " + toExpressionString(internals_original, false) +
                ", Final = " + toExpressionString(internals_mutated, false) +
                ", resolved = " + isResolved() +
                "}";
    }

    public String toExpressionString(List<Expressible> expression, boolean verbose) {
        String r = "";

        for (Expressible s: expression)
            r += verbose ? s.toRep() : s.toSimplifiedRep();
        return r;
    }

    public int getComponentCount() {
        return internals_mutated.size();
    }

    //********************************* PUBLICLY PROVIDED OPERATIONS **************************************************//
    public Expression add(Expressible e) {
        this.addExpressible(e);
        return this;
    }

    //todo
}
