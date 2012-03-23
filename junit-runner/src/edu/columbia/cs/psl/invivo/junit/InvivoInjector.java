package edu.columbia.cs.psl.invivo.junit;




public class InvivoInjector {

	public void go(String[] args)
	{
		try {
			Class<?> c = Class.forName(args[0]);
			String[] args2 = new String[args.length-1];
			for(int i = 1; i<args.length;i++)
				args2[i-1] = args[i];
			c.getMethod("main", String[].class).invoke(null, (Object) args2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		if(args.length == 0)
		{
			System.err.println("Usage: java edu.columbia.cs.psl.invov.junit.InvivoInjector nameOfClassWithMain [Optional arguments for said class]");
			System.exit(0);
		}
		new InvivoInjector().go(args);
	}
}

