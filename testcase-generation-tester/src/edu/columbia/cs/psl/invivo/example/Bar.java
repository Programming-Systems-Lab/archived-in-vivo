package edu.columbia.cs.psl.invivo.example;

public class Bar implements C{
	private static String bad;
	public Foo foo = new Foo();
	public void evil()
	{
		foo.result = "yy";
		C evil = EvilFactory.getC();
		evil.doSomething(this);
	}
	@Override
	public void doSomething(Bar b) {
		//Does nothing.
	}
}
