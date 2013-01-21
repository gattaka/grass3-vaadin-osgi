package org.myftp.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ContentTagFacadeTest {

	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;
	private String delimiter = ContentTagFacade.TAGS_DELIMITER;

	private List<String> parseTags(String tagNames) {
		String[] tags = contentTagFacade.parseTags(tagNames);
		List<String> nonEmptyTags = new ArrayList<String>();
		for (String tag : tags) {
			if (tag.isEmpty())
				continue;
			nonEmptyTags.add(tag);
		}
		return nonEmptyTags;
	}

	@Test
	public void parseTags1() {
		String tagNames = "alfa" + delimiter + "beta";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa");
		assertEquals(tags.get(1), "beta");
	}

	@Test
	public void parseTags2() {
		String tagNames = "alfa " + delimiter + "beta";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa");
		assertEquals(tags.get(1), "beta");
	}

	@Test
	public void parseTags3() {
		String tagNames = "alfa " + delimiter + " beta";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa");
		assertEquals(tags.get(1), "beta");
	}

	@Test
	public void parseTags4() {
		String tagNames = "alfa" + delimiter + " beta";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa");
		assertEquals(tags.get(1), "beta");
	}

	@Test
	public void parseTags5() {
		String tagNames = "alfa     " + delimiter + "    beta";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa");
		assertEquals(tags.get(1), "beta");
	}

	@Test
	public void parseTags6() {
		String tagNames = "alfa 01  " + delimiter + " Z beta 321 G";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa 01");
		assertEquals(tags.get(1), "Z beta 321 G");

	}

	@Test
	public void parseTags7() {
		String tagNames = " alfa 01  " + delimiter + " Z beta 321 G ";
		List<String> tags = parseTags(tagNames);
		assertEquals(tags.get(0), "alfa 01");
		assertEquals(tags.get(1), "Z beta 321 G");

	}

	@Test
	public void serializeTags() {

		String[] tags = { "tag1", "tag2", "last tag" };
		String tagNames = contentTagFacade.serializeTags(tags);
		assertEquals("tag1, tag2, last tag", tagNames);

	}

}
