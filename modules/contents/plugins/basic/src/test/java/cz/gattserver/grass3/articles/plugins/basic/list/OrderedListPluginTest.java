package cz.gattserver.grass3.articles.plugins.basic.list;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class OrderedListPluginTest {

	@Test
	public void testProperties() {
		UnorderedListPlugin plugin = new UnorderedListPlugin();
		assertEquals("UL", plugin.getTag());
		assertEquals(ListParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/ul_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[UL]", to.getPrefix());
		assertEquals("[/UL]", to.getSuffix());
		assertEquals("UL", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
