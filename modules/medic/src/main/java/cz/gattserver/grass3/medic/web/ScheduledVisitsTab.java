package cz.gattserver.grass3.medic.web;

import java.util.Calendar;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.dto.ScheduledVisitState;
import cz.gattserver.grass3.medic.facade.IMedicFacade;
import cz.gattserver.grass3.medic.util.MedicUtil;
import cz.gattserver.grass3.medic.web.ScheduledVisitsCreateWindow.Operation;
import cz.gattserver.grass3.template.DetailTableButton;
import cz.gattserver.grass3.template.ISelectable;
import cz.gattserver.web.common.window.ConfirmWindow;
import cz.gattserver.web.common.window.ErrorWindow;

public class ScheduledVisitsTab extends VerticalLayout implements ISelectable {

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
		UI.getCurrent().addWindow(new ConfirmWindow("Opravdu smazat '" + visit.getPurpose() + "' ?") {

			private static final long serialVersionUID = -422763987707688597L;

			@Override
			protected void onConfirm(ClickEvent event) {
				try {
					medicFacade.deleteScheduledVisit(visit);
					populateContainer(planned);
				} catch (Exception e) {
					UI.getCurrent().addWindow(new ErrorWindow("Nezdařilo se smazat vybranou položku"));
				}
			}

			@Override
			public void close() {
				ScheduledVisitsTab.this.setEnabled(true);
				super.close();
			}
		});
	}

	private void populateContainer(boolean planned) {

		BeanItemContainer<ScheduledVisitDTO> container = planned ? plannedContainer : toBePlannedContainer;
		Table table = planned ? plannedTable : toBePlannedTable;

		container.removeAllItems();
		container.addAll(medicFacade.getAllScheduledVisits(planned));
		table.select(null);
		table.sort(new Object[] { "state", "date" }, new boolean[] { false, true });
	}

	private void createPlannedTable() {

		final Button newTypeBtn = new Button("Naplánovat návštěvu");
		final Button modifyBtn = new Button("Upravit");
		final Button deleteBtn = new Button("Smazat");
		final Button completedBtn = new Button("Absolvováno");
		deleteBtn.setEnabled(false);
		completedBtn.setEnabled(false);
		modifyBtn.setEnabled(false);
		newTypeBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		completedBtn.setIcon(new ThemeResource("img/tags/right_16.png"));
		modifyBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));

		final Button detailBtn = new DetailTableButton<ScheduledVisitDTO>("Detail", plannedTable) {
			private static final long serialVersionUID = -8815751115945625539L;

			@Override
			protected Window getDetailWindow(ScheduledVisitDTO selectedValue) {
				return new SchuduledVisitDetailWindow(selectedValue.getId());
			}
		};

		/**
		 * Přehled
		 */
		Label plannedTableLabel = new Label("Naplánované návštěvy");
		addComponent(plannedTableLabel);

		plannedTable.setContainerDataSource(plannedContainer);
		plannedTable.addGeneratedColumn("icon", new Table.ColumnGenerator() {
			private static final long serialVersionUID = -5729717573733167822L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ScheduledVisitDTO dto = (ScheduledVisitDTO) itemId;
				if (dto.getState().equals(ScheduledVisitState.MISSED)) {
					Embedded icon = new Embedded();
					icon.setSource(new ThemeResource("img/tags/warning_16.png"));
					icon.setDescription("Zmeškáno !");
					return icon;
				} else {
					if (MedicUtil.isVisitPending(dto)) {
						Embedded icon = new Embedded();
						icon.setSource(new ThemeResource("img/tags/clock_16.png"));
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
		plannedTable.setVisibleColumns("icon", "date", "purpose", "institution");
		plannedTable.setConverter("date", new StringToFullDateConverter());
		plannedTable.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = plannedTable.getValue() != null;
				deleteBtn.setEnabled(enabled);
				completedBtn.setEnabled(enabled);
				modifyBtn.setEnabled(enabled);
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
				openCreateWindow(true, null);
			}

		});
		buttonLayout.addComponent(newTypeBtn);

		/**
		 * Detail
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Absolvování návštěvy
		 */
		completedBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openCompletedWindow((ScheduledVisitDTO) plannedTable.getValue());
			}
		});
		buttonLayout.addComponent(completedBtn);

		/**
		 * Úprava návštěvy
		 */
		modifyBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(true, (ScheduledVisitDTO) plannedTable.getValue());
			}

		});
		buttonLayout.addComponent(modifyBtn);

		/**
		 * Smazání návštěvy
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow((ScheduledVisitDTO) plannedTable.getValue(), true);
			}
		});
		buttonLayout.addComponent(deleteBtn);

		populateContainer(true);
	}

	private void createToBePlannedTable() {

		final Button newBtn = new Button("Naplánovat objednání");
		final Button deleteBtn = new Button("Smazat");
		final Button modifyBtn = new Button("Upravit");
		final Button planBtn = new Button("Objednáno");
		deleteBtn.setEnabled(false);
		planBtn.setEnabled(false);
		modifyBtn.setEnabled(false);
		newBtn.setIcon(new ThemeResource("img/tags/plus_16.png"));
		deleteBtn.setIcon(new ThemeResource("img/tags/delete_16.png"));
		planBtn.setIcon(new ThemeResource("img/tags/calendar_16.png"));
		modifyBtn.setIcon(new ThemeResource("img/tags/pencil_16.png"));

		final Button detailBtn = new DetailTableButton<ScheduledVisitDTO>("Detail", toBePlannedTable) {
			private static final long serialVersionUID = -8815751115945625539L;

			@Override
			protected Window getDetailWindow(ScheduledVisitDTO selectedValue) {
				return new SchuduledVisitDetailWindow(selectedValue.getId());
			}
		};

		/**
		 * Přehled
		 */
		Label toBePlannedTableLabel = new Label("K objednání");
		addComponent(toBePlannedTableLabel);

		toBePlannedTable.setContainerDataSource(toBePlannedContainer);
		toBePlannedTable.addGeneratedColumn("icon", new Table.ColumnGenerator() {
			private static final long serialVersionUID = -5729717573733167822L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				ScheduledVisitDTO dto = (ScheduledVisitDTO) itemId;
				if (dto.getState().equals(ScheduledVisitState.MISSED)) {
					Embedded icon = new Embedded();
					icon.setSource(new ThemeResource("img/tags/warning_16.png"));
					icon.setDescription("Zmeškáno !");
					return icon;
				} else {
					if (MedicUtil.isVisitPending(dto)) {
						Embedded icon = new Embedded();
						icon.setSource(new ThemeResource("img/tags/clock_16.png"));
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
		toBePlannedTable.setVisibleColumns("icon", "date", "period", "purpose", "institution");
		toBePlannedTable.setConverter("date", new StringToMonthDateConverter());
		toBePlannedTable.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = toBePlannedTable.getValue() != null;
				deleteBtn.setEnabled(enabled);
				planBtn.setEnabled(enabled);
				modifyBtn.setEnabled(enabled);
			}
		});

		addComponent(toBePlannedTable);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		addComponent(buttonLayout);

		/**
		 * Naplánovat objednání
		 */
		newBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(false, null);
			}

		});
		buttonLayout.addComponent(newBtn);

		/**
		 * Detail
		 */
		buttonLayout.addComponent(detailBtn);

		/**
		 * Objednat návštěvy
		 */
		planBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				final ScheduledVisitDTO toBePlannedVisitDTO = (ScheduledVisitDTO) toBePlannedTable.getValue();

				ScheduledVisitDTO newDto = medicFacade.createPlannedScheduledVisitFromToBePlanned(toBePlannedVisitDTO);
				Window win = new ScheduledVisitsCreateWindow(Operation.PLANNED_FROM_TO_BE_PLANNED, newDto) {
					private static final long serialVersionUID = -7566950396535469316L;

					@Override
					protected void onSuccess() {

						if (toBePlannedVisitDTO.getPeriod() > 0) {
							// posuň plánování a ulož úpravu
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(toBePlannedVisitDTO.getDate());
							calendar.add(Calendar.MONTH, toBePlannedVisitDTO.getPeriod());
							toBePlannedVisitDTO.setDate(calendar.getTime());

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
			}
		});
		buttonLayout.addComponent(planBtn);

		/**
		 * Úprava naplánování
		 */
		modifyBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 6492892850247493645L;

			public void buttonClick(ClickEvent event) {
				openCreateWindow(false, (ScheduledVisitDTO) toBePlannedTable.getValue());
			}

		});
		buttonLayout.addComponent(modifyBtn);

		/**
		 * Smazání naplánování
		 */
		deleteBtn.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 4983897852548880141L;

			@Override
			public void buttonClick(ClickEvent event) {
				openDeleteWindow((ScheduledVisitDTO) toBePlannedTable.getValue(), false);
			}
		});
		buttonLayout.addComponent(deleteBtn);

		populateContainer(false);

	}

	public ScheduledVisitsTab() {

		medicFacade = SpringContextHelper.getBean(IMedicFacade.class);

		setSpacing(true);
		setMargin(true);

		DateTimeFormatter formatter = DateTimeFormat.forPattern("d. MMMMM yyyy");
		addComponent(new Label("<strong>Dnes je: </strong>" + LocalDate.now().toString(formatter), ContentMode.HTML));

		addComponent(new Label("<hr/>", ContentMode.HTML));

		createPlannedTable();

		addComponent(new Label("<hr/>", ContentMode.HTML));

		createToBePlannedTable();

	}

	@Override
	public void select() {
		// tady nic není potřeba
	}
}