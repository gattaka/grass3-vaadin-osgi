package cz.gattserver.grass3.articles.plugins.headers;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class HeaderPluginTest {

	@Test
	public void testProperties() {
		List<AbstractHeaderPlugin> plugins = Arrays.asList(new Header1Plugin(), new Header2Plugin(),
				new Header3Plugin(), new Header4Plugin());
		for (int i = 1; i <= 4; i++) {
			AbstractHeaderPlugin plugin = plugins.get(i - 1);
			assertEquals("N" + i, plugin.getTag());
			assertEquals(HeaderParser.class, plugin.getParser().getClass());
			EditorButtonResourcesTO to = plugin.getEditorButtonResources();
			assertEquals("Nadpis " + i, to.getDescription());
			assertNull(to.getImage());
			assertEquals("[N" + i + "]", to.getPrefix());
			assertEquals("[/N" + i + "]", to.getSuffix());
			assertEquals("N" + i, to.getTag());
			assertEquals("Nadpisy", to.getTagFamily());
		}
	}

}
