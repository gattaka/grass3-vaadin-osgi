package cz.gattserver.grass3.articles.editor.parser.interfaces;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;

public class EditorButtonResourcesTOBuilderTest {

	@Test
	public void testCreateShort() {
		EditorButtonResourcesTO to = new EditorButtonResourcesTOBuilder("tag", "tagFamily").build();
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("tag", to.getDescription());
		assertEquals("[tag]", to.getPrefix());
		assertEquals("[/tag]", to.getSuffix());
		assertNull(to.getImage());
	}

	@Test
	public void testCreateShortAndImprove() {
		EditorButtonResourcesTOBuilder builder = new EditorButtonResourcesTOBuilder("tag", "tagFamily");

		EditorButtonResourcesTO to = builder.build();
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("tag", to.getDescription());
		assertEquals("[tag]", to.getPrefix());
		assertEquals("[/tag]", to.getSuffix());

		builder.setImageAsThemeResource("resource1");
		to = builder.build();

		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("resource1", ((ThemeResource) to.getImage()).getResourceId());

		builder.setImageResource(new ExternalResource("urlPath"));
		to = builder.build();

		assertTrue(to.getImage() instanceof ExternalResource);
		assertEquals("urlPath", ((ExternalResource) to.getImage()).getURL());
	}

	@Test
	public void testCreateLong() {
		EditorButtonResourcesTO to = new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description",
				"[tag][test][/test]Bla", "[/tag]", new ThemeResource("resourceId")).build();
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("description", to.getDescription());
		assertEquals("[tag][test][/test]Bla", to.getPrefix());
		assertEquals("[/tag]", to.getSuffix());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("resourceId", ((ThemeResource) to.getImage()).getResourceId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadPrefixTag() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[badTag]", "suffix",
				new ThemeResource("resourceId")).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadSuffixTag() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[tag]", "[/badTag]",
				new ThemeResource("resourceId")).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadPrefix() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "prefix", "suffix",
				new ThemeResource("resourceId")).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadSuffix() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[tag]", "suffix",
				new ThemeResource("resourceId")).build();
	}
}
