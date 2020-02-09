package cz.gattserver.grass3.articles.plugins.code;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class BASHCodePluginTest {

	@Test
	public void testProperties() {
		BASHCodePlugin plugin = new BASHCodePlugin();
		assertEquals("BASH", plugin.getTag());
		assertEquals(CodeParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("BASH", to.getDescription());
		assertEquals("[BASH]", to.getPrefix());
		assertEquals("[/BASH]", to.getSuffix());
		assertEquals("BASH", to.getTag());
		assertEquals("Code highlight", to.getTagFamily());
	}

}
