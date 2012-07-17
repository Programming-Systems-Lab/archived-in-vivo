package edu.columbia.cs.psl.invivo.example;

public class EvilFactory {
	public static C getC()
	{
		if(Math.random() < .5)
			return new Foo();
		else
			return new Bar();
	}
}
