package cz.gattserver.grass3.hw.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.facade.IHWFacade;
import cz.gattserver.web.common.window.ErrorWindow;
import cz.gattserver.web.common.window.WebWindow;

public abstract class HWItemTypeCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private IHWFacade hwFacade;

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

		final BeanFieldGroup<HWItemTypeDTO> fieldGroup = new BeanFieldGroup<HWItemTypeDTO>(HWItemTypeDTO.class);
		fieldGroup.setItemDataSource(hwItemTypeDTO);

		final TextField nameField = new TextField();
		winLayout.addComponent(nameField);
		fieldGroup.bind(nameField, "name");

		winLayout.addComponent(new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = -8435971966889831628L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fieldGroup.commit();
					if (hwFacade.saveHWType(hwItemTypeDTO) == false) {
						UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
					} else {
						onSuccess();
					}
					close();
				} catch (CommitException e) {
					Notification.show("   Chybná vstupní data\n\n   " + e.getCause().getMessage(),
							Notification.Type.TRAY_NOTIFICATION);
				}
			}
		}));

		setContent(winLayout);

		addCloseListener(new CloseListener() {

			private static final long serialVersionUID = 1435044338717794371L;

			@Override
			public void windowClose(CloseEvent e) {
				triggerComponent.setEnabled(true);
			}

		});

	}

	protected abstract void onSuccess();

}
