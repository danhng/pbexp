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


import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The interface {@code calverter.Evaluable} represents any operand that is able to be evaluated numerically.
 *
 * <p>
 * Any class implementing this interface must provide implementations for producing a decimal value and an integer value as
 * a result of evaluation.<br/>
 * It is the responsibility of the implementors to ensure toDecimal() and toInteger() produces equivalent values, or
 * it should be made aware by users otherwise.
 *</p>
 *
 *
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 19/03/2016
 */
public interface Evaluable {

    /**
     * Return the evaluated decimal.
     *
     * How the object is evaluated is entirely up to the implementation and it is the job of the implementor to make sure
     * the decimal number evaluated is correct.
     *
     * @return the decimal evaluated from the object
     */
    Double toDecimal();

    /**
     * Return the evaluated integer.
     *
     * How the object is evaluated is entirely up to the implementation and it is the job of the implementor to make sure
     * the integer evaluated is correct.
     *
     * @return the integer evaluated from the object
     */
    Long toInteger();

}
