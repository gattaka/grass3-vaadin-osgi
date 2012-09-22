package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public abstract class ColumnBuilder {

	private String rightBorderStyleName = "right_middle_background";
	private Integer columnWidth = 200;

	/**
	 * Default konstruktor
	 */
	public ColumnBuilder() {
	}

	/**
	 * Společně s šířkou sloupce je potřeba udávat i style třídu pravého okraje,
	 * protože pozadí se bude muset posunout doprava dle této šířky :(
	 * 
	 * Prakticky jde jenom o dopsání do CSS řádku (pro šířku sloupce 200px): <br>
	 * <br>
	 * <code> background-position: 180px 0;</code>
	 * 
	 * @param columnWidth
	 * @param rightBorderStyleName
	 */
	public ColumnBuilder(Integer columnWidth, String rightBorderStyleName) {
		setColumnWidth(columnWidth, rightBorderStyleName);
	}

	/**
	 * @see ColumnBuilder
	 */
	public void setColumnWidth(Integer columnWidth, String rightBorderStyleName) {
		this.columnWidth = columnWidth;
		this.rightBorderStyleName = rightBorderStyleName;
	}

	public VerticalLayout buildColumn() {

		// Obalovací layout
		VerticalLayout backgroundLayout = new VerticalLayout();
		backgroundLayout.setWidth(columnWidth + "px");

		// Top part
		HorizontalLayout topBackgroundLayout = new HorizontalLayout();
		backgroundLayout.addComponent(topBackgroundLayout);
		createTopPart(topBackgroundLayout);

		// middle part
		HorizontalLayout contentLayout = new HorizontalLayout();
		backgroundLayout.addComponent(contentLayout);
		backgroundLayout.setComponentAlignment(contentLayout,
				Alignment.MIDDLE_CENTER);
		createMiddlePart(contentLayout);

		// bottom backgroud
		HorizontalLayout bottomBackgroundLayout = new HorizontalLayout();
		backgroundLayout.addComponent(bottomBackgroundLayout);
		createBottomPart(bottomBackgroundLayout);

		return backgroundLayout;
	}

	private void createTopPart(HorizontalLayout layout) {

		// left corner
		HorizontalLayout leftTopCornerBackground = new HorizontalLayout();
		leftTopCornerBackground.setStyleName("left_top_corner_background");
		leftTopCornerBackground.setHeight("10px");
		leftTopCornerBackground.setWidth("10px");
		layout.addComponent(leftTopCornerBackground);

		// middle
		HorizontalLayout centerTopBackground = new HorizontalLayout();
		centerTopBackground.setStyleName("center_top_background");
		centerTopBackground.setHeight("10px");
		centerTopBackground.setWidth(columnWidth - 20 + "px");
		layout.addComponent(centerTopBackground);

		// right corner
		HorizontalLayout rightTopCornerBackground = new HorizontalLayout();
		rightTopCornerBackground.setStyleName("right_top_corner_background");
		rightTopCornerBackground.setHeight("10px");
		rightTopCornerBackground.setWidth("10px");
		layout.addComponent(rightTopCornerBackground);
	}

	private void createMiddlePart(HorizontalLayout layout) {

		/**
		 * Tady je to takhle přeházené, protože není dopředu známo, jak vysoká
		 * tato část bude. Střední části je to jedno, ale okraje se potřebují
		 * nějak roztáhnout na konkrétní výškou, řešením je tak jejich vzájemné
		 * proložení, aby je oba zevnnitř roztahoval obsahový layout, který
		 * udává výšku.
		 */

		// left border
		HorizontalLayout leftMiddleBackground = new HorizontalLayout();
		leftMiddleBackground.setStyleName("left_middle_background");
		leftMiddleBackground.setWidth(columnWidth + "px");
		layout.addComponent(leftMiddleBackground);

		// right border
		HorizontalLayout rightMiddleBackground = new HorizontalLayout();
		rightMiddleBackground.setStyleName(rightBorderStyleName);
		rightMiddleBackground.setWidth(columnWidth - 10 + "px");
		leftMiddleBackground.addComponent(rightMiddleBackground);

		// middle
		VerticalLayout centerMiddleBackground = new VerticalLayout();
		centerMiddleBackground.setStyleName("center_middle_background");
		centerMiddleBackground.setWidth(columnWidth - 20 + "px");
		rightMiddleBackground.addComponent(centerMiddleBackground);

		// middle part content
		createColumnContent(centerMiddleBackground);

	}

	private void createBottomPart(HorizontalLayout layout) {

		// left corner
		HorizontalLayout leftBottomCornerBackground = new HorizontalLayout();
		leftBottomCornerBackground
				.setStyleName("left_bottom_corner_background");
		leftBottomCornerBackground.setHeight("10px");
		leftBottomCornerBackground.setWidth("10px");
		layout.addComponent(leftBottomCornerBackground);

		// middle
		HorizontalLayout centerBottomBackground = new HorizontalLayout();
		centerBottomBackground.setStyleName("center_bottom_background");
		centerBottomBackground.setHeight("10px");
		centerBottomBackground.setWidth(columnWidth - 20 + "px");
		layout.addComponent(centerBottomBackground);

		// right corner
		HorizontalLayout rightBottomCornerBackground = new HorizontalLayout();
		rightBottomCornerBackground
				.setStyleName("right_bottom_corner_background");
		rightBottomCornerBackground.setHeight("10px");
		rightBottomCornerBackground.setWidth("10px");
		layout.addComponent(rightBottomCornerBackground);
	}

	/**
	 * Obsah sloupce
	 * 
	 * @param layout
	 */
	protected abstract void createColumnContent(VerticalLayout layout);

}
