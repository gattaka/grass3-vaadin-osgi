package cz.gattserver.grass3.articles.plugins.basic.table;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class TablePluginTest {

	@Test
	public void testProperties() {
		TablePlugin plugin = new TablePlugin();
		assertEquals("TABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[TABLE]", to.getPrefix());
		assertEquals("[/TABLE]", to.getSuffix());
		assertEquals("TABLE", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
