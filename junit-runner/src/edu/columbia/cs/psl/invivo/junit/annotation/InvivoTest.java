package edu.columbia.cs.psl.invivo.junit.annotation;

public @interface InvivoTest {
//	VariableReplacement[] replacements() default {};
	String[] replacements() default {};
}
