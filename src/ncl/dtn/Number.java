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


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 * date created: 14/03/2016
 */
public class Number implements Evaluable, Expressible {

    public static final int BIN_MODE = 0;
    public static final int OCT_MODE = 1;
    public static final int DEC_MODE = 2;
    public static final int HEX_MODE = 3;

    public static final String[] FORMATS = {"BINARY", "OCTAL", "DECIMAL", "HEXADECIMAL"};

    public static final int MAX_BITS = Long.BYTES * 8;

    public static final String[] SUFFICES = {"0B", "0", "", "0X"};

    // digits of different formats
    public static final Character[][] DIGITS = {
            {'0', '1'},
            {'0', '1', '2', '3', '4', '5', '6', '7'},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}};

    // conventional groups of digits in formats for improved readability
    public static final int[] GROUPS = {4, 3, 3, 4};

    // conventional group delimiters for number formats;
    public static final char[] DELIMITERS = {' ', ' ', ',', ' '};

    // String rep used when some mode is not supported for some required input value.
    public static String NAN = "N/A";

    //Formatting options by combining options of grouping and stuffing
    // no formatting options
    public static final int NO_FORMAT = 0;

    // add the leading 'default' characters (ex. zeroes for binary string)
    public static final int DO_STUFFING = 1;

    // group digits by placing delimiters between them
    public static final int DO_GROUPING = 2;

    private String userInput; // original user input
    private int modeIn; // mode the the input
    private double fvalue; // the float value
    private long  ivalue; // the int value
    //private boolean isFloatMode;
    private String[] reps; // string reps of the number

    // constructors
    public Number(int modeIn, String rep) {
        this.modeIn = modeIn;
        this.userInput = rep;
        updateStringReps();
    }

    public Number(long iValue, double fValue) {
        this.ivalue = iValue;
        this.fvalue = fValue;
        modeIn = DEC_MODE;
        userInput = String.valueOf(fValue);
        updateStringReps();
    }

    /**
     * default fValue
     * @param iValue
     */
    public Number(long iValue) {
        this.ivalue = iValue;
        fvalue = (double) iValue;
        userInput = String.valueOf(iValue);
        modeIn = DEC_MODE;
        updateStringReps();
    }

    /**
     * default iValue is the rounded-up
     * @param fValue
     */
    public Number(double fValue) {
        this.fvalue = fValue;
        ivalue = Math.round(fValue);
        userInput = String.valueOf(fValue);
        modeIn = DEC_MODE;
        updateStringReps();
    }

    /**
     *
     * @return
     */
    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    /**
     * Re-update string reps based on the user input
     */
    private void refresh() {
        reps = NumberHelper.toFormats(userInput, modeIn, NO_FORMAT);
        }

    public double getFvalue() {
        return fvalue;
    }

    public void setFvalue(double fvalue) {
        this.fvalue = fvalue;
    }

    public long getIvalue() {
        return ivalue;
    }

    public void setIvalue(long ivalue) {
        this.ivalue = ivalue;
    }

    public boolean isFloatMode() {
        return NumberHelper.isFloat(userInput);
    }

    public String[] getReps() {
        return reps;
    }

    public void setReps(String[] reps) {
        this.reps = reps;
    }

    public int getModeIn() {
        return modeIn;
    }

    public void setModeIn(int modeIn) {
        this.modeIn = modeIn;
    }



    @Override
    public String toString() {
        return "Number{" +
                "userInput='" + userInput + '\'' +
                ", modeIn=" + modeIn +
                ", fvalue=" + fvalue +
                ", ivalue=" + ivalue +
                ", reps=" + Arrays.toString(reps) +
                '}';
    }

    /**
     * Return the evaluated decimal.
     * <p>
     * How the object is evaluated is entirely up to the implementation and it is the job of the implementor to make sure
     * the decimal number evaluated is correct.
     *
     * @return the decimal evaluated from the object
     */
    @Override
    public BigDecimal toDecimal() {
        return new BigDecimal(fvalue);
    }

    /**
     * Return the evaluated integer.
     * <p>
     * How the object is evaluated is entirely up to the implementation and it is the job of the implementor to make sure
     * the integer evaluated is correct.
     *
     * @return the integer evaluated from the object
     */
    @Override
    public BigInteger toInteger() {
        return new BigInteger(String.valueOf(ivalue));
    }

    private void updateStringReps() {
        refresh();
    }

    @Override
    public String toRep() {
        return String.valueOf(fvalue);
    }

    /**
     * @return the simplified form which in many case is the notation of the Expressible
     */
    @Override
    public String toSimplifiedRep() {
        return toRep();
    }

    @Override
    public Category toCategory() {
        return Category.OPERAND;
    }

    @Override
    public Evaluable toEvaluable() {
        return this;
    }

    /**
     * Attempts to cast the Expressible to an Operator
     *
     * @return the cast Operator
     * @throws ClassCastException if the Expressible cannot be cast to Operator
     */
    @Override
    public Operator toOperator() {
        throw new ClassCastException("Number objects cannot be cast to Operator: " + toRep());
    }

    @Override
    public Expressible clone() {
        Number n = new Number(modeIn, userInput);
        n.setFvalue(fvalue);
        n.setIvalue(ivalue);
        n.setReps(reps);
        return n;
    }
}
