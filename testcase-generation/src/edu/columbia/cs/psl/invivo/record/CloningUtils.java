package edu.columbia.cs.psl.invivo.record;

import java.util.IdentityHashMap;

import com.rits.cloning.Cloner;

public class CloningUtils {
	public static Cloner cloner = new Cloner();
	public static IdentityHashMap<Object, Object> cloneCache = new IdentityHashMap<Object, Object>();;
}
