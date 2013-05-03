package org.myftp.gattserver.grass3.pages.template;

import static org.junit.Assert.*;

import org.junit.Test;

public class GrassLayoutTest {

	@Test
	public void testLoadJSBatch() {

		String[] links = new String[] { "\"exmpl.js\"", "alert('ss')" };

		StringBuilder builder = new StringBuilder();
		GrassLayout.buildJSBatch(builder, 0, links);

		String result = builder.toString();
		assertEquals(
				"$.getScript(\"exmpl.js\", function(){$.getScript(alert('ss'), function(){});});",
				result);

	}

}
