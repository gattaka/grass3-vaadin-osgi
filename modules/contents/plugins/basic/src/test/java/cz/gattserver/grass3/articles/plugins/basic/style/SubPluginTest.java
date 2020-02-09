package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class SubPluginTest {

	@Test
	public void testProperties() {
		SubPlugin plugin = new SubPlugin();
		assertEquals("SUB", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Sub", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[SUB]", to.getPrefix());
		assertEquals("[/SUB]", to.getSuffix());
		assertEquals("SUB", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
