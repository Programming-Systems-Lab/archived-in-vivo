package edu.columbia.cs.psl.invivo.nativedetector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

public class NativeRecursiveFinder {
//	1 - Construct a list of methods, A, that we need to find all calls of (taken from input initially)
//	2 - Find all invocations of each method A_i. Add each method that calls A_i to list A. Once you have found all of the locations that call A_i, remove A_i from the list A.
//	3 - TODO Continue until A is empty.
	
	public static void main(String[] args) throws IOException {
		NativeRecursiveFinder nrf = new NativeRecursiveFinder();
		nrf.getNativeMethods("nativeMethods.txt");
		System.out.println("done with nrf.main");
	}
	
	public String[] getNativeMethods(String inputLine) throws IOException{
		
		
		if (inputLine.endsWith(";"))
			inputLine = inputLine.substring(0, inputLine.length()-1);
		//System.out.println(inputLine);
		String[] inputPieces = inputLine.split("\t");
		String className = inputPieces[0];
		String methodName = inputPieces[1];
		String methodDesc = inputPieces[2];
		
//		Type.getReturnType(methodDesc).
		// TODO parse methodDesc    need "void println(String)"
//		Type[] params = Type.getArgumentTypes(methodDesc);
		
		String[] params = methodDesc.substring(methodDesc.indexOf("(")+1, methodDesc.lastIndexOf(")")).split(",");
		// System.out.println(params);
		Map<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("Z", "boolean");
		typeMap.put("B", "byte");
		typeMap.put("C", "char");
		typeMap.put("S", "short");
		typeMap.put("I", "int");
		typeMap.put("J", "long");
		typeMap.put("F", "float");
		typeMap.put("D", "double");
		typeMap.put("V", "void");
// TODO handle "Ljava/lang/String;"   			String
		// TODO handle "[I"        				int[]
		// TODO handle "[Ljava/lang/Object;"   	Object[]
		ArrayList<String> paramTypes = new ArrayList<String>(); 
		for (String param: params) {
		//	System.out.println(typeMap.get(param));
			paramTypes.add(typeMap.get(param));
		}
		String finalParams = "";
		//System.out.println("entering for loop");
		for( int i=0; i < paramTypes.size(); i++) {
			if (paramTypes.get(i) != null) {
				finalParams += paramTypes.get(i);
			}
			if (i > 0)
				finalParams += ", ";
			//System.out.println(finalParams);
		}
//		System.out.println(methodDesc.length());
		String returnType = methodDesc.substring(methodDesc.lastIndexOf(")")+1);
//		System.out.println(returnType);
		String oneWordType = null;
		oneWordType = typeMap.get(returnType);
		if (oneWordType == null) {
			String noTypeReturnType = returnType.substring(1);
			String[] typeParts = noTypeReturnType.split("/");
			oneWordType = typeParts[typeParts.length-1];
		}
		
	//	System.out.println(oneWordType);
	
		String targetMethodDeclaration = oneWordType+" "+methodName+"("+finalParams+")";
	//	System.out.println(targetMethodDeclaration);
		
		InvocationFinder ifinder = new InvocationFinder();
	//	ifinder.runAndWrite(NativeSearcher.jarPath, "java/io/PrintStream", "void println(String)", "invocationsprintln", true, false);
		ifinder.runAndWrite(NativeSearcher.jarPath, className, targetMethodDeclaration, "invocations", true, false);
		
		// TODO find all methods that call className/methodName/methodDesc
		
	/*	for (String inputPiece: inputPieces) {
			System.out.println(inputPiece);
		}*/
		
		//TODO
		
		
		return null;
		
	}
}
