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
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.ListResourceBundle;
import java.util.prefs.BackingStoreException;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class Java_RMI_Registry_Benchmarks extends SimpleBenchmark {

	public int timeGetRegistry(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			try {
				dummy += java.rmi.registry.LocateRegistry.getRegistry().list().length;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dummy;
	}

	public int timeGetNamingList(int reps) {
		int dummy = 0;
		for (int i = 0; i < reps; i++) {
			try {
				dummy += java.rmi.Naming.list("").length;
			} catch (RemoteException | MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dummy;
	}

	public static void main(String[] args) throws Exception {
		Runner.main(Java_RMI_Registry_Benchmarks.class, args);
	}
}
