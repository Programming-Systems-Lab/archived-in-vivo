package edu.columbia.cs.psl.invivo.record;

public interface Constants {
	public static String BEEN_CLONED_PREFIX = "__beenCloned_";
	public static String PREV_VALUE_PREFIX = "__origValue_";
	public static String LOGGED_CALL_PREFIX = "__loggedValueAt_";
	public static String ARRAY_INIT_METHOD = "__initInvivoLogArrays";
	public static String STATIC_ARRAY_INIT_METHOD = "__initInvivoLogArraysStatic";
	public static String LOG_DUMP_CLASS = "edu/columbia/cs/psl/invivo/record/Log";
	public static int DEFAULT_LOG_SIZE = 20;
	public static double LOG_GROWTH_RATE = 1.5;
}
