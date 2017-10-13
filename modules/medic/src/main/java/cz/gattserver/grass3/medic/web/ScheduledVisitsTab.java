package cz.gattserver.grass3.medic.web;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.medic.web.ScheduledVisitsCreateWindow.Operation;
import cz.gattserver.grass3.pages.template.GridUtils;
import cz.gattserver.grass3.template.DetailGridButton;
import cz.gattserver.web.common.SpringContextHelper;
import cz.gattserver.web.common.ui.BoldLabel;
import cz.gattserver.web.common.ui.ImageIcons;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class ScheduledVisitsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private MedicFacade medicFacade;

	private Grid<ScheduledVisitDTO> toBePlannedGrid = new Grid<>();
	private Grid<ScheduledVisitDTO> plannedGrid = new Grid<>();
	private Collection<ScheduledVisitDTO> toBePlannedGridData;
	private Collection<ScheduledVisitDTO> plannedGridData;

	private void openCreateWindow(final boolean planned, ScheduledVisitDTO scheduledVisitDTO) {
		Window win = new ScheduledVisitsCreateWindow(planned ? Operation.PLANNED : Operation.TO_BE_PLANNED,
				scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer(planned);
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openCompletedWindow(final ScheduledVisitDTO scheduledVisitDTO) {
		Window win = new MedicalRecordCreateWindow(ScheduledVisitsTab.this, scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				try {
					medicFacade.deleteScheduledVisit(scheduledVisitDTO);
				} catch (Exception e) {
					UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
				}
				populateContainer(true);
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow(final ScheduledVisitDTO visit, final boolean planned) {
		ScheduledVisitsTab.this.setEnabled(false);
		UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + visit.getPurpose() + "' ?", ev -> {
			try {
				medicFacade.deleteScheduledVisit(visit);
				populateContainer(planned);
			} catch (Exception e) {
				UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
			}
		}) {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				ScheduledVisitsTab.this.setEnabled(true);
				super.close();
			}

		});
	}

	private void populateContainer(boolean planned) {
		Grid<ScheduledVisitDTO> grid = planned ? plannedGrid : toBePlannedGrid;
		if (planned) {
			plannedGridData = medicFacade.getAllScheduledVisits(planned);
			plannedGrid.setItems(plannedGridData);
		} else {
			toBePlannedGridData = medicFacade.getAllScheduledVisits(planned);
			toBePlannedGrid.setItems(toBePlannedGridData);
		}
		grid.getDataProvider().refreshAll();
		grid.deselectAll();
		grid.sort("date");
	}

	private void createPlannedGrid() {
		final Button newTypeBtn = new Button("Naplánovat návštěvu");
		final Button modifyBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		final Button completedBtn = new Button("Absolvováno");
		deleteBtn.setEnabled(false);
		completedBtn.setEnabled(false);
		modifyBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
		deleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));
		completedBtn.setIcon(new ThemeResource(ImageIcons.RIGHT_16_ICON));
		modifyBtn.setIcon(new ThemeResource(ImageIcons.PENCIL_16_ICON));

		final Button detailBtn = new DetailGridButton<ScheduledVisitDTO>("Detail",
				(e, item) -> UI.getCurrent().addWindow(new SchuduledVisitDetailWindow(item.getId())), plannedGrid);

		/**
		 * Přehled
		 */
		Label plannedTableLabel = new BoldLabel("Naplánované návštěvy");
		addComponent(plannedTableLabel);

		prepareGrid(plannedGrid);

		plannedGrid.addSelectionListener(event -> {
			boolean enabled = event.getAllSelectedItems().size() == 1;
			detailBtn.setEnabled(enabled);
			deleteBtn.setEnabled(enabled);
			completedBtn.setEnabled(enabled);
			modifyBtn.setEnabled(enabled);
		});

		addComponent(plannedGrid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové návštěvy
		 */
		newTypeBtn.addClickListener(event -> openCreateWindow(true, null));
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Detail
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Absolvování návštěvy
		 */
		completedBtn.addClickListener(
				event -> openCompletedWindow((ScheduledVisitDTO) plannedGrid.getSelectedItems().iterator().next()));
		buttonLayout.addComponent(completedBtn);

		/**
		 * Úprava návštěvy
		 */
		modifyBtn.addClickListener(
				event -> openCreateWindow(true, (ScheduledVisitDTO) plannedGrid.getSelectedItems().iterator().next()));
		buttonLayout.addComponent(modifyBtn);

		/**
		 * Smazání návštěvy
		 */
		deleteBtn.addClickListener(
				event -> openDeleteWindow((ScheduledVisitDTO) plannedGrid.getSelectedItems().iterator().next(), true));
		buttonLayout.addComponent(deleteBtn);

		populateContainer(true);
	}

	private void prepareGrid(Grid<ScheduledVisitDTO> grid) {
		grid.addColumn(item -> {
			if (item.getState().equals(ScheduledVisitState.MISSED)) {
				return new ThemeResource(ImageIcons.WARNING_16_ICON);
			} else {
				if (MedicUtil.isVisitPending(item))
					return new ThemeResource(ImageIcons.CLOCK_16_ICON);
			}
			return null;
		}, new ImageRenderer<>()).setId("icon").setWidth(GridUtils.ICON_COLUMN_WIDTH);

		grid.addColumn(ScheduledVisitDTO::getState, new TextRenderer()).setId("state").setCaption("Stav");
		grid.addColumn(ScheduledVisitDTO::getPurpose).setId("purpose").setCaption("Účel");
		grid.addColumn(ScheduledVisitDTO::getDate, new LocalDateTimeRenderer("dd.MM.yyyy")).setId("date")
				.setCaption("Datum");
		grid.addColumn(item -> item.getInstitution().getName()).setId("institution").setCaption("Instituce");
		grid.setWidth("100%");
		grid.setHeight("250px");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("icon", "date", "purpose", "institution");

	}

	private void createToBePlannedTable() {

		final Button newBtn = new Button("Naplánovat objednání");
		final Button deleteBtn = new Button("Smazat");
		final Button modifyBtn = new Button("Upravit");
		final Button planBtn = new Button("Objednáno");
		deleteBtn.setEnabled(false);
		planBtn.setEnabled(false);
		modifyBtn.setEnabled(false);
		newBtn.setIcon(new ThemeResource(ImageIcons.PLUS_16_ICON));
		deleteBtn.setIcon(new ThemeResource(ImageIcons.DELETE_16_ICON));
		planBtn.setIcon(new ThemeResource(ImageIcons.CALENDAR_16_ICON));
		modifyBtn.setIcon(new ThemeResource(ImageIcons.PENCIL_16_ICON));

		final Button detailBtn = new DetailGridButton<ScheduledVisitDTO>("Detail",
				(e, item) -> UI.getCurrent().addWindow(new SchuduledVisitDetailWindow(item.getId())), toBePlannedGrid);

		/**
		 * Přehled
		 */
		Label toBePlannedTableLabel = new BoldLabel("K objednání");
		addComponent(toBePlannedTableLabel);

		prepareGrid(toBePlannedGrid);
		toBePlannedGrid.addSelectionListener(event -> {
			boolean enabled = event.getAllSelectedItems().size() == 1;
			detailBtn.setEnabled(enabled);
			deleteBtn.setEnabled(enabled);
			planBtn.setEnabled(enabled);
			modifyBtn.setEnabled(enabled);
		});

		addComponent(toBePlannedGrid);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Naplánovat objednání
		 */
		newBtn.addClickListener(e -> openCreateWindow(false, null));
		buttonLayout.addComponent(newBtn);

		/**
		 * Detail
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Objednat návštěvy
		 */
		planBtn.addClickListener(event -> {
			final ScheduledVisitDTO toBePlannedVisitDTO = (ScheduledVisitDTO) toBePlannedGrid.getSelectedItems()
					.iterator().next();

			ScheduledVisitDTO newDto = medicFacade.createPlannedScheduledVisitFromToBePlanned(toBePlannedVisitDTO);
			Window win = new ScheduledVisitsCreateWindow(Operation.PLANNED_FROM_TO_BE_PLANNED, newDto) {
				private static final long serialVersionUID = -7566950396535469316L;

				@Override
				protected void onSuccess() {
					if (toBePlannedVisitDTO.getPeriod() > 0) {
						// posuň plánování a ulož úpravu
						toBePlannedVisitDTO
								.setDate(toBePlannedVisitDTO.getDate().plusMonths(toBePlannedVisitDTO.getPeriod()));
						if (medicFacade.saveScheduledVisit(toBePlannedVisitDTO) == false) {
							Notification.show("Nezdařilo se naplánovat příští objednání", Type.WARNING_MESSAGE);
						}
					} else {
						// nemá pravidelnost - návštěva byla objednána,
						// plánování návštěvy lze smazat
						medicFacade.deleteScheduledVisit(toBePlannedVisitDTO);
					}

					populateContainer(true);
					populateContainer(false);
				}
			};
			UI.getCurrent().addWindow(win);
		});
		buttonLayout.addComponent(planBtn);

		/**
		 * Úprava naplánování
		 */
		modifyBtn.addClickListener(event -> openCreateWindow(false,
				(ScheduledVisitDTO) toBePlannedGrid.getSelectedItems().iterator().next()));
		buttonLayout.addComponent(modifyBtn);

		/**
		 * Smazání naplánování
		 */
		deleteBtn.addClickListener(event -> openDeleteWindow(
				(ScheduledVisitDTO) toBePlannedGrid.getSelectedItems().iterator().next(), false));
		buttonLayout.addComponent(deleteBtn);

		populateContainer(false);

	}

	public ScheduledVisitsTab() {
		medicFacade = SpringContextHelper.getBean(MedicFacade.class);

		setSpacing(true);
		setMargin(new MarginInfo(true, false, false, false));

		DateTimeFormatter formatter = DateTimeFormat.forPattern("d.MM.yyyy");
		addComponent(new Label("<strong>Dnes je: </strong>" + LocalDate.now().toString(formatter), ContentMode.HTML));

		createPlannedGrid();
		createToBePlannedTable();
	}

}