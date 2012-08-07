package edu.columbia.cs.psl.invivo.example;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class CopyTester {
	private Bar bar = null;
	private String string = "orig string";
	private int num = 3;

	public static void main(String[] args) {
		CopyTester ct = new CopyTester();
		ct.go();
		/*for (Field f : CopyTester.class.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;
			try {
				System.out.print(f.getName() + "->");
				if (f.getType().isArray()) {
					if (f.getType().getComponentType().isPrimitive())
						System.out.println(f.get(ct));
					else
						System.out.println(Arrays.deepToString((Object[]) f
								.get(ct)));
				} else
					System.out.println(f.get(ct));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}*/
	}

	private void go() {
		this.bar = new Bar();
		this.string = "xx";
		this.num = 3;
	}
}
