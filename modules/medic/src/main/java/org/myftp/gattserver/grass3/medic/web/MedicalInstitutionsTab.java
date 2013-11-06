package org.myftp.gattserver.grass3.medic.web;

import org.myftp.gattserver.grass3.medic.dto.MedicalInstitutionDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.ui.util.StringToPreviewConverter;

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

public class MedicalInstitutionsTab extends VerticalLayout implements ISelectable{

	private static final long serialVersionUID = -5013459007975657195L;

	private IMedicFacade medicFacade;

	final Table table = new Table();
	private BeanContainer<Long, MedicalInstitutionDTO> container;

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
	}

	private void openCreateWindow() {
		Window win = new MedicalInstitutionCreateWindow(
				MedicalInstitutionsTab.this) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer();
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow() {
		MedicalInstitutionsTab.this.setEnabled(false);
		BeanContainer<?, ?> cont = (BeanContainer<?, ?>) table
				.getContainerDataSource();
		BeanItem<?> item = cont.getItem(table.getValue());
		final MedicalInstitutionDTO institution = (MedicalInstitutionDTO) item
				.getBean();
		UI.getCurrent().addWindow(
				new ConfirmSubwindow("Opravdu smazat '" + institution.getName()
						+ "' ?") {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							medicFacade.deleteMedicalInstitution(institution);
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
						MedicalInstitutionsTab.this.setEnabled(true);
					}
				});
	}

	private void populateContainer() {
		container.removeAllItems();
		container.addAll(medicFacade.getAllMedicalInstitutions());
	}

	public MedicalInstitutionsTab(final IMedicFacade medicFacade) {

		this.medicFacade = medicFacade;

		setSpacing(true);
		setMargin(true);

		final Button newTypeBtn = new Button("Založit novou instituci");
		final Button deleteBtn = new Button("Smazat");
		deleteBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));

		/**
		 * Přehled
		 */
		container = new BeanContainer<Long, MedicalInstitutionDTO>(
				MedicalInstitutionDTO.class);
		container.setBeanIdProperty("id");
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
		 * Založení nové instituce
		 */
		newTypeBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow();
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Smazání instituce
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

	@Override
	public void select() {
		// tady nic není potřeba
	}
}
