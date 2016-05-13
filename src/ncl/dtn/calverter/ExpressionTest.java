package calverter;// Copyright (C) <2016>  Danh Thanh Nguyen
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
//        calverter.Expression e1 = new calverter.Expression("3 + 2 * 4");
//        e1.addExpressibles(open, a, plus, b, close, multi, c);
//        e1.compute();

    // OK
//        // (3 + 2) * 4 = 20
//        calverter.Expression e2 = new calverter.Expression("( 3 + 2 ) * 4");
//        e2.addExpressibles(open, a, plus, b, close, multi, c);
//        e2.compute();

    //OK
    // 3 + -2 * 4 = -5
//        calverter.Expression e3 = new calverter.Expression("3 + -2 * 4");
//        e3.addExpressibles(a, plus, neg, b, multi, c);
//        e3.compute();

    //OK
    // nest
    // (3 + 2) * (4 - (1000 + 22)) =  -5090


    @Before
    public void setUp() throws Exception {
        Debug.setDebugLevel(Debug.GIB1);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test_addExp_Float_Add_Point() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressible(k).addExpressible(Expression.FloatPoint.get()).addExpressible(a);
        Debug.gib1(e2.toString());
        assertTrue(Double.valueOf(e2.compute(false))== 1.3);
    }

    @Test
    public void test_Suppress_1() {

            Expression e4 = new Expression("( 3 + 2 ) * ( 4 ", true);
            e4.addExpressibles(
                    Expression.Parenthesis.getOpening(), a, plus, b,
                    Expression.Parenthesis.getClosing(),
                    multi,
                    Expression.Parenthesis.getOpening(), c, subtract,
                    Expression.Parenthesis.getOpening(), d, plus2, e,
                    Expression.Parenthesis.getClosing());
            // -5090;
            Debug.warn("Size of test compute internal: %s", e4.getComponentCount());
            assertTrue(Double.valueOf(e4.compute(true)) ==  -5090);

        }


    @Test
    public void test_Suppress_2() {

        Expression e4 = new Expression("( 3 + 2 ) * ( 4 ", true);
        e4.addExpressibles(
                Expression.Parenthesis.getOpening(), a, plus, b,
                Expression.Parenthesis.getClosing(),
                multi,
                Expression.Parenthesis.getOpening(), c, subtract,
                Expression.Parenthesis.getOpening(), d, plus2, e,
                Expression.Parenthesis.getClosing(),
                Expression.Parenthesis.getClosing(), Operator.get(Operator.DIVISION));
        // -5090;
        Debug.warn("Size of test compute internal: %s", e4.getComponentCount());
        assertTrue(Double.valueOf(e4.compute(true)) ==  -5090);

    }

    @Test
    public void test_Humanread_1() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(a, multi, b, Expression.FloatPoint.get(), c, multi, Expression.Parenthesis.getOpening());
        Debug.gib1(e2.getHumanReadable());
        }

    @Test
    public void test_Humanread_2() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(a, multi, b, Expression.FloatPoint.get(), c, multi, Expression.Parenthesis.getOpening(),
                Expression.Clearance.getClearAll());
        assertTrue(e2.getHumanReadable().isEmpty());
    }

    @Test
    public void test_Humanread_3() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(a, multi, b, Expression.FloatPoint.get(), c, multi, Expression.Parenthesis.getOpening(),
                Expression.Clearance.getClear());
        Debug.gib1(e2.getHumanReadable());
    }

    @Test
    public void test_addExp_Float_Basic_Operation() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(k,
                Expression.FloatPoint.get(), a,
                plus,
                b, Expression.FloatPoint.get(), c);
        Debug.gib1(e2.toString());
        assertTrue(Double.valueOf(e2.compute(false)) == 3.7);
    }

    @Test
    public void test_addExp_Float_Basic_Operation_1() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(k,
                Expression.FloatPoint.get(), a,
                plus,
                b );
        Debug.gib1(e2.toString());
        assertTrue(Double.valueOf(e2.compute(false)) == 3.3);
    }

    @Test
    public void test_addExp_Float_Basic_Operation_2() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(k,
                Expression.FloatPoint.get(), a,
                multi,
                b );
        Debug.gib1(e2.toString());
        assertTrue(Double.valueOf(e2.compute(false)) == 2.6);
    }


    @Test
    public void test_addExp_Float_Basic_Operation_3() {
        Expression e2 = new Expression("1", false);
        Number k = new Number(1);

        e2.addExpressibles(k,
                Expression.FloatPoint.get(),
                multi,
                b );
        Debug.gib1(e2.toString());
        assertTrue(Double.valueOf(e2.compute(false)) == 2);
    }

    //todo more test suites

    @Test
    public void test_compute_internal_basic() {
        Expression e4 = new Expression("( 3 + 2 ) * ( 4 ", false);
        e4.addExpressibles(     Expression.Parenthesis.getOpening(), a, plus, b,
                                Expression.Parenthesis.getClosing(),
                            multi,
                                Expression.Parenthesis.getOpening(), c, subtract,
                                        Expression.Parenthesis.getOpening(), d, plus2, e,
                                        Expression.Parenthesis.getClosing(),
                                Expression.Parenthesis.getClosing());
        // -5090;
        Debug.warn("Size of test compute internal: %s", e4.getComponentCount());
        assertTrue(Double.valueOf(e4.compute(false)) ==  -5090);

    }

    @Test
    public void test_Expression_makeCopy() {
        Expression e4 = new Expression("( 3 + 2 ) * ( 4 ", false);
        e4.addExpressibles(     a, plus, b);

        Expression e5 = Expression.makeCopy(e4);

        assertTrue(e4.getComponentCount() == e5.getComponentCount());
        for (Expressible expressible : e4.getInternals_original()) {
            assertFalse(e5.getInternals_original().contains(expressible));
        }
        for (Expressible expressible : e5.getInternals_original()) {
            assertFalse(e4.getInternals_original().contains(expressible));
        }


    }

    @Test
    public void test_compute_internal_nextOpes() {
        Debug.gib2("TEST COMPUTE INTERNAL NEXTOPES");
        Expression e4 = new Expression("22", false);
        e4.addExpressibles(     a, b);
        // -5090;
        Debug.gib1(e4.toShortString());
        assertTrue(Double.valueOf(e4.compute(false)) == 32);

        Debug.gib2("TEST COMPUTE INTERNAL NEXTOPES END");
    }

    @Test
    public void test_compute_internal_clearance() {

        Debug.gib2("TEST COMPUTE INTERNAL CLEARANCE");
        Expression e4 = new Expression("22", false);
        e4.addExpressibles(     a, plus, Expression.Clearance.getClear());
        // -5090;
        Debug.gib1(e4.toShortString());
        assertTrue(Double.valueOf(e4.compute(false)) == a.getFvalue());

        Debug.gib2("TEST COMPUTE INTERNAL CLEARANCE END");
    }

    @Test
    public void test_compute_internal_clearanceAll() {
        Expression e4 = new Expression("22",false);
        e4.addExpressibles(     a, plus, Expression.Clearance.getClearAll());
        // -5090;
        Debug.gib1(e4.toShortString());
        assertTrue(e4.isEmpty());

    }

    @Test
    public void test_compute_internal_FloatPoint() {
        Expression e4 = new Expression("22", false);
        e4.addExpressibles(     a, Expression.FloatPoint.get());
        Debug.gib1(e4.toShortString());
        // -5090;


    }
}
