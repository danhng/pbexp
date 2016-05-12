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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 14/03/2016
 */

public class NumberHelper {

//    /**
//     * check whether a format is valid, that is, there must be at most one option specified for each formatting aspect.
//     * If there is no option specified for an aspect then the default action is to do nothing with regards to that aspect.
//     * @param format the input format to be test
//     * @return true if the format is valid, false otherwise
//     */
//    public static boolean isFormatValid(int format) {
//        format &= 0b1111; // mask out trash
//        return ((format & calverter.Number.DO_STUFFING) != (format & calverter.Number.NO_STUFFING) &&
//                (format & calverter.Number.DO_GROUPING) != (format & calverter.Number.NO_GROUPING));
//    }

    /**
     * Analyse the rep to see if it's a float number or not.
     * Examples of float number: 1.2, -1.2, .5, -0.5, 1.
     * Examples of non-float (int): 1, 123, 0123.
     * @param rep the string to be analysed
     * @return true if rep is of float form, false otherwise (error or int).
     */
    public static boolean isFloat(String rep) {
//        rep = rep.trim();
////        if (rep.charAt(0) == '-')
////            rep = rep.substring(1);
//        String[] parts = rep.split("\\."); // split on decimal point
//
//        if (parts.length > 2) {
//            Debug.debug("rep %s is not a legal number.\n", rep);
//            return false;
//        }
        // return true only if there are both int part and dec part and dec part is not empty
        return rep.matches("-?[0-9]+\\.[0-9]+");
    }

    public static String[] toFormats(String rep, int modeIn, int format) {
//        return Arrays.asList(Number.BIN_MODE, Number.OCT_MODE, Number.DEC_MODE, Number.HEX_MODE)
//                .stream().map(i -> toFormat(i, rep, modeIn, format)).collect(Collectors.toList()).toArray(new String[1]);
    //todo javac1.7 compat
        List<Integer> l = Arrays.asList(Number.BIN_MODE, Number.OCT_MODE, Number.DEC_MODE, Number.HEX_MODE);
        boolean isFloat = modeIn == Number.DEC_MODE && isFloat(rep);
        String[] out = new String[4];
        for (int i = 0; i < l.size(); i++) {
            if (i != Number.DEC_MODE && isFloat)
                out[i] = "null";
            else
                out[i] = toFormat(i, rep, modeIn, format);
        }
        return out;
    }

    /**
     * Produce string representation of required output format
     * @param modeOut the required output format
     * @param rep the input string rep
     * @param modeIn the input string rep's format
     * @param format the format of the output string
     * @return the output string
     * @throws NumberFormatException if {rep} is not a valid string representation of format {modeIn}
     */
    public static String toFormat(int modeOut, String rep, int modeIn, int format) {
        if (!isValidFormat(rep, modeIn, false)) {
            throw new NumberFormatException("String " + rep + " is not of format " + Number.FORMATS[modeIn]);
        }
        switch (modeOut) {
            case Number.BIN_MODE: {
                return toBin(modeIn, rep, format);
            }
            case Number.OCT_MODE: case Number.DEC_MODE: case Number.HEX_MODE: {
                return toOctDecHex(modeOut, rep, modeIn, format);
            }
            default: {
                Debug.warn("output mode format %d is invalid. \n", modeIn);
                return Number.NAN;
            }
        }
    }


    private static int getNearestMulOf(int in, int factor) {
        return (int) (Math.ceil((double) in / factor) * factor);
    }

