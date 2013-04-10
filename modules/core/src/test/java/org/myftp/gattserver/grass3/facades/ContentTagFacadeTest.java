package org.myftp.gattserver.grass3.facades;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.myftp.gattserver.grass3.facades.impl.ContentTagFacadeImpl;
import org.myftp.gattserver.grass3.test.BaseSpringTest;

public class ContentTagFacadeTest extends BaseSpringTest {

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;
	private String delimiter = ContentTagFacadeImpl.TAGS_DELIMITER;

	private Set<String> parseTags(String tagNames) {
		String[] tags = contentTagFacade.parseTags(tagNames);
		Set<String> nonEmptyTags = new HashSet<String>();
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
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa"));
		assertTrue(tags.contains("beta"));
	}

	@Test
	public void parseTags2() {
		String tagNames = "alfa " + delimiter + "beta";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa"));
		assertTrue(tags.contains("beta"));
	}

	@Test
	public void parseTags3() {
		String tagNames = "alfa " + delimiter + " beta";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa"));
		assertTrue(tags.contains("beta"));
	}

	@Test
	public void parseTags4() {
		String tagNames = "alfa" + delimiter + " beta";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa"));
		assertTrue(tags.contains("beta"));
	}

	@Test
	public void parseTags5() {
		String tagNames = "alfa     " + delimiter + "    beta";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa"));
		assertTrue(tags.contains("beta"));
	}

	@Test
	public void parseTags6() {
		String tagNames = "alfa 01  " + delimiter + " Z beta 321 G";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa 01"));
		assertTrue(tags.contains("Z beta 321 G"));

	}

	@Test
	public void parseTags7() {
		String tagNames = " alfa 01  " + delimiter + " Z beta 321 G ";
		Set<String> tags = parseTags(tagNames);
		assertTrue(tags.contains("alfa 01"));
		assertTrue(tags.contains("Z beta 321 G"));

	}

	@Test
	public void serializeTags() {

		String[] tags = { "tag1", "tag2", "last tag" };
		String tagNames = contentTagFacade.serializeTags(new HashSet<String>(
				Arrays.asList(tags)));
		Set<String> tagsBack = parseTags(tagNames);
		for (String tag : tags) {
			assertTrue(tagsBack.contains(tag));
		}

	}
}
