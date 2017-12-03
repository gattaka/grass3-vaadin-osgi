package cz.gattserver.grass3.articles.plugins.basic.list;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class UnorderedListPluginTest {

	@Test
	public void testProperties() {
		OrderedListPlugin plugin = new OrderedListPlugin();
		assertEquals("OL", plugin.getTag());
		assertEquals(ListParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/ol_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[OL]", to.getPrefix());
		assertEquals("[/OL]", to.getSuffix());
		assertEquals("OL", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
