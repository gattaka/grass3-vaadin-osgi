package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class StrongPluginTest {

	@Test
	public void testProperties() {
		StrongPlugin plugin = new StrongPlugin();
		assertEquals("STR", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/strong_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[STR]", to.getPrefix());
		assertEquals("[/STR]", to.getSuffix());
		assertEquals("STR", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
