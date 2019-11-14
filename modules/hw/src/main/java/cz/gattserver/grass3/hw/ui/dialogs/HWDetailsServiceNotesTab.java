package cz.gattserver.grass3.hw.ui.dialogs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass3.hw.interfaces.HWItemTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteTO;
import cz.gattserver.grass3.hw.service.HWService;
import cz.gattserver.grass3.ui.components.OperationsLayout;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.DeleteGridButton;
import cz.gattserver.grass3.ui.components.button.ModifyGridButton;
import cz.gattserver.grass3.ui.util.ContainerDiv;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;

public class HWDetailsServiceNotesTab extends Div {

	private static final long serialVersionUID = -3236939739462367881L;

	private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

	@Autowired
	private HWService hwService;

	private Column<ServiceNoteTO> serviceDateColumn;
	private Grid<ServiceNoteTO> serviceNotesGrid;
	private HWItemTO hwItem;
	private HWItemDetailsDialog hwItemDetailDialog;

	public HWDetailsServiceNotesTab(HWItemTO hwItem, HWItemDetailsDialog hwItemDetailDialog) {
		SpringContextHelper.inject(this);
		this.hwItem = hwItem;
		this.hwItemDetailDialog = hwItemDetailDialog;
		init();
	}

	private void init() {
		serviceNotesGrid = new Grid<>();
		UIUtils.applyGrassDefaultStyle(serviceNotesGrid);
		serviceNotesGrid.setSelectionMode(SelectionMode.SINGLE);
		Column<ServiceNoteTO> idColumn = serviceNotesGrid
				.addColumn(new TextRenderer<ServiceNoteTO>(to -> String.valueOf(to.getId())));
		serviceDateColumn = serviceNotesGrid
				.addColumn(new LocalDateRenderer<ServiceNoteTO>(ServiceNoteTO::getDate, "d.M.yyyy")).setHeader("Datum")
				.setTextAlign(ColumnTextAlign.END).setWidth("80px").setFlexGrow(0);
		serviceNotesGrid.addColumn(hw -> hw.getState().getName()).setHeader("Stav").setWidth("110px").setFlexGrow(0);
		serviceNotesGrid
				.addColumn(
						new TextRenderer<>(to -> to.getUsedInName() == null ? "" : String.valueOf(to.getUsedInName())))
				.setHeader("Je součástí").setWidth("180px").setFlexGrow(0);
		serviceNotesGrid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getDescription()))).setHeader("Obsah");
		idColumn.setVisible(false);
		serviceNotesGrid.setHeight("300px");

		serviceNotesGrid
				.sort(Arrays.asList(new GridSortOrder<ServiceNoteTO>(serviceDateColumn, SortDirection.ASCENDING),
						new GridSortOrder<ServiceNoteTO>(idColumn, SortDirection.ASCENDING)));

		populateServiceNotesGrid();

		add(serviceNotesGrid);

		final Div serviceNoteDescription = new ContainerDiv();
		serviceNoteDescription.add(DEFAULT_NOTE_LABEL_VALUE);
		serviceNoteDescription.setHeight("300px");
		serviceNoteDescription.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(serviceNoteDescription);

		OperationsLayout operationsLayout = new OperationsLayout(e -> hwItemDetailDialog.close());
		add(operationsLayout);

		Button newNoteBtn = new CreateButton(e -> new ServiceNoteCreateDialog(hwItem) {
			private static final long serialVersionUID = -5582822648042555576L;

			@Override
			protected void onSuccess(ServiceNoteTO noteDTO) {
				hwItem.getServiceNotes().add(noteDTO);
				populateServiceNotesGrid();
				hwItemDetailDialog.refreshItem();
				hwItemDetailDialog.switchServiceNotesTab();
				serviceNotesGrid.select(noteDTO);
			}
		}.open());

		Button fixNoteBtn = new ModifyGridButton<>("Opravit záznam", event -> {
			if (serviceNotesGrid.getSelectedItems().isEmpty())
				return;
			new ServiceNoteCreateDialog(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
				private static final long serialVersionUID = -5582822648042555576L;

				@Override
				protected void onSuccess(ServiceNoteTO noteDTO) {
					populateServiceNotesGrid();
				}
			}.open();
		}, serviceNotesGrid);

		Button deleteNoteBtn = new DeleteGridButton<>("Smazat záznam", items -> {
			ServiceNoteTO item = items.iterator().next();
			hwService.deleteServiceNote(item, hwItem.getId());
			hwItem.getServiceNotes().remove(item);
			populateServiceNotesGrid();
			hwItemDetailDialog.refreshTabLabels();
		}, serviceNotesGrid);

		serviceNotesGrid.addSelectionListener(selection -> {
			if (selection.getFirstSelectedItem().isPresent()) {
				ServiceNoteTO serviceNoteDTO = selection.getFirstSelectedItem().get();
				serviceNoteDescription.setText((String) serviceNoteDTO.getDescription());
			} else {
				serviceNoteDescription.setText(DEFAULT_NOTE_LABEL_VALUE);
			}
		});

		operationsLayout.add(newNoteBtn);
		operationsLayout.add(fixNoteBtn);
		operationsLayout.add(deleteNoteBtn);
	}

	private void populateServiceNotesGrid() {
		serviceNotesGrid.setItems(hwItem.getServiceNotes());
		serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.DESCENDING)));
	}
}
