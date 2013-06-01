package org.myftp.gattserver.grass3.hw.web;

import java.util.List;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.util.GrassStringToDateConverter;
import org.myftp.gattserver.grass3.util.GrassStringToMoneyConverter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

public class HWItemDetailWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;
	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

	private Label createShiftedLabel(String caption) {
		Label label = new Label(caption, ContentMode.HTML);
		label.addStyleName("shiftlabel");
		return label;
	}

	private String createPriceString(Integer price) {
		if (price == null)
			return "-";
		return GrassStringToMoneyConverter.format(price);
	}

	private String createWarrantyYearsString(Integer warrantyYears) {
		if (warrantyYears == null)
			return "-";
		switch (warrantyYears) {
		case 0:
			return "-";
		case 1:
			return warrantyYears + " rok";
		case 2:
		case 3:
		case 4:
			return warrantyYears + " roky";
		default:
			return warrantyYears + " let";
		}
	}

	public HWItemDetailWindow(final Component triggerComponent,
			final HWItemDTO hwItem) {
		super(hwItem.getName());

		setWidth("830px");
		// setHeight("630px");

		triggerComponent.setEnabled(false);

		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSpacing(true);
		layout.setMargin(true);

		GridLayout winLayout = new GridLayout(3, 8);
		layout.addComponent(winLayout);
		winLayout.setSpacing(true);
		winLayout.setWidth("100%");

		/**
		 * Info pole - první sloupec
		 */
		winLayout.addComponent(new Label("<strong>Stav</strong>",
				ContentMode.HTML), 0, 0);
		winLayout.addComponent(createShiftedLabel(hwItem.getState().getName()),
				0, 1);

		winLayout.addComponent(new Label("<strong>Cena</strong>",
				ContentMode.HTML), 0, 2);
		winLayout.addComponent(
				createShiftedLabel(createPriceString(hwItem.getPrice())), 0, 3);

		winLayout.addComponent(new Label("<strong>Získáno</strong>",
				ContentMode.HTML), 0, 4);
		String purchDate = hwItem.getPurchaseDate() == null ? "-"
				: GrassStringToDateConverter.format(hwItem.getPurchaseDate());
		winLayout.addComponent(createShiftedLabel(purchDate), 0, 5);

		winLayout.addComponent(new Label("<strong>Odepsáno</strong>",
				ContentMode.HTML), 0, 6);
		String destrDate = hwItem.getDestructionDate() == null ? "-"
				: GrassStringToDateConverter
						.format(hwItem.getDestructionDate());
		winLayout.addComponent(createShiftedLabel(destrDate), 0, 7);

		/**
		 * Info pole - druhý sloupec
		 */
		winLayout.addComponent(new Label("<strong>Záruka</strong>",
				ContentMode.HTML), 1, 0);
		winLayout.addComponent(
				createShiftedLabel(createWarrantyYearsString(hwItem
						.getWarrantyYears())), 1, 1);

		winLayout.addComponent(new Label("<strong>Je součástí</strong>",
				ContentMode.HTML), 1, 2);
		if (hwItem.getUsedIn() == null) {
			winLayout.addComponent(createShiftedLabel("-"), 1, 3);
		} else {
			Button usedInBtn = new Button(hwItem.getUsedIn().getName());
			usedInBtn.setStyleName(BaseTheme.BUTTON_LINK);
			usedInBtn.addStyleName("shiftlabel");
			usedInBtn.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 4983897852548880141L;

				@Override
				public void buttonClick(ClickEvent event) {
					close();
					UI.getCurrent().addWindow(
							new HWItemDetailWindow(triggerComponent, hwItem
									.getUsedIn()));
				}
			});
			winLayout.addComponent(usedInBtn, 1, 3);
		}

		winLayout.addComponent(new Label("<strong>Typ</strong>",
				ContentMode.HTML), 1, 4);
		StringBuilder builder = new StringBuilder();
		for (HWItemTypeDTO type : hwItem.getTypes()) {
			builder.append(type.getName());
			builder.append(", ");
		}
		String types = builder.length() > 1 ? builder.substring(0,
				builder.length() - 2) : "-";
		winLayout.addComponent(createShiftedLabel(types), 1, 5);

		/**
		 * Součásti
		 */
		IHWFacade hwFacade = SpringContextHelper.getBean(IHWFacade.class);
		layout.addComponent(new Label("<strong>Součásti</strong>",
				ContentMode.HTML));
		List<HWItemDTO> parts = hwFacade.getAllParts(hwItem.getId());
		if (parts.isEmpty())
			layout.addComponent(createShiftedLabel("-"));
		for (final HWItemDTO part : parts) {
			Button partDetailBtn = new Button(part.getName());
			partDetailBtn.setStyleName(BaseTheme.BUTTON_LINK);
			partDetailBtn.addStyleName("shiftlabel");
			partDetailBtn.addClickListener(new Button.ClickListener() {

				private static final long serialVersionUID = 4983897852548880141L;

				@Override
				public void buttonClick(ClickEvent event) {
					close();
					UI.getCurrent().addWindow(
							new HWItemDetailWindow(triggerComponent, part));
				}
			});
			layout.addComponent(partDetailBtn);
		}

		/**
		 * Spodní část
		 */
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		layout.addComponent(horizontalLayout);
		horizontalLayout.setWidth("100%");
		horizontalLayout.setSpacing(true);
		horizontalLayout.setMargin(new MarginInfo(true, false, false, false));

		/**
		 * Tabulka záznamů
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);

		BeanContainer<Long, ServiceNoteDTO> container = new BeanContainer<Long, ServiceNoteDTO>(
				ServiceNoteDTO.class);
		container.setBeanIdProperty("id");
		container.addAll(hwItem.getServiceNotes());

		table.setContainerDataSource(container);
		table.setConverter("date", GrassStringToDateConverter.getInstance());
		table.setConverter("state", new StringToHWItemStateConverter());
		table.setConverter("usedIn", new StringToHWItemConverter());

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usedIn", "Je součástí");
		table.setVisibleColumns(new String[] { "date", "state", "usedIn" });
		table.setWidth("450px");

		table.setSortContainerPropertyId("date");
		table.setSortAscending(false);

		horizontalLayout.addComponent(table);

		/**
		 * Detail záznamu
		 */
		final Label serviceNoteDescription = new Label(DEFAULT_NOTE_LABEL_VALUE);
		Panel panel = new Panel(serviceNoteDescription);
		panel.setStyleName("hw-panel");
		panel.setHeight("100%");
		panel.setWidth("100%");
		horizontalLayout.addComponent(panel);
		horizontalLayout.setExpandRatio(panel, 1);

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
							.getContainerDataSource();
					BeanItem<?> item = cont.getItem(table.getValue());
					ServiceNoteDTO serviceNoteDTO = (ServiceNoteDTO) item
							.getBean();
					serviceNoteDescription.setValue((String) serviceNoteDTO
							.getDescription());
				} else {
					serviceNoteDescription.setValue(DEFAULT_NOTE_LABEL_VALUE);
				}

			}
		});

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				if (triggerComponent != null)
					triggerComponent.setEnabled(true);
			}

		});

		center();

	}
}
