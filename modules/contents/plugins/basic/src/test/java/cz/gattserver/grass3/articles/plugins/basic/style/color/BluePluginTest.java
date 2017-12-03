package cz.gattserver.grass3.articles.plugins.basic.style.color;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class BluePluginTest {

	@Test
	public void testProperties() {
		BluePlugin plugin = new BluePlugin();
		assertEquals("BLU", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Modře", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[BLU]", to.getPrefix());
		assertEquals("[/BLU]", to.getSuffix());
		assertEquals("BLU", to.getTag());
		assertEquals("Obarvení", to.getTagFamily());
	}

}
