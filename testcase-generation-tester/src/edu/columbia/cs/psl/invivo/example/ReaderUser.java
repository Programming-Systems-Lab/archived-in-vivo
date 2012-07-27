package edu.columbia.cs.psl.invivo.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReaderUser {
	private void go() {
		try {
			BufferedReader r = new BufferedReader(new FileReader("in-vivo.log"));
			char[] buf = new char[1024];
			int charsRead = 0;
			charsRead = r.read(buf, 0, buf.length);
			System.out.println(buf);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
//	private static int proxy(BufferedReader r,char[] cbuf, int o, int n) throws IOException
//	{
//		return r.read(cbuf, o, n);
//	}
	public static void main(String[] args) throws Exception{
		new ReaderUser().go();
		/*for(Field f : Class.forName("edu.columbia.cs.psl.invivo.example.ReaderUserInvivoLog").getDeclaredFields())
		{
			if(!Modifier.isStatic(f.getModifiers()))
				continue;
			try {
				System.out.print(f.getName()+ "->");
				if(f.getType().isArray())
				{
					if(f.getType().getComponentType() == Integer.TYPE)
					{
						for(int i = 0; i < Array.getLength(f.get(null)); i++)
						{
							System.out.println(Array.get(f.get(null), i));
						}
					}
					if(f.getType().getComponentType().isPrimitive())
						System.out.println(f.get(null));
					else
						System.out.println(Arrays.deepToString((Object[]) f.get(null)));
				}
				else
					System.out.println(f.get(null));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
