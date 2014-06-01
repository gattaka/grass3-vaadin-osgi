package org.myftp.gattserver.grass3.monitor.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Console {

	public static String executeCommand(String... commandAndArguments) {

		File dummyInput = null;
		try {
			dummyInput = File.createTempFile(String.valueOf(System.currentTimeMillis()), "GRASS-CONSOLE-DUMMY-INPUT");
		} catch (IOException e1) {
			e1.printStackTrace();
			return "ERORR during creation of dummy input file";
		}

		try {
			ProcessBuilder pb = new ProcessBuilder(commandAndArguments);

			pb.redirectInput(dummyInput);

			Process process = pb.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		} catch (IOException e) {
			return "ERROR occurred during command execution: " + e.getMessage();
		} finally {
			dummyInput.delete();
		}

	}
}
