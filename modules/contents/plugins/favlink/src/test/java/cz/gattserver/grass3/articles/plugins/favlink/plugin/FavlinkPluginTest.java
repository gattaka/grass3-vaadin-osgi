package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.test.AbstractContextAwareTest;

public class FavlinkPluginTest extends AbstractContextAwareTest {

	@Test
	public void testProperties() {
		FavlinkPlugin plugin = new FavlinkPlugin();
		assertEquals("A", plugin.getTag());
		assertEquals(FavlinkParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Link", to.getDescription());
		assertEquals("[A]", to.getPrefix());
		assertEquals("[/A]", to.getSuffix());
		assertEquals("A", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
