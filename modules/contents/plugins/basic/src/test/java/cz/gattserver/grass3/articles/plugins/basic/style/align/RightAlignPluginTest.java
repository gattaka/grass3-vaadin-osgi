package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class RightAlignPluginTest {

	@Test
	public void testProperties() {
		RightAlignPlugin plugin = new RightAlignPlugin();
		assertEquals("ALGNRT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[ALGNRT]", to.getPrefix());
		assertEquals("[/ALGNRT]", to.getSuffix());
		assertEquals("ALGNRT", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
