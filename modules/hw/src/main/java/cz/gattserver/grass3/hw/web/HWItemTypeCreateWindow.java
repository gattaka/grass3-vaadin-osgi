package cz.gattserver.grass3.hw.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.HWFacade;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class HWItemTypeCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWFacade hwFacade;

	public HWItemTypeCreateWindow(final Component triggerComponent, final Long fixTypeId) {
		super("Založení nového typu HW");

		triggerComponent.setEnabled(false);

		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		HWItemTypeDTO hwItemTypeDTO;
		if (fixTypeId != null) {
			hwItemTypeDTO = hwFacade.getHWItemType(fixTypeId);
		} else {
			hwItemTypeDTO = new HWItemTypeDTO();
			hwItemTypeDTO.setName("");
		}

		final TextField nameField = new TextField();
		Binder<HWItemTypeDTO> binder = new Binder<HWItemTypeDTO>(HWItemTypeDTO.class);
		binder.setBean(hwItemTypeDTO);
		binder.bind(nameField, "name");
		
		winLayout.addComponent(nameField);
		winLayout.addComponent(new Button("Uložit", e -> {
			if (hwFacade.saveHWType(hwItemTypeDTO)) {
				onSuccess(hwItemTypeDTO);
			} else {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
			close();
		}));

		setContent(winLayout);
		addCloseListener(e -> triggerComponent.setEnabled(true));
	}

	protected abstract void onSuccess(HWItemTypeDTO hwItemTypeDTO);

}
