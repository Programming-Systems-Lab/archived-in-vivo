import java.io.*;
import java.util.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import output.JavaLexer;
import output.JavaParser;

public class Analyzer {

	public static boolean debug = true;

	public static void main(String[] args) {
		try {
			BufferedWritier writer = new BufferedWriter( new FileWriter( args[ 0 ] ) );

			for ( int i = 1; i < args.length; i++ ) {
				CharStream cs = new ANTLRFileStream(args[i]);      	
				JavaLexer lexer = new JavaLexer(cs);
										    
				CommonTokenStream tokens = new CommonTokenStream();
				tokens.setTokenSource(lexer);
										    
				JavaParser parser = new JavaParser(tokens);
										    
				JavaParser.program_return r = parser.program();
				CommonTree t = (CommonTree)r.getTree();

				printMemberData(t);
			}
		}
		catch (IOException e) {
			System.out.println(Error getting data from file:  + e.getMessage());
			System.exit(1);
		}
		catch (RecognitionException e) {
			System.out.println(Recognition Exception received);
			System.exit(2);
		}

	}

	public static void printMemberData(CommonTree t) {
		
	}

}
