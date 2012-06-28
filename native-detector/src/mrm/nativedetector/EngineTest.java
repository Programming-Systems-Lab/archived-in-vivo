package mrm.nativedetector;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class EngineTest {
	public static int methodCount = 0;
	private static Logger logger = Logger.getLogger(EngineTest.class);
	
	public static void main(String[] args) {
		Engine eng = new Engine("/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Classes/classes.jar");
		
		try {
			ArrayList<String> openClasses = eng.getClassNames();
			int classCount = openClasses.size();
			
			
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(methodCount + " methods");
		return;
	}
}
