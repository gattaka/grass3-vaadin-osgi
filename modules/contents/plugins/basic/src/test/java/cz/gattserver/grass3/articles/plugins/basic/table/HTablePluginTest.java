package cz.gattserver.grass3.articles.plugins.basic.table;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class HTablePluginTest {

	@Test
	public void testProperties() {
		HTablePlugin plugin = new HTablePlugin();
		assertEquals("HTABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/htbl_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[HTABLE]", to.getPrefix());
		assertEquals("[/HTABLE]", to.getSuffix());
		assertEquals("HTABLE", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
