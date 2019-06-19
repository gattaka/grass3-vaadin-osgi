package cz.gattserver.grass3.articles.templates.plotter;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;

public class PlotterPluginTest {

	@Test
	public void testProperties() {
		PlotterPlugin plugin = new PlotterPlugin();
		assertEquals("PLOTTER", plugin.getTag());
		assertEquals(PlotterParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Plotter", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[PLOTTER]x*x;-5;5;-10;10", to.getPrefix());
		assertEquals("[/PLOTTER]", to.getSuffix());
		assertEquals("PLOTTER", to.getTag());
		assertEquals("Šablony", to.getTagFamily());
	}

}
