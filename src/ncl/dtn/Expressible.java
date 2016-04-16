// Maths Expressions Parser
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
 * The interface {@code Expressible} represents objects that are allowed in a typical mathematical expression including:
 * <u>
 *     <li>Operands: see {@code Evaluable}</li>
 *     <li>Operators: see {@code Operator}</li>
 * </u>
 *
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 21/03/2016
 */
public interface Expressible{

    /**
     * Any Expressible can take on only one category which is either an operand (OPERAND) or an operator (OPERATOR)
     */
    enum Category {OPERAND, OPERATOR, PARENTHESIS_OPEN, PARENTHESIS_CLOSE};

    /**
     * @return the string representation of the Expressible
     */
    String toRep();

    /**
     * @return the simplified form which in many case is the notation of the Expressible
     */
    String toSimplifiedRep();

    /**
     * @return the category of the Expressible.
     */
    Category toCategory();

    /**
     * Attempts to cast the Expressible to an Evaluable
     * @return the cast Evaluable
     *
     * @throws  ClassCastException if the Expressible cannot be cast to Evaluable
     */
    Evaluable toEvaluable() throws PBEXPException;

    /**
     * Attempts to cast the Expressible to an Operator
     * @return the cast Operator
     *
     * @throws ClassCastException if the Expressible cannot be cast to Operator
     */
    Operator toOperator();

    Expressible clone();

}
