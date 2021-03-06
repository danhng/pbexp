package calverter;// Copyright (C) <2016>  Danh Thanh Nguyen
//
// This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 */
public class PBEXPException extends IllegalArgumentException {

    // thrown when two operands are neighbours
    public static final int OPERANDS_NEIGHBOURING = 1;

    // thrown when two binary operators are neighbours
    public static final int BINARY_OPERATORS_NEIGHBOURING = 2;

    // thrown when an operator does not have sufficient number of neighbours
    // this includes both binary and unary operators
    public static final int INSUFFICIENT_NEIGHBOR_FOR_OPERATOR = 3;

    // thrown when class cast conflict happens, i.e. an calverter.Expressible cannot be cast to an operand.
    public static final int CLASS_CAST_EXCEPTION = 4;

    // thrown when the calverter.Expression is empty
    public static final int EXPRESSION_EMPTY = 5;

    // thrown when there are too few or too many operands given to some operator to execute
    public static final int OPERANDS_COUNT_MISMATCH = 6;

    public static final int PARENTHESES_NOT_CLOSED = 7;

    public static final int PARENTHESES_NOT_OPENED_BEFORE_CLOSED = 8;

    public static final int PARENTHESES_EXPRESSION_INCOMPLETE = 9;

    public static final int ELEMENT_NOT_RECOGNISABLE = 10;

    public static final int NEIGHBOURING_RULES_VIOLATED = 11;

    private int id;

    private static final String[] MESSAGES = {
            "OK",
            "Operands cannot be neighbours",
            "Binary operators cannot be neighbours",
            "calverter.Operator has insufficient number of neighbouring operands",
            "Class cast exception",
            "calverter.Expression is empty",
            "Operands given to an operator is either too many or too few.",
            "Some opening parenthesis is not closed",
            "Some closing parenthesis is not opened",
            "calverter.Expression enclosed is incomplete",
            "The current element is not recognised",
            "Neighbouring rules violated."
    };

    private static final String[] MSGCODES = {
            "OK",
            "Neighbouring",
            "Neighbouring",
            "Incomplete",
            "Classcast",
            "Empty",
            "Neighbouring",
            "Parentheses",
            "Parentheses",
            "Incomplete",
            "Bad",
            "Neighbouring"
    };

    public static String getMessageCode(int id) {
        return MSGCODES[id];
    }

    public int getId() {
        return id;
    }

    public PBEXPException(int id) {
        super(MESSAGES[id]);
        this.id = id;
    }

    public PBEXPException(int id, String detailedMessage) {
        super(MESSAGES[id] + ": " + detailedMessage);
    }

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public PBEXPException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public PBEXPException(String message) {
        super(message);
    }
}
