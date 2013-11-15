package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.medic.web.templates.DeleteBtn;
import org.myftp.gattserver.grass3.medic.web.templates.DetailBtn;
import org.myftp.gattserver.grass3.medic.web.templates.ModifyBtn;
import org.myftp.gattserver.grass3.ui.util.StringToPreviewConverter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MedicalInstitutionsTab extends VerticalLayout implements
		ISelectable {

	private static final long serialVersionUID = -5013459007975657195L;

	private IMedicFacade medicFacade;

	final Table table = new Table();
	private BeanItemContainer<MedicalInstitutionDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openCreateWindow(MedicalInstitutionDTO institution) {
		Window win = new MedicalInstitutionCreateWindow(
				MedicalInstitutionsTab.this, institution) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(medicFacade.getAllMedicalInstitutions());
	}

	public MedicalInstitutionsTab(final IMedicFacade medicFacade) {

		this.medicFacade = medicFacade;

		setSpacing(true);
		setMargin(true);

		final Button detailBtn = new DetailBtn<MedicalInstitutionDTO>("Detail",
				table, MedicalInstitutionsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getDetailWindow(
					MedicalInstitutionDTO selectedValue,
					Component... triggerComponent) {
				return new MedicalInstitutionDetailWindow(
						selectedValue.getId(), triggerComponent);
			}
		};

		final Button modifyInstitutionBtn = new ModifyBtn<MedicalInstitutionDTO>(
				"Upravit instituci", table, MedicalInstitutionsTab.this) {
			private static final long serialVersionUID = -8949928545479455240L;

			@Override
			protected Window getModifyWindow(
					MedicalInstitutionDTO selectedValue,
					Component... triggerComponent) {
				return new MedicalInstitutionCreateWindow(
						MedicalInstitutionsTab.this, selectedValue) {
					private static final long serialVersionUID = -7566950396535469316L;

					@Override
					protected void onSuccess() {
						populateContainer();
					}
				};
			}
		};

		final Button deleteBtn = new DeleteBtn<MedicalInstitutionDTO>("Smazat",
				table, MedicalInstitutionsTab.this) {

			private static final long serialVersionUID = 1900185891293966049L;

			@Override
			protected void onConfirm(MedicalInstitutionDTO selectedValue) {
				medicFacade.deleteMedicalInstitution(selectedValue);
				populateContainer();
			}
		};

		final Button newInstitutionBtn = new Button("Založit novou instituci");
		newInstitutionBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));

		/**
		 * Přehled
		 */
		container = new BeanItemContainer<MedicalInstitutionDTO>(
				MedicalInstitutionDTO.class);
		populateContainer();
		table.setContainerDataSource(container);

		table.setColumnHeader("name", "Název");
		table.setColumnHeader("address", "Adresa");
		table.setColumnHeader("web", "Stránky");
		table.setWidth("100%");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setConverter("web", new StringToPreviewConverter(50));
		table.setVisibleColumns(new String[] { "name", "address", "web" });
		addComponent(table);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové instituce
		 */
		newInstitutionBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(table.getValue() != null ? (MedicalInstitutionDTO) table
						.getValue() : null);
			}

		});
		buttonLayout.addComponent(newInstitutionBtn);

		/**
		 * Detail instituce
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Úprava doktora
		 */
		buttonLayout.addComponent(modifyInstitutionBtn);

		/**
		 * Smazání instituce
		 */
		buttonLayout.addComponent(deleteBtn);

	}

	@Override
	public void select() {
		// tady nic není potřeba
	}
}
