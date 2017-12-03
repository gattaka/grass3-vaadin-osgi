package cz.gattserver.grass3.articles.plugins.basic.style;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ThemeResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class MonospacePluginTest {

	@Test
	public void testProperties() {
		MonospacePlugin plugin = new MonospacePlugin();
		assertEquals("MONSPC", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertTrue(to.getImage() instanceof ThemeResource);
		assertEquals("articles/basic/img/mono_16.png", ((ThemeResource) to.getImage()).getResourceId());
		assertEquals("[MONSPC]", to.getPrefix());
		assertEquals("[/MONSPC]", to.getSuffix());
		assertEquals("MONSPC", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
