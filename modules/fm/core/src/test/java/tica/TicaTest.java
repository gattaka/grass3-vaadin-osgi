package tica;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TicaTest {

	@Test
	public void test() {
		return;
	}

	public void tica() {

		// TODO applikovat detekci do FM

		InputStream is = null;

		File[] files = new File("C:\\Users\\Gattaka\\Downloads\\").listFiles();

		for (File file : files) {

			if (file.isDirectory())
				continue;

			try {
				is = new BufferedInputStream(new FileInputStream(file));
				// is = new BufferedInputStream(getClass().getResourceAsStream(
				// "want_some_fun.jpg"));

				Parser parser = new AutoDetectParser();
				ContentHandler handler = new BodyContentHandler(System.out);

				Metadata metadata = new Metadata();

				parser.parse(is, handler, metadata, new ParseContext());

				for (String name : metadata.names()) {
					String value = metadata.get(name);

					if (value != null) {
						System.out.println(file.getName());
						System.out.println(name + ": \t" + value);
						System.out.println();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TikaException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
