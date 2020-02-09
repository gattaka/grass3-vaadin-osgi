package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class UnderlinePluginTest {

	@Test
	public void testProperties() {
		UnderlinePlugin plugin = new UnderlinePlugin();
		assertEquals("UND", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[UND]", to.getPrefix());
		assertEquals("[/UND]", to.getSuffix());
		assertEquals("UND", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
