package org.example;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.CtBFSIterator;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import static java.sql.DriverManager.println;
import static org.junit.Assert.assertEquals;

public class IfReverseTestDemo {
    @Test
    public void if_reverse_test_demo(){
        // load the java file for transformation
        Launcher l = new Launcher();
        l.addInputResource("src/test/demo");
        l.buildModel();
        CtClass demo = l.getFactory().Package().getRootPackage().getElements(new NamedElementFilter<>(CtClass.class,"Demo")).get(0);
        // apply the transformation:
        // first find the all the "if" condition expressions
        // then reverse "==" and "!=", "&&" and "||", ">" and "<", ">=" and "<="
        for(Object e : demo.getElements(new TypeFilter(CtIf.class))) {
            CtIf if_exp = (CtIf) e;
             for (Object o : if_exp.getCondition().getElements(new TypeFilter(CtBinaryOperator.class))) {
                 CtBinaryOperator op = (CtBinaryOperator) o;
                 if (op.getKind() == BinaryOperatorKind.OR) {
                     op.setKind(BinaryOperatorKind.AND);
                 } else if (op.getKind() == BinaryOperatorKind.AND) {
                     op.setKind(BinaryOperatorKind.OR);
                 }
                 if (op.getKind() == BinaryOperatorKind.EQ) {
                     op.setKind(BinaryOperatorKind.NE);
                 } else if (op.getKind() == BinaryOperatorKind.NE) {
                     op.setKind(BinaryOperatorKind.EQ);
//                    println(String.valueOf(op));
                 }
                 if (op.getKind() == BinaryOperatorKind.LT) {
                     op.setKind(BinaryOperatorKind.GT);
                 } else if (op.getKind() == BinaryOperatorKind.GT) {
                     op.setKind(BinaryOperatorKind.LT);
                 }
                 if (op.getKind() == BinaryOperatorKind.LE) {
                     op.setKind(BinaryOperatorKind.GE);
                 } else if (op.getKind() == BinaryOperatorKind.GE) {
                     op.setKind(BinaryOperatorKind.LE);
                 }
             }
         }
        l.prettyprint();
        // Because the demo code is not a complete class, so we just do some simple assertion on the results of the
        // transformation instead of the functional check.
        assertEquals(2, demo.getElements(new Filter<CtBinaryOperator<?>>() {
            @Override
            public boolean matches(CtBinaryOperator<?> arg0) {
                return arg0.getKind()==BinaryOperatorKind.EQ;
            }
        }).size());
        assertEquals(0, demo.getElements(new Filter<CtBinaryOperator<?>>() {
            @Override
            public boolean matches(CtBinaryOperator<?> arg0) {
                return arg0.getKind()==BinaryOperatorKind.AND;
            }
        }).size());

    }
}
