package cz.gattserver.grass3.articles.templates.sort;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass3.articles.editor.parser.elements.Element;

class SortElementsLine {
	
	private String comparable;
	private List<Element> elements = new ArrayList<>();

	public String getComparable() {
		return comparable;
	}

	public void setComparable(String comparable) {
		this.comparable = comparable;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

}