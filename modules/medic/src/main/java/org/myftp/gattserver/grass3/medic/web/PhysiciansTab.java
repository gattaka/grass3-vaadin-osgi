package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.medic.dto.PhysicianDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class PhysiciansTab extends VerticalLayout implements ISelectable {

	private static final long serialVersionUID = -5013459007975657195L;

	private IMedicFacade medicFacade;

	final Table table = new Table();
	private BeanItemContainer<PhysicianDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) { 
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openCreateWindow(final PhysicianDTO physician) {
		Window win = new PhysicianCreateWindow(PhysiciansTab.this, physician) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow(final PhysicianDTO physician) {
		PhysiciansTab.this.setEnabled(false);
		UI.getCurrent().addWindow(
				new ConfirmSubwindow("Opravdu smazat '" + physician.getName()
						+ "' ?") {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							medicFacade.deletePhysician(physician);
							populateContainer();
						} catch (Exception e) {
							UI.getCurrent()
									.addWindow(
											new ErrorSubwindow(
													"Nezdařilo se smazat vybranou položku"));
						}
					}

					@Override
					protected void onClose(CloseEvent e) {
						PhysiciansTab.this.setEnabled(true);
					}
				});
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(medicFacade.getAllPhysicians());
	}

	public PhysiciansTab(final IMedicFacade medicFacade) {

		this.medicFacade = medicFacade;

		setSpacing(true);
		setMargin(true);

		final Button newPhysicianBtn = new Button("Přidat doktora");
		final Button modifyPhysicianBtn = new Button("Upravit doktora");
		final Button deleteBtn = new Button("Smazat");
		deleteBtn.setEnabled(false);
		modifyPhysicianBtn.setEnabled(false);
		newPhysicianBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		modifyPhysicianBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

		/**
		 * Přehled
		 */
		container = new BeanItemContainer<PhysicianDTO>(PhysicianDTO.class);
		populateContainer();
		table.setContainerDataSource(container);

		table.setColumnHeader("name", "Jméno");
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
				modifyPhysicianBtn.setEnabled(enabled);
			}
		});

		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Přidání nového doktora
		 */
		newPhysicianBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(table.getValue() != null ? (PhysicianDTO) table
						.getValue() : null);
			}

		});
		buttonLayout.addComponent(newPhysicianBtn);

		/**
		 * Úprava doktora
		 */
		modifyPhysicianBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow((PhysicianDTO) table.getValue());
			}

		});
		buttonLayout.addComponent(modifyPhysicianBtn);

		/**
		 * Smazání doktora
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow((PhysicianDTO) table.getValue());
			}
		});
		buttonLayout.addComponent(deleteBtn);

	}

	@Override
	public void select() {
		// tady nic není potřeba
	}

}
