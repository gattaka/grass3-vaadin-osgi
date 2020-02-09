package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class SupPluginTest {

	@Test
	public void testProperties() {
		SupPlugin plugin = new SupPlugin();
		assertEquals("SUP", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Sup", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[SUP]", to.getPrefix());
		assertEquals("[/SUP]", to.getSuffix());
		assertEquals("SUP", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
