package cz.gattserver.grass3.medic.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.medic.web.ScheduledVisitsCreateDialog.Operation;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.DetailGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class ScheduledVisitsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient MedicFacade medicFacade;

	private Grid<ScheduledVisitDTO> toBePlannedGrid = new Grid<>();
	private Grid<ScheduledVisitDTO> plannedGrid = new Grid<>();

	public ScheduledVisitsTab() {
		setSpacing(true);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
		add(new HtmlDiv("<strong>Dnes je: </strong>" + LocalDate.now().format(formatter)));

		createPlannedGrid();
		createToBePlannedTable();
	}

	private void openCreateWindow(final boolean planned, ScheduledVisitDTO scheduledVisitDTO) {
		new ScheduledVisitsCreateDialog(planned ? Operation.PLANNED : Operation.TO_BE_PLANNED, scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer(planned);
			}
		}.open();
	}

	private void openCompletedWindow(final ScheduledVisitDTO scheduledVisitDTO) {
		new MedicalRecordCreateDialog(scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				try {
					getMedicFacade().deleteScheduledVisit(scheduledVisitDTO);
				} catch (Exception e) {
					new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
				}
				populateContainer(true);
			}
		}.open();
	}

	private void openDeleteWindow(final ScheduledVisitDTO visit, final boolean planned) {
		ScheduledVisitsTab.this.setEnabled(false);
		new ConfirmDialog("Opravdu smazat '" + visit.getPurpose() + "' ?", ev -> {
			try {
				getMedicFacade().deleteScheduledVisit(visit);
				populateContainer(planned);
			} catch (Exception e) {
				new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
			}
		}) {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			public void close() {
				ScheduledVisitsTab.this.setEnabled(true);
				super.close();
			}

		}.open();
	}

	private void populateContainer(boolean planned) {
		Grid<ScheduledVisitDTO> grid = planned ? plannedGrid : toBePlannedGrid;
		if (planned) {
			plannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		} else {
			toBePlannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		}
		grid.getDataProvider().refreshAll();
		grid.deselectAll();
		Column<ScheduledVisitDTO> dateColumn = grid.getColumnByKey("date");
		grid.sort(Arrays.asList(new GridSortOrder<ScheduledVisitDTO>(dateColumn, SortDirection.ASCENDING)));
	}

	private void createPlannedGrid() {

		/**
		 * Přehled
		 */
		add(new H2("Naplánované návštěvy"));

		prepareGrid(plannedGrid, true);
		add(plannedGrid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Založení nové návštěvy
		 */
		final Button newTypeBtn = new CreateButton("Naplánovat návštěvu", event -> openCreateWindow(true, null));
		buttonLayout.add(newTypeBtn);

		/**
		 * Úprava návštěvy
		 */
		final Button modifyBtn = new ModifyGridButton<ScheduledVisitDTO>(to -> openCreateWindow(true, to), plannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání návštěvy
		 */
		final Button deleteBtn = new DeleteGridButton<ScheduledVisitDTO>(
				set -> openDeleteWindow(set.iterator().next(), true), plannedGrid);
		buttonLayout.add(deleteBtn);

		/**
		 * Absolvování návštěvy
		 */
		final Button completedBtn = new GridButton<ScheduledVisitDTO>("Absolvováno",
				set -> openCompletedWindow(set.iterator().next()), plannedGrid);
		completedBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "Upravit"));
		buttonLayout.add(completedBtn);

		/**
		 * Detail
		 */
		final Button detailBtn = new DetailGridButton<ScheduledVisitDTO>(
				item -> new SchuduledVisitDetailWindow(item.getId()), plannedGrid);
		buttonLayout.add(detailBtn);

		populateContainer(true);
	}

	private void prepareGrid(Grid<ScheduledVisitDTO> grid, boolean fullTime) {
		grid.addColumn(new IconRenderer<ScheduledVisitDTO>(item -> {
			if (item.getState().equals(ScheduledVisitState.MISSED)) {
				return new Image(ImageIcon.WARNING_16_ICON.createResource(), "Zmeškáno");
			} else {
				if (MedicUtil.isVisitPending(item))
					return new Image(ImageIcon.CLOCK_16_ICON.createResource(), "Blíží se");
			}
			return new Span();
		}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(ScheduledVisitDTO::getState).setKey("state").setHeader("Stav");
		grid.addColumn(ScheduledVisitDTO::getPurpose).setKey("purpose").setHeader("Účel");
		if (fullTime)
			grid.addColumn(new LocalDateTimeRenderer<ScheduledVisitDTO>(to -> to.getDate().atTime(to.getTime()),
					"dd.MM.yyyy HH:mm")).setKey("date").setHeader("Datum");
		else
			grid.addColumn(new LocalDateTimeRenderer<ScheduledVisitDTO>(to -> to.getDate().atStartOfDay(), "MMMM yyyy"))
					.setKey("date").setHeader("Datum");
		grid.addColumn(new TextRenderer<ScheduledVisitDTO>(
				to -> to.getInstitution() == null ? "" : to.getInstitution().getName())).setKey("institution")
				.setHeader("Instituce");

		grid.setWidth("100%");
		grid.setHeight("250px");
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.setColumns("icon", "date", "purpose", "institution");
	}

	private void createToBePlannedTable() {

		/**
		 * Přehled
		 */
		add(new Strong("K objednání"));

		prepareGrid(toBePlannedGrid, false);
		add(toBePlannedGrid);

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Naplánovat objednání
		 */
		final Button newBtn = new CreateButton("Naplánovat objednání", e -> openCreateWindow(false, null));
		buttonLayout.add(newBtn);

		/**
		 * Detail
		 */
		final Button detailBtn = new DetailGridButton<ScheduledVisitDTO>(
				item -> new SchuduledVisitDetailWindow(item.getId()).open(), toBePlannedGrid);
		buttonLayout.add(detailBtn);

		/**
		 * Objednat návštěvy
		 */
		final Button planBtn = new GridButton<ScheduledVisitDTO>("Objednáno", toBePlannedGrid);
		planBtn.setIcon(new Image(ImageIcon.CALENDAR_16_ICON.createResource(), "Objednáno"));
		planBtn.addClickListener(event -> {
			final ScheduledVisitDTO toBePlannedVisitDTO = (ScheduledVisitDTO) toBePlannedGrid.getSelectedItems()
					.iterator().next();

			ScheduledVisitDTO newDto = getMedicFacade().createPlannedScheduledVisitFromToBePlanned(toBePlannedVisitDTO);
			new ScheduledVisitsCreateDialog(Operation.PLANNED_FROM_TO_BE_PLANNED, newDto) {
				private static final long serialVersionUID = -7566950396535469316L;

				@Override
				protected void onSuccess() {
					try {
						if (toBePlannedVisitDTO.getPeriod() > 0) {
							// posuň plánování a ulož úpravu
							toBePlannedVisitDTO
									.setDate(toBePlannedVisitDTO.getDate().plusMonths(toBePlannedVisitDTO.getPeriod()));
							getMedicFacade().saveScheduledVisit(toBePlannedVisitDTO);
						} else {
							// nemá pravidelnost - návštěva byla objednána,
							// plánování návštěvy lze smazat
							getMedicFacade().deleteScheduledVisit(toBePlannedVisitDTO);
						}

						populateContainer(true);
						populateContainer(false);
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se naplánovat příští objednání").open();
					}
				}
			}.open();
		});
		buttonLayout.add(planBtn);

		/**
		 * Úprava naplánování
		 */
		final Button modifyBtn = new ModifyGridButton<ScheduledVisitDTO>(to -> openCreateWindow(false, to),
				toBePlannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání naplánování
		 */
		final Button deleteBtn = new DeleteGridButton<ScheduledVisitDTO>(
				set -> openDeleteWindow(set.iterator().next(), false), toBePlannedGrid);
		buttonLayout.add(deleteBtn);

		populateContainer(false);
	}

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

}