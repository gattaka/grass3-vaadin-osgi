package cz.gattserver.grass3.articles.plugins.basic.table;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class TablePluginTest {

	@Test
	public void testProperties() {
		TablePlugin plugin = new TablePlugin();
		assertEquals("TABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/tbl_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[TABLE]", to.getPrefix());
		assertEquals("[/TABLE]", to.getSuffix());
		assertEquals("TABLE", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
