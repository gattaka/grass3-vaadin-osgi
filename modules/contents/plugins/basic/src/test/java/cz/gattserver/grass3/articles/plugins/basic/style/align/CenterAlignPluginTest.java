package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class CenterAlignPluginTest {

	@Test
	public void testProperties() {
		CenterAlignPlugin plugin = new CenterAlignPlugin();
		assertEquals("ALGNCT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[ALGNCT]", to.getPrefix());
		assertEquals("[/ALGNCT]", to.getSuffix());
		assertEquals("ALGNCT", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
