package cz.gattserver.grass3.articles.plugins.basic.style.color;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;

public class RedPluginTest {

	@Test
	public void testProperties() {
		RedPlugin plugin = new RedPlugin();
		assertEquals("RED", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Červeně", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[RED]", to.getPrefix());
		assertEquals("[/RED]", to.getSuffix());
		assertEquals("RED", to.getTag());
		assertEquals("Obarvení", to.getTagFamily());
	}

}
