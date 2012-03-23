package edu.columbia.cs.psl.invivo.junit;

public class SimpleExample {
	@Tested(testClass = "edu.columbia.cs.psl.invivo.junit.SimpleExampleTest")
	public int multiply(int x, int y)
	{
		return x * y;
	}
	public static void main(String[] args) {
		System.out.println("3 x 4 = " + new SimpleExample().multiply(3, 4));
	}
}
