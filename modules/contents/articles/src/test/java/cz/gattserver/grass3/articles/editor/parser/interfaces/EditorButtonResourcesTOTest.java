package cz.gattserver.grass3.articles.editor.parser.interfaces;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

public class EditorButtonResourcesTOTest {

	@Test
	public void test() {
		EditorButtonResourcesTO to = new EditorButtonResourcesTO("tag", "tagFamily", "description", "prefix", "suffix",
				new ThemeResource("resourceId"));
		assertEquals("tag", to.getTag());
		assertEquals("tagFamily", to.getTagFamily());
		assertEquals("description", to.getDescription());
		assertEquals("prefix", to.getPrefix());
		assertEquals("suffix", to.getSuffix());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("resourceId", ((ThemeResource) to.getImage()).getResourceId());

		EditorButtonResourcesTO to2 = new EditorButtonResourcesTO("tag2", "tagFamily", "description", "prefix",
				"suffix", new ThemeResource("resourceId"));
		assertEquals(-1, to.compareTo(to2));

		EditorButtonResourcesTO to3 = new EditorButtonResourcesTO("tag", "tagFamilyX", "descriptionX", "prefixX",
				"suffixX", new ThemeResource("resourceIdX"));
		assertTrue(to.equals(to3));
		assertFalse(to2.equals(to3));
		assertFalse(to2.equals("wrongInstance"));
	}

}
