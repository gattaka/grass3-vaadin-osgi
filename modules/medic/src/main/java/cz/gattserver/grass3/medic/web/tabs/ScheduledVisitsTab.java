package cz.gattserver.grass3.medic.web.tabs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.ScheduledVisitState;
import cz.gattserver.grass3.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.medic.web.MedicalRecordCreateDialog;
import cz.gattserver.grass3.medic.web.Operation;
import cz.gattserver.grass3.medic.web.ScheduledVisitsCreateDialog;
import cz.gattserver.grass3.medic.web.SchuduledVisitDetailDialog;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.DetailGridButton;
import cz.gattserver.grass3.ui.components.button.GridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ButtonLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.HtmlDiv;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.Strong;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public class ScheduledVisitsTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient MedicFacade medicFacade;

	private Grid<ScheduledVisitTO> toBePlannedGrid = new Grid<>();
	private Grid<ScheduledVisitTO> plannedGrid = new Grid<>();

	public ScheduledVisitsTab() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy");
		Div div = new HtmlDiv("<strong>Dnes je: </strong>" + LocalDate.now().format(formatter));
		div.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(div);

		createPlannedGrid();
		createToBePlannedTable();
	}

	private void openCreateWindow(final boolean planned, ScheduledVisitTO scheduledVisitDTO) {
		new ScheduledVisitsCreateDialog(planned ? Operation.PLANNED : Operation.TO_BE_PLANNED, scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer(planned);
			}
		}.open();
	}

	private void openCompletedWindow(final ScheduledVisitTO scheduledVisitDTO) {
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

	private void openDeleteWindow(final ScheduledVisitTO visit, final boolean planned) {
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
		Grid<ScheduledVisitTO> grid = planned ? plannedGrid : toBePlannedGrid;
		if (planned) {
			plannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		} else {
			toBePlannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		}
		grid.getDataProvider().refreshAll();
		grid.deselectAll();
		Column<ScheduledVisitTO> dateColumn = grid.getColumnByKey("date");
		grid.sort(Arrays.asList(new GridSortOrder<ScheduledVisitTO>(dateColumn, SortDirection.ASCENDING)));
	}

	private void createPlannedGrid() {

		/**
		 * Přehled
		 */
		Div headerDiv = new Div(new Strong("Naplánované návštěvy"));
		headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(headerDiv);

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
		final Button modifyBtn = new ModifyGridButton<ScheduledVisitTO>(to -> openCreateWindow(true, to), plannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání návštěvy
		 */
		final Button deleteBtn = new DeleteGridButton<ScheduledVisitTO>(
				set -> openDeleteWindow(set.iterator().next(), true), plannedGrid);
		buttonLayout.add(deleteBtn);

		/**
		 * Absolvování návštěvy
		 */
		final Button completedBtn = new GridButton<ScheduledVisitTO>("Absolvováno",
				set -> openCompletedWindow(set.iterator().next()), plannedGrid);
		completedBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "Upravit"));
		buttonLayout.add(completedBtn);

		/**
		 * Detail
		 */
		final Button detailBtn = new DetailGridButton<ScheduledVisitTO>(
				item -> new SchuduledVisitDetailDialog(item.getId()).open(), plannedGrid);
		buttonLayout.add(detailBtn);

		populateContainer(true);
	}

	private void prepareGrid(Grid<ScheduledVisitTO> grid, boolean fullTime) {
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
		grid.addColumn(new IconRenderer<ScheduledVisitTO>(item -> {
			if (item.getState().equals(ScheduledVisitState.MISSED)) {
				return new Image(ImageIcon.WARNING_16_ICON.createResource(), "Zmeškáno");
			} else {
				if (MedicUtil.isVisitPending(item))
					return new Image(ImageIcon.CLOCK_16_ICON.createResource(), "Blíží se");
			}
			return new Span();
		}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(ScheduledVisitTO::getPurpose).setKey("purpose").setHeader("Účel");
		if (fullTime)
			grid.addColumn(new LocalDateTimeRenderer<ScheduledVisitTO>(to -> to.getDate().atTime(to.getTime()),
					DateTimeFormatter.ofPattern("d. MMMM yyyy H:mm", Locale.forLanguageTag("CS")))).setKey("date")
					.setHeader("Datum").setWidth("200px").setFlexGrow(0);
		else
			grid.addColumn(new LocalDateTimeRenderer<ScheduledVisitTO>(to -> to.getDate().atStartOfDay(),
					DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("CS")))).setKey("date")
					.setHeader("Datum").setWidth("100px").setFlexGrow(0);
		grid.addColumn(new TextRenderer<ScheduledVisitTO>(
				to -> to.getInstitution() == null ? "" : to.getInstitution().getName())).setKey("institution")
				.setHeader("Instituce");

		grid.setWidth("100%");
		grid.setHeight("250px");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void createToBePlannedTable() {

		/**
		 * Přehled
		 */
		Div headerDiv = new Div(new Strong("K objednání"));
		headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(headerDiv);

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
		final Button detailBtn = new DetailGridButton<ScheduledVisitTO>(
				item -> new SchuduledVisitDetailDialog(item.getId()).open(), toBePlannedGrid);
		buttonLayout.add(detailBtn);

		/**
		 * Objednat návštěvy
		 */
		final Button planBtn = new GridButton<ScheduledVisitTO>("Objednáno", toBePlannedGrid);
		planBtn.setIcon(new Image(ImageIcon.CALENDAR_16_ICON.createResource(), "Objednáno"));
		planBtn.addClickListener(event -> {
			final ScheduledVisitTO toBePlannedVisitDTO = (ScheduledVisitTO) toBePlannedGrid.getSelectedItems()
					.iterator().next();

			ScheduledVisitTO newDto = getMedicFacade().createPlannedScheduledVisitFromToBePlanned(toBePlannedVisitDTO);
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
		final Button modifyBtn = new ModifyGridButton<ScheduledVisitTO>(to -> openCreateWindow(false, to),
				toBePlannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání naplánování
		 */
		final Button deleteBtn = new DeleteGridButton<ScheduledVisitTO>(
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