package edu.columbia.cs.psl.invivo.junit;

import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.junit.annotation.TestCase;
import edu.columbia.cs.psl.invivo.junit.annotation.VariableReplacement;

public class SimpleExample {
	@Tested(cases = { @TestCase(clazz = SimpleExampleTest.class, method = "testMultiply", replacements = {
			@VariableReplacement(from = "tester", to = "new edu.columbia.cs.psl.invivo.junit.SimpleExample()"),
			@VariableReplacement(from = "otherNumber", to = "x") }) })
	public int multiply(int x, int y) {
		return x * y;
	}

	public static void main(String[] args) {
		System.out.println("3 x 4 = " + new SimpleExample().multiply(3, 4));
	}
}
