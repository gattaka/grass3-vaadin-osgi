package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class ItalicPluginTest {

	@Test
	public void testProperties() {
		ItalicPlugin plugin = new ItalicPlugin();
		assertEquals("EM", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[EM]", to.getPrefix());
		assertEquals("[/EM]", to.getSuffix());
		assertEquals("EM", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
