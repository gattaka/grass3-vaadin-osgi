package org.myftp.gattserver.grass3.hw.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HWItemsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private BeanContainer<Long, HWItemDTO> container;
	private IHWFacade hwFacade;

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(hwFacade.getAllHWItems());
	}

	public HWItemsTab(final IHWFacade hwFacade) {
		this.hwFacade = hwFacade;

		setSpacing(true);
		setMargin(true);

		final Button deleteBtn = new Button("Smazat");
		final Button detailsBtn = new Button("Detail");
		final Button newNoteBtn = new Button("Přidat záznam");
		deleteBtn.setEnabled(false);
		detailsBtn.setEnabled(false);
		newNoteBtn.setEnabled(false);
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		detailsBtn.setIcon(new ThemeResource("img/tags/clipboard_16.png"));
		newNoteBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));

		/**
		 * Tabulka HW
		 */
		final Table table = new Table();
		table.setSelectable(true);
		table.setImmediate(true);
		container = new BeanContainer<Long, HWItemDTO>(HWItemDTO.class);
		container.setBeanIdProperty("id");
		populateContainer();
		table.setContainerDataSource(container);

		StringToDateConverter dateConverter = new StringToDateConverter() {
			private static final long serialVersionUID = -2914696445291603483L;

			@Override
			protected DateFormat getFormat(Locale locale) {
				return new SimpleDateFormat("dd.MM.yyyy");
			}
		};

		table.setConverter("purchaseDate", dateConverter);
		table.setConverter("destructionDate", dateConverter);
		table.setConverter("state", new StringToHWItemStateConverter());

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
				newNoteBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové položky HW
		 */
		final Button newTypeBtn = new Button("Založit novou položku HW");
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		newTypeBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {

				newTypeBtn.setEnabled(false);

				NewHWItemWindow win = new NewHWItemWindow(newTypeBtn) {

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
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Založení nového servisního záznamu
		 */
		newNoteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8876001665427003203L;

			public void buttonClick(ClickEvent event) {

				newNoteBtn.setEnabled(false);

				BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
						.getContainerDataSource();
				BeanItem<?> item = cont.getItem(table.getValue());
				HWItemDTO hwItem = (HWItemDTO) item.getBean();

				NewServiceNoteWindow win = new NewServiceNoteWindow(newNoteBtn,
						hwItem) {

					private static final long serialVersionUID = -5582822648042555576L;

					@Override
					protected void onSuccess() {
						populateContainer();
					}
				};

				UI.getCurrent().addWindow(win);
				win.center();
			}

		});
		buttonLayout.addComponent(newNoteBtn);

		/**
		 * Zobrazení detailů položky HW
		 */
		detailsBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
						.getContainerDataSource();
				BeanItem<?> item = cont.getItem(table.getValue());
				final HWItemDTO hwItem = (HWItemDTO) item.getBean();
				UI.getCurrent().addWindow(
						new HWItemDetailWindow(detailsBtn, hwItem));
			}
		});
		buttonLayout.addComponent(detailsBtn);

		/**
		 * Smazání položky HW
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
						.getContainerDataSource();
				BeanItem<?> item = cont.getItem(table.getValue());
				final HWItemDTO hwItem = (HWItemDTO) item.getBean();
				UI.getCurrent().addWindow(
						new ConfirmSubwindow("Opravdu smazat '"
								+ hwItem.getName()
								+ "' (budou smazány i servisní záznamy) ?") {

							private static final long serialVersionUID = -422763987707688597L;

							@Override
							protected void onConfirm(ClickEvent event) {
								if (hwFacade.deleteHWItem(hwItem)) {
									populateContainer();
								} else {
									UI.getCurrent()
											.addWindow(
													new Window(
															"Chyba",
															new Label(
																	"Nezdařilo se smazat vybranou položku")));
								}
							}
						});
			}
		});
		buttonLayout.addComponent(deleteBtn);

	}
}
