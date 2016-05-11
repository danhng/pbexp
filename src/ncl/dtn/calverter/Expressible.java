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


/**
 * The interface {@code calverter.Expressible} represents objects that are allowed in a typical mathematical expression including:
 * <u>
 *     <li>Operands: see {@code calverter.Evaluable}</li>
 *     <li>Operators: see {@code calverter.Operator}</li>
 * </u>
 *
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 21/03/2016
 */
public interface Expressible{

    /**
     * Any calverter.Expressible can take on only one category which is either an operand (OPERAND) or an operator (OPERATOR)
     */
    enum Category {OPERAND, OPERATOR, PARENTHESIS_OPEN, PARENTHESIS_CLOSE, CLEAR, CLEAR_ALL};

    /**
     * @return the string representation of the calverter.Expressible
     */
    String toRep();

    /**
     * @return the simplified form which in many case is the notation of the calverter.Expressible
     */
    String toSimplifiedRep();

    /**
     * @return the category of the calverter.Expressible.
     */
    Category toCategory();

    /**
     * Attempts to cast the calverter.Expressible to an calverter.Evaluable
     * @return the cast calverter.Evaluable
     *
     * @throws  ClassCastException if the calverter.Expressible cannot be cast to calverter.Evaluable
     */
    Evaluable toEvaluable() throws PBEXPException;

    /**
     * Attempts to cast the calverter.Expressible to an calverter.Operator
     * @return the cast calverter.Operator
     *
     * @throws ClassCastException if the calverter.Expressible cannot be cast to calverter.Operator
     */
    Operator toOperator();

    /**
     *
     * @return
     */
    boolean isOperand();

    /**
     *
     * @return
     */
    boolean isOperator();

    Expressible clone();

}
