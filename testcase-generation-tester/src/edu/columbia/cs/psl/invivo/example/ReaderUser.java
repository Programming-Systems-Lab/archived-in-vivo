package edu.columbia.cs.psl.invivo.example;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReaderUser {
	private void go() throws Exception{
		try {
			int c = 0;
			int d = 0;
			int[] x = new int[4];
			BufferedReader r = new BufferedReader(new FileReader("in-vivo.log"));
			char[] buf = new char[1024];
			int charsRead = 0;
			charsRead = r.read(buf, 0, buf.length);
			System.out.println(charsRead);
			System.out.println(buf);
			c++;
			System.out.println(c);
			d++;
			x[2]=2;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		throw new Exception("AHHHH i CRASHED");
	}
//	private static int proxy(BufferedReader r,char[] cbuf, int o, int n) throws IOException
//	{
//		return r.read(cbuf, o, n);
//	}
	public static void main(String[] args) throws Exception{
		new ReaderUser().go();
	}
}
