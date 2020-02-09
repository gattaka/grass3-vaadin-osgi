package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class CrossoutPluginTest {

	@Test
	public void testProperties() {
		CrossoutPlugin plugin = new CrossoutPlugin();
		assertEquals("CROSS", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[CROSS]", to.getPrefix());
		assertEquals("[/CROSS]", to.getSuffix());
		assertEquals("CROSS", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
