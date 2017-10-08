package cz.gattserver.grass3.hw.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Table;

import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class HWTypesTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	@Autowired
	private HWFacade hwFacade;

	final Table table = new Table();
	private BeanContainer<Long, HWItemTypeDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openNewTypeWindow(boolean fix) {
		HWItemTypeDTO hwItemTypeDTO = null;
		if (fix) {
			BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table.getContainerDataSource();
			BeanItem<?> item = cont.getItem(table.getValue());
			hwItemTypeDTO = (HWItemTypeDTO) item.getBean();
		}
		Window win = new HWItemTypeCreateWindow(HWTypesTab.this, hwItemTypeDTO == null ? null : hwItemTypeDTO.getId()) {
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
		BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table.getContainerDataSource();
		BeanItem<?> item = cont.getItem(table.getValue());
		final HWItemTypeDTO hwItemType = (HWItemTypeDTO) item.getBean();
		UI.getCurrent().addWindow(new ConfirmWindow(
				"Opravdu smazat '" + hwItemType.getName() + "' (typ bude odebrán od všech označených položek HW) ?",
				e -> {
					if (hwFacade.deleteHWItemType(hwItemType.getId())) {
						populateContainer();
					} else {
						UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
					}
				}) {
			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				HWTypesTab.this.setEnabled(true);
				super.close();
			}
		});
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(hwFacade.getAllHWTypes());
	}

	public HWTypesTab() {
		SpringContextHelper.inject(this);

		setSpacing(true);
		setMargin(true);

		final Button newTypeBtn = new Button("Založit nový typ");
		final Button fixBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		fixBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
		fixBtn.setIcon(new ThemeResource(ImageIcons.QUICKEDIT_16_ICON));
		deleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));

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
		table.setVisibleColumns(new Object[] { "name" });
		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				deleteBtn.setEnabled(enabled);
				fixBtn.setEnabled(enabled);
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
				openNewTypeWindow(false);
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Úprava typu
		 */
		fixBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openNewTypeWindow(true);
			}

		});
		buttonLayout.addComponent(fixBtn);

		/**
		 * Smazání typu
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
