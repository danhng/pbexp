// Copyright (C) <2016>  Danh Thanh Nguyen
//
// This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.

import org.junit.runner.Result;

import java.util.List;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 */
final class Rules {

    static final int OK = 0;

    static final int NO = 1;

    // indecisive -- context depended
    static final int IND = 2;

    private static final int OPENING_P = 0;
    private static final int CLOSING_P = 1;
    private static final int UNARY = 2;
    private static final int BINARY = 3;
    private static final int OP = 4;

    /**
     * Rules
     */
    private static int[] openingPRules = {OK, NO , OK, NO, OK};
    private static int[] closingPRules = {NO, OK, IND, OK, NO};
    private static int[] unaryRules = {OK, NO , OK, IND, IND};
    private static int[] binaryRules = {OK, NO , OK, NO, OK};
    private static int[] operandRules = {NO, OK , IND, OK, NO};

    static boolean validate(Expressible first, Expressible second) {
        switch (first.toCategory()) {
            case OPERAND:{
                // operand unary
                if (second.toCategory() == Expressible.Category.OPERATOR && second.toOperator().isUnary())
                    return second.toOperator().getDirection() == Operator.AssociatingDirection.RIGHT;
                return operandRules[toRuleID(second)] != NO;
            }
            case OPERATOR: {
                if (first.toOperator().isBinary())
                    return binaryRules[toRuleID(second)] != NO;
                else {
                    if (second.toCategory() == Expressible.Category.OPERATOR && second.toOperator().isBinary())
                        return first.toOperator().getDirection() == Operator.AssociatingDirection.LEFT;
                    if (second.toCategory() == Expressible.Category.OPERAND)
                        return first.toOperator().getDirection() == Operator.AssociatingDirection.RIGHT;
                    return unaryRules[toRuleID(second)] != NO;
                }
            }
            case PARENTHESIS_OPEN:
                return openingPRules[toRuleID(second)] != NO;
            case PARENTHESIS_CLOSE: {
                if (second.toCategory() == Expressible.Category.OPERATOR && second.toOperator().isUnary()) {
                    return second.toOperator().getDirection() == Operator.AssociatingDirection.LEFT;
                }
                return closingPRules[toRuleID(second)] != NO;
            }
        }
        return false;
    }

    /**
     *
     * @param expressibles
     * @return returns -1 if rules are met. Otherwise returns the index where the rule is first violated.
     */
    static int validateByRules(List<Expressible> expressibles) {
        for (int i = 0; i < expressibles.size() - 1; i++) {
            if (!validate(expressibles.get(i), expressibles.get(i+1)))
                return i;
        }
        return -1;
    }

    private static int toRuleID(Expressible expressible) {
        switch (expressible.toCategory()) {
            case OPERAND:
                return OP;
            case OPERATOR:
                return expressible.toOperator().isUnary() ? UNARY : BINARY;
            case PARENTHESIS_OPEN:
                return OPENING_P;
            case PARENTHESIS_CLOSE:
                return CLOSING_P;
            default:
                throw new IllegalArgumentException("Expressible of type " + expressible.toCategory() + " does not have a rule ID.");
        }
    }


}
