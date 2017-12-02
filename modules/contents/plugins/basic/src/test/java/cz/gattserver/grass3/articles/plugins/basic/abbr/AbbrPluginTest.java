package cz.gattserver.grass3.articles.plugins.basic.abbr;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class AbbrPluginTest {

	@Test
	public void testProperties() {
		AbbrPlugin plugin = new AbbrPlugin();
		assertEquals("ABBR", plugin.getTag());
		assertEquals(AbbrParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/abbr_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[ABBR]", to.getPrefix());
		assertEquals("[T][/T][/ABBR]", to.getSuffix());
		assertEquals("ABBR", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
