package edu.columbia.cs.psl.invivo.example;

public class RandomUser {
	public void makeRandom()
	{
		Math.random();
	}
	public void doTest()
	{
		for(int i = 0; i< 10000;i++)
		{
			long start = System.currentTimeMillis();
			for(int j = 0; j < 1000000; j++)
			{
				makeRandom();
			}
			long end = System.currentTimeMillis();
			System.out.println(i+"\t"+(end-start));
		}
	}
	public static void main(String[] args) {
		new RandomUser().doTest();
	}
}
