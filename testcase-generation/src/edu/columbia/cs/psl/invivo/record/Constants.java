package edu.columbia.cs.psl.invivo.record;

public interface Constants {
	public static String BEEN_CLONED_PREFIX = "__beenCloned_";
	public static String PREV_VALUE_PREFIX = "__origValue_";
//	public static String LOGGED_CALL_PREFIX = "__loggedValueAt_";

	public static String LOG_DUMP_CLASS = "edu/columbia/cs/psl/invivo/record/Log";
	public static String LOG_REPLAY_CLASS = "edu/columbia/cs/psl/invivo/record/ExportedLog";

	public static int DEFAULT_LOG_SIZE = 1000;
	public static int MAX_LOG_SIZE = 50000;
	public static double LOG_GROWTH_RATE = 1.5;
	public static String REPLAY_CLASS_SUFFIX = "InvivoReplay";
	public static String INNER_COPY_METHOD_NAME = "_Invivo___copy";
	public static String OUTER_COPY_METHOD_NAME = "_Invivo_copy";
	public static String SET_FIELDS_METHOD_NAME = "_Invivo_set_fields";
}
