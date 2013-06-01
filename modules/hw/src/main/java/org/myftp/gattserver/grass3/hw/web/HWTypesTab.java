package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private IHWFacade hwFacade;
	final Table table = new Table();
	private BeanContainer<Long, HWItemTypeDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openNewTypeWindow() {
		Window win = new HWItemTypeCreateWindow(HWTypesTab.this) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow() {
		HWTypesTab.this.setEnabled(false);
		BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
				.getContainerDataSource();
		BeanItem<?> item = cont.getItem(table.getValue());
		final HWItemTypeDTO hwItemType = (HWItemTypeDTO) item.getBean();
		UI.getCurrent()
				.addWindow(
						new ConfirmSubwindow(
								"Opravdu smazat '"
										+ hwItemType.getName()
										+ "' (typ bude odebrán od všech označených položek HW) ?") {

							private static final long serialVersionUID = -422763987707688597L;

							@Override 
							protected void onConfirm(ClickEvent event) {
								if (hwFacade.deleteHWItemType(hwItemType)) {
									populateContainer();
								} else {
									UI.getCurrent()
											.addWindow(
													new ErrorSubwindow(
															"Nezdařilo se smazat vybranou položku"));
								}
							}

							@Override
							protected void onClose(CloseEvent e) {
								HWTypesTab.this.setEnabled(true);
							}
						});
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(hwFacade.getAllHWTypes());
	}

	public HWTypesTab(final IHWFacade hwFacade) {

		this.hwFacade = hwFacade;

		setSpacing(true);
		setMargin(true);

		final Button newTypeBtn = new Button("Založit nový typ");
		final Button deleteBtn = new Button("Smazat");
		deleteBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

		/**
		 * Přehled typů
		 */
		container = new BeanContainer<Long, HWItemTypeDTO>(HWItemTypeDTO.class);
		container.setBeanIdProperty("id");
		populateContainer();
		table.setContainerDataSource(container);

		table.setColumnHeader("id", "Id");
		table.setColumnHeader("name", "Název");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setVisibleColumns(new String[] { "name" });
		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				deleteBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nového typu
		 */
		newTypeBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openNewTypeWindow();
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Smazání položky HW
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow();
			}
		});
		buttonLayout.addComponent(deleteBtn);

	}
}
