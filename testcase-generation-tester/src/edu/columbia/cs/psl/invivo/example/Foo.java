package edu.columbia.cs.psl.invivo.example;

public class Foo implements C{
	public String result = "xx";
	@Override
	public void doSomething(Bar b) {
		b.foo = new Foo();
	}
}
