package edu.columbia.cs.psl.invivo.bench;

import java.io.File;

import com.rits.cloning.Cloner;

public class Benchmark {
	private static Cloner cloner = new Cloner();
	public static void main(String[] args) {
		
//		for(int i = 0; i< 35; i++)
//		{
//			System.out.println("if(annoying"+i+" != null) ret.annoying"+i+" = annoying"+i+"._copy();");
//		}
//		System.exit(0);
		for(int i = 0; i < 5; i++)
		{
			SomeOtherObject o = generateObjects();
			long start  = System.currentTimeMillis();
			SomeOtherObject o2 = o.copy();
			long end = System.currentTimeMillis();
			System.out.println("Static: " + (end - start));
			o = null;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i = 0; i < 5; i++)
		{
			SomeOtherObject o = generateObjects();
			long start  = System.currentTimeMillis();
			SomeOtherObject o2 = cloner.deepClone(o);
			long end = System.currentTimeMillis();
			System.out.println("Deep: " + (end - start));
			o = null;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static SomeOtherObject generateObjects() {
		SomeOtherObject n = new SomeOtherObject(new File("asfd"), new ComplexObject(), new SomeOtherObject(), "asdf", 3);
//		n.setOtherObject(n);
//		SomeOtherObject n2 = new SomeOtherObject(n.getF(), n.getO(), null, "aslfdjsaf", 4);
//		n.getO().setChildren2(new ArrayList<ComplexObject>());
//		for(int i = 0; i< 1000; i++)
//		{
//			ComplexObject oo = new ComplexObject();
//			oo.setChildren(new ComplexObject[50]);
//			oo.setChildren2(new ArrayList<ComplexObject>());
//			n.getO().getChildren2().add(oo);
//			oo.annoying0 = new ComplexObject();
//			oo.annoying1 = new ComplexObject();
//			oo.annoying2 = new ComplexObject();
//			oo.annoying3 = new ComplexObject();
//			oo.annoying4 = new ComplexObject();
//			oo.annoying5 = new ComplexObject();
//			oo.annoying6 = new ComplexObject();
//			oo.annoying7 = new ComplexObject();
//			oo.annoying8 = new ComplexObject();
//			oo.annoying9 = new ComplexObject();
//			oo.annoying10 = new ComplexObject();
//			oo.annoying11 = new ComplexObject();
//			oo.annoying12 = new ComplexObject();
//			oo.annoying13 = new ComplexObject();
//			oo.annoying14 = new ComplexObject();
//			oo.annoying15 = new ComplexObject();
//			oo.annoying16 = new ComplexObject();
//			oo.annoying17 = new ComplexObject();
//			oo.annoying18 = new ComplexObject();
//			oo.annoying19 = new ComplexObject();
//			oo.annoying20 = new ComplexObject();
//			oo.annoying21 = new ComplexObject();
//			oo.annoying22 = new ComplexObject();
//			oo.annoying23 = new ComplexObject();
//			oo.annoying24 = new ComplexObject();
//			oo.annoying25 = new ComplexObject();
//			oo.annoying26 = new ComplexObject();
//			oo.annoying27 = new ComplexObject();
//			oo.annoying28 = new ComplexObject();
//			oo.annoying29 = new ComplexObject();
//			oo.annoying30 = new ComplexObject();
//			oo.annoying31 = new ComplexObject();
//			oo.annoying32 = new ComplexObject();
//			oo.annoying33 = new ComplexObject();
//			oo.annoying34 = new ComplexObject();
//
//			for(int j = 0;j<50;j++)
//			{
//				oo.getChildren()[j] = new ComplexObject(null, new ArrayList<ComplexObject>(), oo, "asdf", n2);
//				oo.getChildren2().add(oo.getChildren()[j]);
//			}
//			oo.setSoo(n);
//		}
		return n;
	}
}
