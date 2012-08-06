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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ListResourceBundle;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class Java_Util_Scanner_Benchmarks extends SimpleBenchmark {

	public int timeScannerNext(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			try {
				File file = new File("/home/nikhil/in-vivo/hs_err_pid4595.log");
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					System.out.println(scanner.next());
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return dummy;
	}

	public int timeScannerNextPattern(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			try {
				File file = new File("/home/nikhil/in-vivo/hs_err_pid4595.log");
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					System.out.println(scanner.next("*"));
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return dummy;
	}

	public int timeScannerNextLine(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			try {
				File file = new File("/home/nikhil/in-vivo/hs_err_pid4595.log");
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					System.out.println(scanner.nextLine());
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return dummy;
	}

	public static void main(String[] args) throws Exception {
		Runner.main(Java_Util_Scanner_Benchmarks.class, args);
	}
}
