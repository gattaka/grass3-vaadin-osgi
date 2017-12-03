package cz.gattserver.grass3.articles.plugins.basic.image;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vaadin.server.ClassResource;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.web.common.ui.ImageIcon;

public class ImagePluginTest {

	@Test
	public void testProperties() {
		ImagePlugin plugin = new ImagePlugin();
		assertEquals("IMG", plugin.getTag());
		assertEquals(ImageParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Obrázek", to.getDescription());
		assertTrue(to.getImage() instanceof ClassResource);
		assertEquals(((ClassResource) ImageIcon.IMG_16_ICON.createResource()).getFilename(),
				((ClassResource) to.getImage()).getFilename());
		assertEquals("[IMG]", to.getPrefix());
		assertEquals("[/IMG]", to.getSuffix());
		assertEquals("IMG", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
