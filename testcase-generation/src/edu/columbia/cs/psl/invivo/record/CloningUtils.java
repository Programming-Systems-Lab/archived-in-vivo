package edu.columbia.cs.psl.invivo.record;

import java.util.IdentityHashMap;

import com.rits.cloning.Cloner;

public class CloningUtils {
	public static boolean CATCH_ALL_ERRORS = true;

	static
	{
		if(CATCH_ALL_ERRORS)
		{
			Thread.setDefaultUncaughtExceptionHandler(new WallaceUncaughtExceptionHandler());
		}
	}
	public static Cloner cloner = new Cloner();
	public static IdentityHashMap<Object, Object> cloneCache = new IdentityHashMap<Object, Object>();;
}
