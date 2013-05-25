package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class OverviewTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private Container populateContainer(IHWFacade hwFacade) {
		BeanContainer<Long, HWItemDTO> container = new BeanContainer<Long, HWItemDTO>(
				HWItemDTO.class);
		container.setBeanIdProperty("id");
		container.addAll(hwFacade.getAllHWItems());
		return container;
	}

	public OverviewTab(final IHWFacade hwFacade) {

		setSpacing(true);
		setMargin(true);

		/**
		 * Přehled typů
		 */
		final Table table = new Table();
		table.setContainerDataSource(populateContainer(hwFacade));

		table.setColumnHeader("id", "Id");
		table.setColumnHeader("name", "Název");
		table.setColumnHeader("purchaseDate", "Získáno");
		table.setColumnHeader("destructionDate", "Odepsáno");
		table.setColumnHeader("price", "Cena");
		table.setColumnHeader("state", "Stav");
		table.setWidth("100%");

		addComponent(table);

		/**
		 * Založení nové položky HW
		 */
		final Button newType = new Button("Založit novou položku HW");
		newType.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {

				newType.setEnabled(false);

				final HWItemDTO hwItemDTO = new HWItemDTO();
				BeanItem<HWItemDTO> beanItem = new BeanItem<HWItemDTO>(
						hwItemDTO);

				final Window win = new Window("Založení nové položky HW");

				VerticalLayout winLayout = new VerticalLayout();
				winLayout.setMargin(true);
				winLayout.setSpacing(true);

				TextField nameField = new TextField("Název", beanItem
						.getItemProperty("name"));
				winLayout.addComponent(nameField);
				
				DateField purchaseDateField = new DateField("Získáno", beanItem
						.getItemProperty("purchaseDate"));
				winLayout.addComponent(purchaseDateField);

				DateField destructionDateField = new DateField("Odepsáno",
						beanItem.getItemProperty("destructionDate"));
				winLayout.addComponent(destructionDateField);

				TextField priceField = new TextField("Cena", beanItem
						.getItemProperty("price"));
				winLayout.addComponent(priceField);

				TextField stateField = new TextField("Stav", beanItem
						.getItemProperty("state"));
				winLayout.addComponent(stateField);

				winLayout.addComponent(new Button("Založit",
						new Button.ClickListener() {

							private static final long serialVersionUID = -8435971966889831628L;

							@Override
							public void buttonClick(ClickEvent event) {

								if (hwFacade.saveHWItem(hwItemDTO)) {
									table.setContainerDataSource(populateContainer(hwFacade));
								} else {
									UI.getCurrent()
											.addWindow(
													new Window(
															"Chyba",
															new Label(
																	"Nezdařilo se vytvořit novou položku hardware")));
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
