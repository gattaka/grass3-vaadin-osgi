package url.encode;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.junit.Test;

public class URLencodeTest {

	@Test
	public void test() {

		String testURL = "Žluťoučký kůň úpěl v novém adresáři/aasss";
		try {
			String encoded = URLEncoder.encode(testURL, "UTF-8");
			System.out.println("Encoded: '" + encoded + "'");
			String decoded = URLDecoder.decode(encoded, "UTF-8");
			assertEquals(testURL, decoded);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void test2() throws UnsupportedEncodingException {

		String encoded = "Å½luÅ¥ouÄkÃ½%20adresÃ¡Å";

		byte[] utf8 = new String(encoded.getBytes(), "ISO-8859-1")
				.getBytes("UTF-8");
		String decoded = new String(utf8);
		
		System.out.println(decoded);
	}

}
