package cz.gattserver.grass3.articles.plugins.favlink.plugin;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ClassResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.web.common.ui.ImageIcon;

public class FavlinkPluginTest {

	@Test
	public void testProperties() {
		FavlinkPlugin plugin = new FavlinkPlugin();
		assertEquals("A", plugin.getTag());
		assertEquals(FavlinkParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Link", to.getDescription());
		assertTrue(to.getImage() instanceof ClassResource);
		assertEquals(((ClassResource) ImageIcon.GLOBE_16_ICON.createResource()).getFilename(),
				((ClassResource) to.getImage()).getFilename());
		assertEquals("[A]", to.getPrefix());
		assertEquals("[/A]", to.getSuffix());
		assertEquals("A", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
