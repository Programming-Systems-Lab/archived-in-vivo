package edu.columbia.cs.psl.invivo.example;

public class Bar implements C{
	private static String bad;
	public Foo foo = new Foo();
	public String result;
	public void evil()
	{
		result = foo.result;
//		C evil = EvilFactory.getC();
		EvilFactory.getC().doSomething(this);
	}
	@Override
	public void doSomething(Bar b) {
		//Does nothing.
	}
}
