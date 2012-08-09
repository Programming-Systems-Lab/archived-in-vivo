package edu.columbia.cs.psl.invivo.example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.EventListener;
import java.util.Scanner;

import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.View;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyTester {

	private static byte[] getByteArray(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return str.getBytes();
		}
	}	
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter your input: ");
		String rawText = scanner.nextLine();/*"HTTP/1.1 200 OK\r\n"+
	            "Content-Type: text/html\r\n"+
	            "Content-Length: 22\r\n"+
	            "\r\n";*/

		ByteArrayBuffer buf = new ByteArrayBuffer(getByteArray(rawText));
		View view = new View(buf);
		HttpParser parser = new HttpParser(view, new HttpParser.EventHandler() {			
			@Override
	        public void startRequest(Buffer method, Buffer url, Buffer version) throws IOException {}

	        @Override
	        public void startResponse(Buffer version, int status, Buffer reason) throws IOException {}
	        
	        @Override
	        public void parsedHeader(Buffer name, Buffer value) throws IOException {}

	        @Override
	        public void headerComplete() throws IOException {}

	        @Override
	        public void messageComplete(long contextLength) throws IOException {}
	        
	        @Override
	        public void content(Buffer ref) throws IOException {}

		});
		
		try {
			parser.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
