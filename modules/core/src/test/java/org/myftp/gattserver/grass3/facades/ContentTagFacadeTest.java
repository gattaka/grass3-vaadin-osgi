package org.myftp.gattserver.grass3.facades;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContentTagFacadeTest {

	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;
	private String delimiter = ContentTagFacade.TAGS_DELIMITER;

	@Test
	public void parseTags1() {
		String tagNames = "alfa" + delimiter + "beta";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa");
		assertEquals(tags[1], "beta");
	}

	@Test
	public void parseTags2() {
		String tagNames = "alfa " + delimiter + "beta";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa");
		assertEquals(tags[1], "beta");
	}

	@Test
	public void parseTags3() {
		String tagNames = "alfa " + delimiter + " beta";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa");
		assertEquals(tags[1], "beta");
	}

	@Test
	public void parseTags4() {
		String tagNames = "alfa" + delimiter + " beta";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa");
		assertEquals(tags[1], "beta");
	}

	@Test
	public void parseTags5() {
		String tagNames = "alfa     " + delimiter + "    beta";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa");
		assertEquals(tags[1], "beta");
	}

	@Test
	public void parseTags6() {
		String tagNames = "alfa 01  " + delimiter + " Z beta 321 G";
		String[] tags = contentTagFacade.parseTags(tagNames);
		assertEquals(tags[0], "alfa 01");
		assertEquals(tags[1], "Z beta 321 G");

	}

	@Test
	public void serializeTags() {

		String[] tags = { "tag1", "tag2", "last tag" };
		String tagNames = contentTagFacade.serializeTags(tags);
		assertEquals("tag1, tag2, last tag", tagNames);

	}

}
