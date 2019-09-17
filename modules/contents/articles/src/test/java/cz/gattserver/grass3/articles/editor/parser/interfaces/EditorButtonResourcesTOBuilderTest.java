package cz.gattserver.grass3.articles.editor.parser.interfaces;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;

public class EditorButtonResourcesTOBuilderTest {

	private StreamResource createDummyResource() {
		return new StreamResource("resourceId", new InputStreamFactory() {
			private static final long serialVersionUID = 3561448763534828856L;

			@Override
			public InputStream createInputStream() {
				return null;
			}
		});
	}

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
	}

	@Test
	public void testCreateLong() {
		EditorButtonResourcesTO to = new EditorButtonResourcesTOBuilder("tag", "tagFamily")
				.setDescription("description").setPrefix("[tag][test][/test]Bla").setSuffix("[/tag]").build();
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("description", to.getDescription());
		assertEquals("[tag][test][/test]Bla", to.getPrefix());
		assertEquals("[/tag]", to.getSuffix());
	}

	@Test
	public void testCreateChain() {
		EditorButtonResourcesTO to = new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description",
				"[tag][test][/test]Bla", "[/tag]", createDummyResource()).build();
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("description", to.getDescription());
		assertEquals("[tag][test][/test]Bla", to.getPrefix());
		assertEquals("[/tag]", to.getSuffix());
		assertEquals("resourceId", to.getImage().getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadPrefixTag() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[badTag]", "suffix",
				createDummyResource()).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadSuffixTag() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[tag]", "[/badTag]",
				createDummyResource()).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadPrefix() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "prefix", "suffix", createDummyResource())
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadSuffix() {
		new EditorButtonResourcesTOBuilder("tag", "tagFamily", "description", "[tag]", "suffix", createDummyResource())
				.build();
	}
}
