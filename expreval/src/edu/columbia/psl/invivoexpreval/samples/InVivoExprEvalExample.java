


package edu.columbia.psl.invivoexpreval.samples;

import java.io.*;
import java.lang.reflect.InvocationTargetException;


import edu.columbia.psl.commons.compiler.CompileException;
import edu.columbia.psl.invivoexpreval.*;
import edu.columbia.psl.invivoexpreval.util.*;

public class InVivoExprEvalExample extends Traverser {
    public static class foo {
        public static void bar() {
            System.out.println("Check");
        }
    }
    public static void main(String[] args) throws CompileException, IOException {
        ExprEvaluator ee = new ExprEvaluator(
            "c > d ? c : d",                     // expression
            int.class,                           // expressionType
            new String[] { "c", "d" },           // parameterNames
            new Class[] { int.class, int.class } // parameterTypes
        );
         
        Integer res = null;
        try {
            res = (Integer) ee.evaluate(
                new Object[] {          // parameterValues
                    new Integer(10),
                    new Integer(11),
                }
            );
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("res = " + res);
        
        ee = new ExprEvaluator(
            "a.bar()",                     // expression
            void.class,                           // expressionType
            new String[] { "a", },           // parameterNames
            new Class[] { foo.class } // parameterTypes
        );
        
        try {
            res = (Integer) ee.evaluate(
                new Object[] {          // parameterValues
                    new foo()
                }
            );
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
