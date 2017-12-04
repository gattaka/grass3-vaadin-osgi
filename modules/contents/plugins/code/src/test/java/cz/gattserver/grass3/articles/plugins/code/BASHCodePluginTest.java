package cz.gattserver.grass3.articles.plugins.code;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class BASHCodePluginTest {

	@Test
	public void testProperties() {
		BASHCodePlugin plugin = new BASHCodePlugin();
		assertEquals("BASH", plugin.getTag());
		assertEquals(CodeParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("BASH", to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/code/img/bash.gif", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[BASH]", to.getPrefix());
		assertEquals("[/BASH]", to.getSuffix());
		assertEquals("BASH", to.getTag());
		assertEquals("Code highlight", to.getTagFamily());
	}

}
