package org.myftp.gattserver.grass3.medic.web;

import java.util.Calendar;

import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitState;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.ui.util.GrassStringToDateConverter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class ScheduledVisitsTab extends VerticalLayout {

	private static final long serialVersionUID = -5013459007975657195L;

	private IMedicFacade medicFacade;

	final Table toBePlannedTable = new Table();
	final Table plannedTable = new Table();

	final BeanItemContainer<ScheduledVisitDTO> plannedContainer = new BeanItemContainer<ScheduledVisitDTO>(
			ScheduledVisitDTO.class);
	final BeanItemContainer<ScheduledVisitDTO> toBePlannedContainer = new BeanItemContainer<ScheduledVisitDTO>(
			ScheduledVisitDTO.class);

	// BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		plannedTable.setEnabled(enabled);
		toBePlannedTable.setEnabled(enabled);
	}

	private void openCreateWindow(final boolean planned) {
		Window win = new ScheduledVisitsCreateWindow(ScheduledVisitsTab.this,
				planned) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer(planned);
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openCompletedWindow(final ScheduledVisitDTO scheduledVisitDTO,
			final boolean planned) {
		Window win = new MedicalRecordCreateWindow(ScheduledVisitsTab.this,
				scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				try {
					medicFacade.deleteScheduledVisit(scheduledVisitDTO);
				} catch (Exception e) {
					UI.getCurrent().addWindow(
							new ErrorSubwindow(
									"Nezdařilo se smazat vybranou položku"));
				}
				populateContainer(planned);
			}
		};
		UI.getCurrent().addWindow(win);
	}

	private void openDeleteWindow(BeanItem<?> item, final boolean planned) {
		ScheduledVisitsTab.this.setEnabled(false);
		final ScheduledVisitDTO visit = (ScheduledVisitDTO) item.getBean();
		UI.getCurrent().addWindow(
				new ConfirmSubwindow("Opravdu smazat '" + visit.getPurpose()
						+ "' ?") {

					private static final long serialVersionUID = -422763987707688597L;

					@Override
					protected void onConfirm(ClickEvent event) {
						try {
							medicFacade.deleteScheduledVisit(visit);
							populateContainer(planned);
						} catch (Exception e) {
							UI.getCurrent()
									.addWindow(
											new ErrorSubwindow(
													"Nezdařilo se smazat vybranou položku"));
						}
					}

					@Override
					protected void onClose(CloseEvent e) {
						ScheduledVisitsTab.this.setEnabled(true);
					}
				});
	}

	private void populateContainer(boolean planned) {

		BeanItemContainer<ScheduledVisitDTO> container = planned ? plannedContainer
				: toBePlannedContainer;
		Table table = planned ? plannedTable : toBePlannedTable;

		container.removeAllItems();
		container.addAll(medicFacade.getAllScheduledVisits(planned));
		table.select(null);
		table.sort(new Object[] { "state", "date" }, new boolean[] { false,
				true });
	}

	private void createPlannedTable() {

		final Button newTypeBtn = new Button("Naplánovat návštěvu");
		final Button deleteBtn = new Button("Smazat");
		final Button completedBtn = new Button("Absolvováno");
		deleteBtn.setEnabled(false);
		completedBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		completedBtn.setIcon(new ThemeResource("img/tags/right_16.png"));

		/**
		 * Přehled
		 */
		Label plannedTableLabel = new Label("Naplánované návštěvy");
		addComponent(plannedTableLabel);

		plannedTable.setContainerDataSource(plannedContainer);
		plannedTable.addGeneratedColumn("icon", new Table.ColumnGenerator() {
			private static final long serialVersionUID = -5729717573733167822L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				ScheduledVisitDTO dto = (ScheduledVisitDTO) itemId;
				if (dto.getState().equals(ScheduledVisitState.MISSED)) {
					Embedded icon = new Embedded();
					icon.setSource(new ThemeResource("img/tags/warning_16.png"));
					icon.setDescription("Zmeškáno !");
					return icon;
				} else {
					int plannedMonth = Calendar.getInstance().get(
							Calendar.MONTH);
					Calendar cal = Calendar.getInstance();
					cal.setTime(dto.getDate());
					int currentMonth = cal.get(Calendar.MONTH);
					if (plannedMonth == currentMonth) {
						Embedded icon = new Embedded();
						icon.setSource(new ThemeResource(
								"img/tags/clock_16.png"));
						icon.setDescription("Již tento měsíc");
						return icon;
					}
				}
				return null;
			}
		});
		plannedTable.setColumnHeader("state", "Stav");
		plannedTable.setColumnHeader("purpose", "Účel");
		plannedTable.setColumnHeader("date", "Datum");
		plannedTable.setColumnHeader("institution", "Instituce");
		plannedTable.setColumnHeader("icon", "");
		plannedTable.setColumnWidth("icon", 16);
		plannedTable.setWidth("100%");
		plannedTable.setHeight("250px");
		plannedTable.setSelectable(true);
		plannedTable.setImmediate(true);
		plannedTable.setVisibleColumns(new String[] { "icon", "date",
				"purpose", "institution" });
		plannedTable.setConverter("date",
				GrassStringToDateConverter.getInstance());
		plannedTable.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = plannedTable.getValue() != null;
				deleteBtn.setEnabled(enabled);
				completedBtn.setEnabled(enabled);
			}
		});

		addComponent(plannedTable);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Založení nové návštěvy
		 */
		newTypeBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(true);
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Absolvování návštěvy
		 */
		completedBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openCompletedWindow(
						(ScheduledVisitDTO) plannedTable.getValue(), true);
			}
		});
		buttonLayout.addComponent(completedBtn);

		/**
		 * Smazání návštěvy
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow(
						plannedContainer.getItem(plannedTable.getValue()), true);
			}
		});
		buttonLayout.addComponent(deleteBtn);

		populateContainer(true);
	}

	private void createToBePlannedTable() {

		final Button newTypeBtn = new Button("Naplánovat objednání");
		final Button deleteBtn = new Button("Smazat");
		final Button planBtn = new Button("Objednáno");
		deleteBtn.setEnabled(false);
		planBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		planBtn.setIcon(new ThemeResource("img/tags/calendar_16.png"));

		/**
		 * Přehled
		 */
		Label toBePlannedTableLabel = new Label("K objednání");
		addComponent(toBePlannedTableLabel);

		toBePlannedTable.setContainerDataSource(toBePlannedContainer);
		toBePlannedTable.addGeneratedColumn("icon",
				new Table.ColumnGenerator() {
					private static final long serialVersionUID = -5729717573733167822L;

					@Override
					public Object generateCell(Table source, Object itemId,
							Object columnId) {
						ScheduledVisitDTO dto = (ScheduledVisitDTO) itemId;
						if (dto.getState().equals(ScheduledVisitState.MISSED)) {
							Embedded icon = new Embedded();
							icon.setSource(new ThemeResource(
									"img/tags/warning_16.png"));
							icon.setDescription("Zmeškáno !");
							return icon;
						} else {
							int plannedMonth = Calendar.getInstance().get(
									Calendar.MONTH);
							Calendar cal = Calendar.getInstance();
							cal.setTime(dto.getDate());
							int currentMonth = cal.get(Calendar.MONTH);
							if (plannedMonth == currentMonth) {
								Embedded icon = new Embedded();
								icon.setSource(new ThemeResource(
										"img/tags/clock_16.png"));
								icon.setDescription("Již tento měsíc");
								return icon;
							}
						}
						return null;
					}
				});
		toBePlannedTable.setColumnHeader("state", "Stav");
		toBePlannedTable.setColumnHeader("purpose", "Účel");
		toBePlannedTable.setColumnHeader("date", "Datum");
		toBePlannedTable.setColumnHeader("period", "Pravidelnost");
		toBePlannedTable.setColumnHeader("institution", "Instituce");
		toBePlannedTable.setColumnHeader("icon", "");
		toBePlannedTable.setColumnWidth("icon", 16);
		toBePlannedTable.setWidth("100%");
		toBePlannedTable.setHeight("250px");
		toBePlannedTable.setSelectable(true);
		toBePlannedTable.setImmediate(true);
		toBePlannedTable.setVisibleColumns(new String[] { "icon", "date",
				"period", "purpose", "institution" });
		toBePlannedTable.setConverter("date",
				StringToMonthDateConverter.getInstance());
		toBePlannedTable.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = toBePlannedTable.getValue() != null;
				deleteBtn.setEnabled(enabled);
				planBtn.setEnabled(enabled);
			}
		});

		addComponent(toBePlannedTable);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Naplánovat objednání
		 */
		newTypeBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(false);
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Objednat návštěvy
		 */
		planBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO zadat datum - objednání se vytvoří samo a plánování se smaže
				// TODO pravidelné se nesmažou, ale posunou se o periodu
			}
		});
		buttonLayout.addComponent(planBtn);

		/**
		 * Smazání naplánování
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow(toBePlannedContainer.getItem(toBePlannedTable
						.getValue()), false);
			}
		});
		buttonLayout.addComponent(deleteBtn);

		populateContainer(false);

	}

	public ScheduledVisitsTab(final IMedicFacade medicFacade) {

		this.medicFacade = medicFacade;

		setSpacing(true);
		setMargin(true);

		createPlannedTable();

		addComponent(new Label("<hr/>", ContentMode.HTML));

		createToBePlannedTable();

	}
}