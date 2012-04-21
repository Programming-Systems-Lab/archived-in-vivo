package edu.columbia.cs.psl.invivo.junit.annotation;

public @interface TestCase {
	Class clazz();
	String method();
	VariableReplacement[] replacements() default {};
}