    /**
     * The problem with converting to other modes is that Long.to[Mode]String family functions don't support negative numbers.
     * A workaround is to convert rep to binary strings first which then can be used to convert to desired format easily.
     * @param modeOut the output format
     * @param rep the input string to be converted
     * @param modeIn the input format
     * @param format formatting options
     * @return the output string converted to {modeOut} format
     */
    private static String toOctDecHex(int modeOut, String rep, int modeIn, int format) {
        String out;
        rep = succinct(rep);
        if (modeOut != Number.OCT_MODE && modeOut != Number.DEC_MODE && modeOut != Number.HEX_MODE) {
            try {
                Debug.warn("toOctDecHex requires Oct/Dec/Hex modeOut. %s given\n", Number.FORMATS[modeOut]);
            }
            catch (Exception e) {
                Debug.warn("Invalid format mode. %d\n", modeOut);
            }
        }
        // if modeIn == modeOut already
        if (modeIn == modeOut) {
            // do the formatting if needed
            return (format & Number.DO_GROUPING) > 0 ?
                    groupDigits(rep, Number.GROUPS[modeOut], Number.DELIMITERS[modeOut]) : rep;
        }
        Long in;
        // else if the rep is decimal then we need to convert it to oct using the lib first
        if (modeIn == Number.DEC_MODE && (in = Long.valueOf(rep)) >= 0) {
            // if value is >= 0 then we could use the lib function to convert it.
                out = modeOut == Number.OCT_MODE ? Long.toOctalString(in) : Long.toHexString(in);
                // no need to do the stuffing
            }
        // if the dec is negative or the rep is of another format
        // then convert it to binary string before converting back to oct
        else {
            out = "";
            // convert it to raw binary string first
            String bin = toBin(modeIn, rep, Number.NO_FORMAT);

            // if modeOut is DEC then flip all bits, add 1 and multiply by -1 to get the dec
            if (modeOut == Number.DEC_MODE) {
//                String flipped = flipBits(bin);
//                out = String.valueOf((Long.valueOf(flipped, 2) + 1) * -1);
                // another way is to use BigInteger
                 out = String.valueOf(new BigInteger(bin, 2).longValue());
            }
            // else if modeOut is either HEX or OCT then group 4 or 3 bin digits of the bin string and work out the corresponding chars.
            else {
                // group digits as groups of modeOut (For convenient octal conversion)
                String[] groups = groupDigits(bin, Number.GROUPS[modeOut], Number.DELIMITERS[Number.BIN_MODE])
                        .split(String.valueOf(Number.DELIMITERS[Number.BIN_MODE]));
                Debug.debug("Groups of mode %d chars in binary is: %s\n", modeOut, Arrays.toString(groups));

                // convert each group of {modeOut} binary digits to a modeOut value
                for (int i = groups.length - 1; i >= 0; i--) {
                    String bins = groups[i];
                    // no need to worry about sign here
                    int value = Integer.valueOf(bins, 2);
                    String modeOutChar = String.valueOf(Number.DIGITS[modeOut][value]);
                    Debug.debug("bins: %s, value: %s, modeOut val: %s\n", bins, value, modeOutChar);
                    out = modeOutChar + out;
                }
            }
        }
        // do the final formatting
        return (format & Number.DO_GROUPING) > 0 ?
                groupDigits(out, Number.GROUPS[modeOut], Number.DELIMITERS[modeOut]) : out;
    }

    /**
     * Flip all bits of a binary string
     * @param binRep the binary string in which all bits are to be flipped
     * @return the flipped output
     */
    private static String flipBits(String binRep) {
        if (!isValidFormat(binRep, Number.BIN_MODE, true)) {
            Debug.warn("flipBits requires a valid binary string. %s given.\n", binRep);
            return Number.NAN;
        }
        char[] characters = binRep.toCharArray();
        for(int i = 0; i < characters.length; i++) {
            characters[i] = characters[i] == '0' ? '1' : '0';
        }
        return String.valueOf(characters);
    }

    private static String succinct(String rep) {
        if (rep.matches("-?[0-9]+\\.0*"))
            return rep.split("\\.")[0];
        return rep;
    }

