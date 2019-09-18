package cz.gattserver.grass3.articles.plugins.basic.image;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class ImagePluginTest {

	@Test
	public void testProperties() {
		ImagePlugin plugin = new ImagePlugin();
		assertEquals("IMG", plugin.getTag());
		assertEquals(ImageParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Obrázek", to.getDescription());
		assertEquals("[IMG]", to.getPrefix());
		assertEquals("[/IMG]", to.getSuffix());
		assertEquals("IMG", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
