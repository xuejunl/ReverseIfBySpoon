package org.example;
import static java.sql.DriverManager.println;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class IfReverseTestFoo {

    @Test
    public void if_reverse_test_foo() throws Exception {
        Launcher l = new Launcher();

        // required for having class in the classpath in Maven
        l.setArgs(new String[] {"--source-classpath","src/test/demo/"});

        l.addInputResource("src/test/demo/");
        l.buildModel();

        CtClass foo = l.getFactory().Package().getRootPackage().getElements(new NamedElementFilter<>(CtClass.class, "Foo")).get(0);

        // compiling and testing the initial class
        Class<?> FooClass = InMemoryJavaCompiler.compile(foo.getQualifiedName(), "package "+foo.getPackage().getQualifiedName()+";"+foo.toString());
        IFoo x = (IFoo) FooClass.newInstance();
        // testing its behavior
        assertEquals(false, x.equal(1,1));

        // apply a transformation
        // first find the if statements
        // then reverse "==" and "!=", "&&" and "||", ">" and "<", ">=" and "<="
        for(Object e : foo.getElements(new TypeFilter(CtIf.class))) {
            CtIf if_exp = (CtIf)e;
            for(Object o : if_exp.getCondition().getElements(new TypeFilter(CtBinaryOperator.class))){
                CtBinaryOperator op = (CtBinaryOperator)o;
                if (op.getKind()==BinaryOperatorKind.OR) {
                    op.setKind(BinaryOperatorKind.AND);
                }
                else if (op.getKind()==BinaryOperatorKind.AND) {
                    op.setKind(BinaryOperatorKind.OR);
                }
                if (op.getKind()==BinaryOperatorKind.EQ) {
                    op.setKind(BinaryOperatorKind.NE);
                }
                else if (op.getKind()==BinaryOperatorKind.NE) {
                    op.setKind(BinaryOperatorKind.EQ);
//                    println(String.valueOf(op));
                }
                if (op.getKind()==BinaryOperatorKind.LT) {
                    op.setKind(BinaryOperatorKind.GT);
                }
                else if (op.getKind()==BinaryOperatorKind.GT) {
                    op.setKind(BinaryOperatorKind.LT);
                }
                if (op.getKind()==BinaryOperatorKind.LE) {
                    op.setKind(BinaryOperatorKind.GE);
                }
                else if (op.getKind()==BinaryOperatorKind.GE) {
                    op.setKind(BinaryOperatorKind.LE);
                }

            }


        }
        l.prettyprint();

        // first assertion on the results of the transfo
        assertEquals(0, foo.getElements(new Filter<CtBinaryOperator<?>>() {
            @Override
            public boolean matches(CtBinaryOperator<?> arg0) {
                return arg0.getKind()==BinaryOperatorKind.NE;
            }
        }).size());

        // second assertions on the behavior of the transformed code
        // compiling and testing the transformed class
        FooClass = InMemoryJavaCompiler.compile(foo.getQualifiedName(), "package "+foo.getPackage().getQualifiedName()+";"+foo.toString());
        IFoo y = (IFoo) FooClass.newInstance();
        // testing its behavior
        assertEquals(true, y.equal(1,1));
        assertEquals(false, y.equal(1,2));
    }
}
