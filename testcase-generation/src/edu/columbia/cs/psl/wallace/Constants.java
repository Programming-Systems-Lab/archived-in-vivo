package edu.columbia.cs.psl.wallace;

public interface Constants {

	public static int DEFAULT_LOG_SIZE = 2000;
	public static int MAX_LOG_SIZE = 40000000;
	public static int VERY_MAX_LOG_SIZE = 400000000;

	public static double LOG_GROWTH_RATE = 2.5;
	public static String REPLAY_CLASS_SUFFIX = "InvivoReplay";
	public static String INNER_COPY_METHOD_NAME = "_Invivo___copy";
	public static String OUTER_COPY_METHOD_NAME = "_Invivo_copy";
	public static String SET_FIELDS_METHOD_NAME = "_Invivo_set_fields";
}
