package org.myftp.gattserver.grass3.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringPreviewCreatorTest {

	@Test
	public void test() {
		assertEquals("ta", StringPreviewCreator.createPreview("ta", 4));
		assertEquals("text", StringPreviewCreator.createPreview("text", 4));
		assertEquals("t...", StringPreviewCreator.createPreview("texty", 4));
		assertEquals("texty...", StringPreviewCreator.createPreview("texty jsou dlouhé", 8));
	}

}
