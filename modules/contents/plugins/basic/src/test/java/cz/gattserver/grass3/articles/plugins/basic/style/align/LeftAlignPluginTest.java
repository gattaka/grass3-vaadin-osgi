package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class LeftAlignPluginTest {

	@Test
	public void testProperties() {
		LeftAlignPlugin plugin = new LeftAlignPlugin();
		assertEquals("ALGNLT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[ALGNLT]", to.getPrefix());
		assertEquals("[/ALGNLT]", to.getSuffix());
		assertEquals("ALGNLT", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
