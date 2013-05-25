package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private Container populateContainer(IHWFacade hwFacade) {
		BeanContainer<Long, HWItemTypeDTO> container = new BeanContainer<Long, HWItemTypeDTO>(
				HWItemTypeDTO.class);
		container.setBeanIdProperty("id");
		container.addAll(hwFacade.getAllHWTypes());
		return container;
	}

	public HWTypesTab(final IHWFacade hwFacade) {

		setSpacing(true);
		setMargin(true);

		/**
		 * Přehled typů
		 */
		final Table table = new Table();
		table.setContainerDataSource(populateContainer(hwFacade));

		table.setColumnHeader("id", "Id");
		table.setColumnHeader("name", "Název");
		table.setWidth("100%");

		addComponent(table);

		/**
		 * Založení nového typu
		 */
		final Button newType = new Button("Založit nový typ");
		newType.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {

				newType.setEnabled(false);

				final Window win = new Window("Založení nového typu HW");

				VerticalLayout winLayout = new VerticalLayout();
				winLayout.setMargin(true);
				winLayout.setSpacing(true);

				final TextField nameField = new TextField();
				winLayout.addComponent(nameField);

				winLayout.addComponent(new Button("Založit",
						new Button.ClickListener() {

							private static final long serialVersionUID = -8435971966889831628L;

							@Override
							public void buttonClick(ClickEvent event) {

								if (hwFacade.saveHWType(nameField.getValue()) == false) {
									UI.getCurrent()
											.addWindow(
													new Window(
															"Chyba",
															new Label(
																	"Nezdařilo se vytvořit nový typ hardware")));
								} else {
									table.setContainerDataSource(populateContainer(hwFacade));
								}
								win.close();
							}

						}));

				win.setContent(winLayout);

				win.addCloseListener(new CloseListener() {

					private static final long serialVersionUID = 1435044338717794371L;

					@Override
					public void windowClose(CloseEvent e) {
						newType.setEnabled(true);
					}

				});

				UI.getCurrent().addWindow(win);
				win.center();
			}

		});
		addComponent(newType);
		setComponentAlignment(newType, Alignment.BOTTOM_RIGHT);

	}
}
