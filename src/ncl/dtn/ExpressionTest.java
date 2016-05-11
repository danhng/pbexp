// Copyright (C) <2016>  Danh Thanh Nguyen
//
// This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

/**
 * @author Danh Thanh Nguyen <d.t.nguyen@newcastle.ac.uk>
 */
public class ExpressionTest {

    HashMap<String, String> suites;
    Number a = new Number(3);
    Number b = new Number(2);
    Number c = new Number(4);
    Number d = new Number(1000);
    Number e = new Number(22);
    Number f = new Number(2000);

    Operator plus = Operator.get(Operator.ADDITION);
    Operator plus2 = Operator.get(Operator.ADDITION);
    Operator subtract = Operator.get(Operator.SUBTRACTION);
    Operator neg = Operator.get(Operator.NEGATION);
    Operator multi = Operator.get(Operator.MULTIPLICATION);

    // OK
    // 3 + 2 * 4 = 11
//        Expression e1 = new Expression("3 + 2 * 4");
//        e1.addExpressibles(open, a, plus, b, close, multi, c);
//        e1.compute_internal();

    // OK
//        // (3 + 2) * 4 = 20
//        Expression e2 = new Expression("( 3 + 2 ) * 4");
//        e2.addExpressibles(open, a, plus, b, close, multi, c);
//        e2.compute_internal();

    //OK
    // 3 + -2 * 4 = -5
//        Expression e3 = new Expression("3 + -2 * 4");
//        e3.addExpressibles(a, plus, neg, b, multi, c);
//        e3.compute_internal();

    //OK
    // nest
    // (3 + 2) * (4 - (1000 + 22)) =  -5090


    @Before
    public void setUp() throws Exception {
        Debug.setDebugLevel(Debug.DEBUG);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test_compute_internal_basic() {
        Expression e4 = new Expression("( 3 + 2 ) * ( 4 ");
        e4.addExpressibles(     Expression.Parenthesis.getOpening(), a, plus, b,
                                Expression.Parenthesis.getClosing(),
                            multi,
                                Expression.Parenthesis.getOpening(), c, subtract,
                                        Expression.Parenthesis.getOpening(), d, plus2, e,
                                        Expression.Parenthesis.getClosing(),
                                Expression.Parenthesis.getClosing());
        // -5090;
        assertTrue(e4.compute_internal().toDecimal().doubleValue() == -5090);

    }

    @Test
    public void test_compute_internal_nextOpes() {
        Expression e4 = new Expression("22");
        e4.addExpressibles(     a, b);
        // -5090;
        Debug.debug(e4.toShortString());
        assertTrue(e4.compute_internal().toDecimal().doubleValue() == 32);

    }
}
