package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class OverviewTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private BeanContainer<Long, HWItemDTO> container;
	private IHWFacade hwFacade;

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(hwFacade.getAllHWItems());
	}

	public OverviewTab(final IHWFacade hwFacade) {
		this.hwFacade = hwFacade;

		setSpacing(true);
		setMargin(true);

		final Button deleteBtn = new Button("Smazat");
		final Button detailsBtn = new Button("Podrobnosti");
		deleteBtn.setEnabled(false);
		detailsBtn.setEnabled(false);

		/**
		 * Přehled typů
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);
		container = new BeanContainer<Long, HWItemDTO>(HWItemDTO.class);
		container.setBeanIdProperty("id");
		populateContainer();
		table.setContainerDataSource(container);

		table.setColumnHeader("name", "Název");
		table.setColumnHeader("purchaseDate", "Získáno");
		table.setColumnHeader("destructionDate", "Odepsáno");
		table.setColumnHeader("price", "Cena");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usage", "Je součástí");
		table.setVisibleColumns(new String[] { "name", "state", "price",
				"usage", "purchaseDate", "destructionDate" });
		table.setWidth("100%");

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				deleteBtn.setEnabled(enabled);
				detailsBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové položky HW
		 */
		final Button newType = new Button("Založit novou položku HW");
		newType.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {

				newType.setEnabled(false);

				NewHWItemWindow win = new NewHWItemWindow(newType) {

					private static final long serialVersionUID = -1397391593801030584L;

					@Override
					protected void onSuccess() {
						populateContainer();
					}
				};

				UI.getCurrent().addWindow(win);
				win.center();
			}

		});
		buttonLayout.addComponent(newType);
		buttonLayout.addComponent(deleteBtn);
		buttonLayout.addComponent(detailsBtn);

	}
}
