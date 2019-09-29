package cz.gattserver.grass3.ui.util;

public class TableBuilder {

	private StringBuilder builder;

	public TableBuilder() {
		builder = new StringBuilder();
		builder.append("<table class=\"grass-table\">");
	}

	public TableBuilder startRow() {
		builder.append("<tr>");
		return this;
	}

	public TableBuilder endRow() {
		builder.append("</tr>");
		return this;
	}

	public TableBuilder strongCell(String value) {
		builder.append("<td>");
		builder.append("<strong>");
		builder.append(value);
		builder.append("</strong>");
		builder.append("</td>");
		return this;
	}

	public TableBuilder cell(String value) {
		builder.append("<td>");
		builder.append(value);
		builder.append("</td>");
		return this;
	}

	public TableBuilder nextRow() {
		endRow();
		startRow();
		return this;
	}

	public String build() {
		builder.append("</table>");
		return builder.toString();
	}

}
