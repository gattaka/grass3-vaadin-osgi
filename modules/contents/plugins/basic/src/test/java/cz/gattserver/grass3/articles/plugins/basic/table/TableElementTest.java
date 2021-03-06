package cz.gattserver.grass3.articles.plugins.basic.table;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cz.gattserver.grass3.articles.editor.parser.elements.Element;
import cz.gattserver.grass3.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass3.articles.editor.parser.impl.ContextImpl;

public class TableElementTest {

	@Test
	public void test_headless() {
		List<List<List<Element>>> rows = new ArrayList<>();

		List<List<Element>> row0 = new ArrayList<>();
		rows.add(row0);
		List<Element> cel00 = new ArrayList<>();
		row0.add(cel00);
		cel00.add(new TextElement("a1"));
		cel00.add(new TextElement("a2"));
		List<Element> cel10 = new ArrayList<>();
		row0.add(cel10);
		cel10.add(new TextElement("b"));
		List<List<Element>> row1 = new ArrayList<>();

		rows.add(row1);
		List<Element> cel01 = new ArrayList<>();
		row1.add(cel01);
		cel01.add(new TextElement("c"));
		List<Element> cel11 = new ArrayList<>();
		row1.add(cel11);
		cel11.add(new TextElement("d"));
		List<Element> cel21 = new ArrayList<>();
		row1.add(cel21);
		cel21.add(new TextElement("e"));

		TableElement e = new TableElement(rows, false, 3);
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<tr><td>a1a2</td><td>b</td><td></td></tr>" + "<tr><td>c</td><td>d</td><td>e</td></tr>"
						+ "</table>",
				out);
	}
	
	@Test
	public void test_withHeader() {
		List<List<List<Element>>> rows = new ArrayList<>();

		List<List<Element>> row0 = new ArrayList<>();
		rows.add(row0);
		List<Element> cel00 = new ArrayList<>();
		row0.add(cel00);
		cel00.add(new TextElement("a1"));
		cel00.add(new TextElement("a2"));
		List<Element> cel10 = new ArrayList<>();
		row0.add(cel10);
		cel10.add(new TextElement("b"));
		List<List<Element>> row1 = new ArrayList<>();

		rows.add(row1);
		List<Element> cel01 = new ArrayList<>();
		row1.add(cel01);
		cel01.add(new TextElement("c"));
		List<Element> cel11 = new ArrayList<>();
		row1.add(cel11);
		cel11.add(new TextElement("d"));
		List<Element> cel21 = new ArrayList<>();
		row1.add(cel21);
		cel21.add(new TextElement("e"));

		TableElement e = new TableElement(rows, true, 3);
		ContextImpl ctx = new ContextImpl();
		e.apply(ctx);
		String out = ctx.getOutput();
		assertEquals(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">"
						+ "<thead><th>a1a2</th><th>b</th><th></th></thead>" + "<tr><td>c</td><td>d</td><td>e</td></tr>"
						+ "</table>",
				out);
	}

}
