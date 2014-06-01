package org.myftp.gattserver.grass3.monitor.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Console {

	public static void main(String[] args) throws IOException {

		// String cmd = "ssh gattaka@gattserver.myfp.org";
		String cmd = "cmd";

		Process process = new ProcessBuilder(cmd).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		System.out.printf("Output of running %s is:", cmd);

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

	}

}
