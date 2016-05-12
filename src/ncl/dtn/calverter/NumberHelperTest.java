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


import org.junit.*;
import org.junit.Test;

import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 *         Date created: 15/03/2016
 */
public class NumberHelperTest {

    String[][] inputsNoFormat = {
            {"10010110010", "2262", "1202", "4B2"},
            {"10111101011001", "27531", "12121", "2F59" },
            {"1111111111111111111111111111111010111010010000001011001101001001", "1777777777727220131511", "-5465132215", "FFFFFFFEBA40B349"}};
    String[] trueFloats = {"-102.3", "31.0", "102.3", "1.3", "0.3212"};
    String[] falseFloats = {"31", "31", "0", "-3", "2.3.3", ".3"};
    String[][] validStringNumbers = {{"101", "0", "01"}, {"012", "77", "12"}, {"-1", "-2.3", ".3", "239"}, {"FEA", "FFFFFFFFFFBFFBBB", "01F"}};

    @Before
    public void setUp() throws Exception {
        Debug.setDebugLevel(Debug.ALERT);
    }

    @After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testIsFloat() throws Exception {
        for (String floats : trueFloats)
            assertTrue(NumberHelper.isFloat(floats));
        for (String floats : falseFloats)
            assertFalse(NumberHelper.isFloat(floats));

        long v = (long) -5 | -3;
    //        System.out.printf("pow is: %s, size \n", Long.toBinaryString();
    }

    @Test
    public void testToFormatNoFormat() throws Exception {
        for (String[] suite : inputsNoFormat) {
            System.out.printf("********Suite: %s*********\n", Arrays.toString(suite));
            for (int modeIn = Number.BIN_MODE; modeIn < Number.HEX_MODE; modeIn++)
                for (int modeOut = Number.BIN_MODE; modeOut < Number.HEX_MODE; modeOut++) {
                    System.out.printf("     Mode in: %s, Mode out: %s\n", Number.FORMATS[modeIn], Number.FORMATS[modeOut]);
                    String actual = null;
                    String expected = null;
                    try {
                        actual = NumberHelper.toFormat(modeOut, suite[modeIn], modeIn, Number.NO_FORMAT);
                        expected = suite[modeOut];
                        assertTrue(actual.equalsIgnoreCase(expected));
                        System.out.printf("         Success\n");
                    }
                    catch (AssertionError e) {
                        throw new AssertionError("         Failed. Expected: "+expected+", Actual: "+actual+"\n");
                    }
                }
        }
    }

    @Test
    public void testFillUpTo() throws Exception {
        StringBuilder i = new StringBuilder();
        ArrayList<StringBuilder> l = new ArrayList<>();
        l.add(i);
        l.get(0).append('s');
        System.out.println("New string builder: " + i.toString());
    }

    @Test
    public void testGroupDigits() throws Exception {

    }

    @Test
    public void testIsValidFormat() throws Exception {

    }
}