    /**
     * Convert any kind of number formats to binary format
     * @param modeIn the number format of the input
     * @param rep the input number as string
     * @param format format of the output string representation
     * @return the output number as binary string
     */
    private static String toBin(int modeIn, String rep, int format) {
        //int BITS = Long.BYTES * 8;
        String out = Number.NAN;
        rep = succinct(rep);
        // todo binary for float not yet supported
        if (modeIn == Number.DEC_MODE &&  isFloat(rep)) {
            return Number.NAN;
        }

        // if the rep is of binary mode already, then we just need to "stuff" some leading zeroes to make sure all groups are flat
        if (modeIn == Number.BIN_MODE) {
            out = rep;
            int c = getNearestMulOf(out.length(), Number.GROUPS[Number.BIN_MODE]);
            // fill up the most significant group if needed
            out = (format & Number.DO_STUFFING) == 1 ? fillUpTo(out, c, '0') : out;
            return (format & Number.DO_GROUPING) != 0 ? groupDigits(out, Number.GROUPS[Number.BIN_MODE], Number.DELIMITERS[Number.BIN_MODE]) : out;
        }

        // else if the rep is decimal then we need to convert it to bin using the lib first before do the stuffing.
        if (modeIn == Number.DEC_MODE) {
            Long in = Long.valueOf(rep);
            if (in >= 0) {
                out = Long.toBinaryString(in);
                // are groups flat?
                int c = getNearestMulOf(out.length(), Number.GROUPS[Number.DEC_MODE]);
                // fill up the most significant group if needed
                out = (format & Number.DO_STUFFING) > 0 ? fillUpTo(out, c, '0') : out;
            }
            // if the dec is negative then subtract 1 and flipBits all bits
            else {
                out = Long.toBinaryString(Math.abs(in) - 1);
                // are groups flat?
                // fill up the most significant group no matter whether the format says do stuffing or not as this is negative number
                out = fillUpTo(out, Number.MAX_BITS, '0');
                Debug.debug("pre out neg: %s\n", out);
                char[] characters = out.toCharArray();
                for(int i = 0; i < characters.length; i++) {
                    characters[i] = characters[i] == '0' ? '1' : '0';
                }
                out = String.valueOf(characters);
            }
            return (format & Number.DO_GROUPING) > 0 ? groupDigits(out, Number.GROUPS[Number.BIN_MODE], Number.DELIMITERS[Number.BIN_MODE]) : out;
        }
        //if modeIn is not decimal then we need to expand all digits
        else if (modeIn == Number.HEX_MODE || modeIn == Number.OCT_MODE) {
            Long in = new BigInteger(rep, modeIn == Number.HEX_MODE ? 16 : 8).longValue();
            Debug.debug("pre in non-dec int is: %d\n", in);
            out = Long.toBinaryString(in);
            out = (format & Number.DO_STUFFING) > 0 ? fillUpTo(out, getNearestMulOf(out.length(), Number.GROUPS[Number.BIN_MODE]), '0') : out;
            return (format & Number.DO_GROUPING) > 0 ? groupDigits(out, Number.GROUPS[Number.BIN_MODE], Number.DELIMITERS[Number.BIN_MODE]) : out;
        }
        // for all cases that are not handled yet, N/A will be returned.
        return out;
    }

    static String fillUpTo(String in, int limit, char fill) {
        // fill up the most significant group if needed
        while (in.length() < limit)
            in = fill + in;
        return in;
    }

    /**
     * Group digits by placing a "delimiter" between groups
     * @param in the input string in which digits are grouped.
     * @param groupSize the size of a group
     * @param delimiter the delimiter between two adjacent groups
     * @return the original string with digits grouped.
     */
    static String groupDigits(String in, int groupSize, char delimiter) {
        int currentInsertPos = in.length() - groupSize;
        ArrayList<Character> characters = new ArrayList<>();
        for (char c: in.toCharArray()) {
            characters.add(c);
        }
        while (currentInsertPos > 0) {
            characters.add(currentInsertPos, delimiter);
            currentInsertPos -= (groupSize); // add shifts towards RHS which is why we only subtract groupSize
        }
        String out = "";
        for (Character c : characters) {
            out += c;
        }
        Debug.debug("After groupDigits call: %s\n", out);
        return out;
    }

    /**
     * Verify if the input string is of valid {mode} format
     * @param rep the input string to be verified
     * @param mode the mode against which the input string is to be verified
     * @return true if the input string is valid, false otherwise
     */
    public static boolean isValidFormat(String rep, int mode, boolean strict) {
        String regex;
        switch (mode) {
            case Number.BIN_MODE: {
                regex = "([01]+)|(null)";
                break;
            }
            case Number.OCT_MODE: {
                regex = "([0-7]+)|(null)";
                break;
            }
            case Number.DEC_MODE: {
                regex = strict ? "(-?[0-9]+)|(-?[0-9]+\\.[0-9]+)|(null)" : "(-?[0-9]+)|(-?[0-9]*\\.[0-9]+)|(-?[0-9]+\\.[0-9]*)|(null)";
                break;
            }
            case Number.HEX_MODE: {
                regex = "([0-9A-Fa-f]+)|(null)";
                break;
            }
            default:{
                Debug.warn("mode %d is invalid.\n", mode);
                return false;
            }
        }
        return rep.matches(regex);
    }
}
