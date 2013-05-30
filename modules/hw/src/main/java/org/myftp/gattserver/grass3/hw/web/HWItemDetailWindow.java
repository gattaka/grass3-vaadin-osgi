package org.myftp.gattserver.grass3.hw.web;

import java.text.SimpleDateFormat;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

public class HWItemDetailWindow extends Window {

	private static final long serialVersionUID = -6773027334692911384L;
	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";
	private static final int MAX_DESCRIPTION_PREVIEW_LENGTH = 20;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	private class ShiftedLabel extends Label {
		private static final long serialVersionUID = 318954634723598021L;

		public ShiftedLabel(String caption) {
			super("&nbsp;&nbsp;" + caption, ContentMode.HTML);
		}
	}

	private String createDescriptionPreview(String description) {
		if (description.length() <= MAX_DESCRIPTION_PREVIEW_LENGTH)
			return description;
		return description.substring(0, MAX_DESCRIPTION_PREVIEW_LENGTH) + "...";
	}

	public HWItemDetailWindow(final Button btn, final HWItemDTO hwItem) {
		super("Detail pro '" + hwItem.getName() + "'");

		setWidth("830px");
		setHeight("630px");

		GridLayout winLayout = new GridLayout(3, 9);
		setContent(winLayout);
		winLayout.setSpacing(true);
		winLayout.setMargin(true);
		winLayout.setWidth("100%");

		/**
		 * Info pole - první sloupec
		 */
		winLayout.addComponent(new Label("<strong>Název</strong>",
				ContentMode.HTML), 0, 0);
		winLayout.addComponent(new ShiftedLabel(hwItem.getName()), 0, 1);

		winLayout.addComponent(new Label("<strong>Cena</strong>",
				ContentMode.HTML), 0, 2);
		winLayout.addComponent(new ShiftedLabel(hwItem.getPrice().toString()),
				0, 3);

		winLayout.addComponent(new Label("<strong>Získáno</strong>",
				ContentMode.HTML), 0, 4);
		String purchDate = hwItem.getPurchaseDate() == null ? "-" : dateFormat
				.format(hwItem.getPurchaseDate());
		winLayout.addComponent(new ShiftedLabel(purchDate), 0, 5);

		winLayout.addComponent(new Label("<strong>Odepsáno</strong>",
				ContentMode.HTML), 0, 6);
		String destrDate = hwItem.getDestructionDate() == null ? "-"
				: dateFormat.format(hwItem.getDestructionDate());
		winLayout.addComponent(new ShiftedLabel(destrDate), 0, 7);

		/**
		 * Info pole - druhý sloupec
		 */
		winLayout.addComponent(new Label("<strong>Stav</strong>",
				ContentMode.HTML), 1, 0);
		winLayout.addComponent(new ShiftedLabel(hwItem.getState().getName()),
				1, 1);

		winLayout.addComponent(new Label("<strong>Je součástí</strong>",
				ContentMode.HTML), 1, 2);
		winLayout.addComponent(new ShiftedLabel(hwItem.getUsage() == null ? "-"
				: hwItem.getUsage()), 1, 3);

		winLayout.addComponent(new Label("<strong>Typ</strong>",
				ContentMode.HTML), 1, 4);
		StringBuilder builder = new StringBuilder();
		for (HWItemTypeDTO type : hwItem.getTypes()) {
			builder.append(type.getName());
			builder.append(", ");
		}
		String types = builder.length() > 1 ? builder.substring(0,
				builder.length() - 1) : "-";
		winLayout.addComponent(new ShiftedLabel(types), 1, 5);

		final Label serviceNoteDescription = new Label(DEFAULT_NOTE_LABEL_VALUE);
		Panel panel = new Panel(serviceNoteDescription);
		panel.setStyleName("hw-panel");
		panel.setHeight("200px");
		panel.setWidth("400px");
		winLayout.addComponent(panel, 2, 0, 2, 7);
		winLayout.setComponentAlignment(panel, Alignment.MIDDLE_RIGHT);

		/**
		 * Tabulka záznamů
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);
		final IndexedContainer container = new IndexedContainer();
		container.addContainerProperty("date", String.class, "-");
		container.addContainerProperty("state", String.class, "-");
		container.addContainerProperty("description", String.class, "-");
		container.addContainerProperty("usage", String.class, "-");
		container.addContainerProperty("descriptionPreview", String.class, "-");
		for (ServiceNoteDTO note : hwItem.getServiceNotes()) {
			Item item = container.addItem(note);
			item.getItemProperty("date").setValue(
					dateFormat.format(note.getDate()));
			item.getItemProperty("state").setValue(note.getState().getName());
			item.getItemProperty("descriptionPreview").setValue(
					createDescriptionPreview(note.getDescription()));
			item.getItemProperty("description").setValue(note.getDescription());
			item.getItemProperty("usage").setValue(note.getUsage());
		}
		table.setContainerDataSource(container);

		table.setColumnHeader("date", "Datum");
		table.setColumnHeader("state", "Stav");
		table.setColumnHeader("usage", "Je součástí");
		table.setColumnHeader("descriptionPreview", "Popis");
		table.setVisibleColumns(new String[] { "date", "state", "usage",
				"descriptionPreview" });
		table.setWidth("100%");

		table.setSortContainerPropertyId("date");
		table.setSortAscending(false);

		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() != null) {
					Item item = container.getItem(table.getValue());
					serviceNoteDescription.setValue((String) item
							.getItemProperty("description").getValue());
				} else {
					serviceNoteDescription.setValue(DEFAULT_NOTE_LABEL_VALUE);
				}

			}
		});

		winLayout.addComponent(table, 0, 8, 2, 8);

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				btn.setEnabled(true);
			}

		});

		center();

	}
}
