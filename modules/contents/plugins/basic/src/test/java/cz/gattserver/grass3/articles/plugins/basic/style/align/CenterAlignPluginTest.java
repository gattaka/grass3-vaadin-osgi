package cz.gattserver.grass3.articles.plugins.basic.style.align;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class CenterAlignPluginTest {

	@Test
	public void testProperties() {
		CenterAlignPlugin plugin = new CenterAlignPlugin();
		assertEquals("ALGNCT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[ALGNCT]", to.getPrefix());
		assertEquals("[/ALGNCT]", to.getSuffix());
		assertEquals("ALGNCT", to.getTag());
		assertEquals("Zarovnání", to.getTagFamily());
	}

}
