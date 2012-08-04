package edu.columbia.cs.psl.invivo.example;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReaderUser extends FilterInputStream {
	protected ReaderUser(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	int x =0;
	@Override
	public int read(byte[] b) throws IOException {
		x++;
		return super.read(b);
	}
	private void go() throws Exception{
			int c = 0;
			int d = 0;
			int[] x = new int[4];
			BufferedReader r = new BufferedReader(new FileReader("in-vivo.log"));
			char[] buf = new char[2];
			int zz = read(new byte[4]);
			System.out.println("zz: " + zz);
			int charsRead = 0;
			charsRead = r.read(buf, 0, buf.length);
			System.out.println(charsRead);
			System.out.println(buf);
			while(charsRead > 0)
			{
				charsRead = r.read(buf, 0, buf.length);
				System.out.println(charsRead);
				System.out.println(buf);
				Thread.sleep(100);
			}
			c++;
			System.out.println(c);
			d++;
			x[2]=2;
			
		
		throw new Exception("AHHHH i CRASHED");
	}
//	private static int proxy(BufferedReader r,char[] cbuf, int o, int n) throws IOException
//	{
//		return r.read(cbuf, o, n);
//	}
	public static void main(String[] args) throws Exception{
		new ReaderUser(new FileInputStream("in-vivo.log")).go();
	}
}
