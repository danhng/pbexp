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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 19/03/2016
 */
public class Operation {

    private List<Evaluable> operands;

    private Operator operator;

    private boolean completed;

    // todo fixme incompatible with Action interface
    private Evaluable result;

    @Override
    public String toString() {
        String operandstr = "[";
        for (Evaluable operand : operands)
            operandstr += operand.toDecimal().toString() + ", ";
        operandstr += "]";

        return "calverter.Operation{" +
                // todo javac1.7
//                "operands=" + Arrays.toString(operands.stream().map(Evaluable::toDecimal).toArray()) +
                "operands=" + operandstr +
                ", operator=" + operator +
                ", completed=" + completed +
                ", result=" + (result == null ? "N/A" : result.toDecimal()) +
                ", ready=" + isOperationComputable() +
                '}';
    }

    static class OperationBuilder {
        private List<Evaluable> operands;

        private Operator operator;

        public OperationBuilder() {
                   operands = new ArrayList<>();
        }

        /**
         * Sets operands
         * @param operands the operands to be set
         * @return the {@code OperationBuilder} instance
         * @throws IllegalArgumentException if the operands can't be set due to a mismatch between input operand number
         * and expected operand number which could have been set already.
         * @throws NullPointerException if the operands collection is null
         */
        public OperationBuilder setOperands(Collection<Evaluable> operands) {
            if (operands != null && operands.size() != operator.getOperandsCount())
                Operator.throwMismatchOperandCount(operator.getName(), operator.getOperandsCount(), operands.size());
            if (operands == null)
                throw new NullPointerException("operands to be set cannot be null.");
            this.operands = new ArrayList<>(operands);
            return this;
        }

        /**
         * Sets the operator
         * @param operator the operator to be set
         * @return the {@code OperationBuilder} instance
         * @throws IllegalArgumentException if the operator can't be set due to a mismtach between required operand number
         * and actual operand number which could have been set already.
         * @throws NullPointerException if the operator to be set is null
         */
        public OperationBuilder setOperator(Operator operator) {
            this.operator = operator;
            if (operands != null && !operands.isEmpty() && operands.size() != operator.getOperandsCount())
                Operator.throwMismatchOperandCount(operator.getName(), operator.getOperandsCount(), operands.size());
            if(operator == null)
                throw new NullPointerException("operator to be set cannot be null");
            this.operator = operator;
            return this;
        }

        /**
         * Builds the operation
         * @return the {@code calverter.Operation} instance to be built
         *
         * @throws NullPointerException if the operands or operator is null
         * @throws IllegalArgumentException if the operator can't be set due to a mismatch between required operand number
         * in the operator and the operand number in the operands collection.
         */
        public Operation build() {
            if (operator == null)
                throw new NullPointerException("operator to be set cannot be null");
            if (operands == null)
                throw new NullPointerException("operands to be set cannot be null");
            if (!operands.isEmpty() && operands.size() != operator.getOperandsCount())
                Operator.throwMismatchOperandCount(operator.getName(), operator.getOperandsCount(), operands.size());
            return new Operation(operands, operator);
        }
    }

    private Operation(List<Evaluable> operands, Operator operator) {
        this.operands = operands;
        this.operator = operator;
        completed = false;
    }

    boolean checkOperandAssociability(Evaluable e) {

        if (!isModificationAllowed()) {
            Debug.warn("calverter.Operation " + toString() + "can't be modified.\n");
            return false;
        }
            if (operands.size() >= operator.getOperandsCount())
            {
                Debug.warn("Can't associate any more operands. Operand count reached: %d\n", operator.getOperandsCount());
                return false;
            }
            if (operands.contains(e)) {
                Debug.warn("Can't associate operand %s to calverter.Operation %s. It has been already associated.\n", e.toDecimal(), this);
                return false;
            }
        return true;
    }

    public void associateOperand(Evaluable e) {
        if (isModificationAllowed())
            operands.add(e);
        else
            Debug.warn("calverter.Operation " + toString() + "can't be modified.\n");
    }

    public boolean isOperationComputable() {
        return operands.size() == operator.getOperandsCount();
    }

    public Evaluable compute() {
        if (!isOperationComputable())
            return null;
        result = isOperationCompleted() ? result :  operator.actionDiscrete(operands.toArray(new Evaluable[1]));
        completed = true;
        return result;
    }

    public boolean isOperationCompleted() {
        return completed;
    }

    public List<Evaluable> getOperands() {
        return operands;
    }

    public Operator getOperator() {
        return operator;
    }

    public Evaluable getResult() {
        return result;
    }

    private boolean isModificationAllowed() {
       return !isOperationCompleted();
         }
}