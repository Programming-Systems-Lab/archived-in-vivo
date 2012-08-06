/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package examples;

import java.util.ListResourceBundle;
import java.util.prefs.BackingStoreException;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class Java_Util_Logging_LogManager_Benchmarks extends SimpleBenchmark {

	public int timeGetLoggerNames(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			java.util.logging.LogManager.getLogManager().addLogger(
					java.util.logging.Logger.getLogger("List"));
			dummy += java.util.logging.LogManager.getLogManager()
					.getLoggerNames().toString().length();
		}
		return dummy;
	}

	public static void main(String[] args) throws Exception {
		Runner.main(Java_Util_Logging_LogManager_Benchmarks.class, args);
	}
}
