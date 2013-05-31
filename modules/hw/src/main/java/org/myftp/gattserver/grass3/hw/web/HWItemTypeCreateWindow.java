package org.myftp.gattserver.grass3.hw.web;

import org.myftp.gattserver.grass3.SpringContextHelper;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.facade.IHWFacade;
import org.myftp.gattserver.grass3.subwindows.ErrorSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public abstract class HWItemTypeCreateWindow extends GrassSubWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private IHWFacade hwFacade;

	public HWItemTypeCreateWindow(final Component triggerComponent) {
		super("Založení nového typu HW");

		hwFacade = SpringContextHelper.getBean(IHWFacade.class);

		triggerComponent.setEnabled(false);

		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		HWItemTypeDTO hwItemTypeDTO = new HWItemTypeDTO();
		hwItemTypeDTO.setName("");
		final BeanFieldGroup<HWItemTypeDTO> fieldGroup = new BeanFieldGroup<HWItemTypeDTO>(
				HWItemTypeDTO.class);
		fieldGroup.setItemDataSource(hwItemTypeDTO);

		final TextField nameField = new TextField();
		winLayout.addComponent(nameField);
		fieldGroup.bind(nameField, "name");

		winLayout.addComponent(new Button("Založit",
				new Button.ClickListener() {

					private static final long serialVersionUID = -8435971966889831628L;

					@Override
					public void buttonClick(ClickEvent event) {
						try {
							fieldGroup.commit();
							if (hwFacade.saveHWType(nameField.getValue()) == false) {
								UI.getCurrent()
										.addWindow(
												new ErrorSubwindow(
														"Nezdařilo se vytvořit nový typ hardware"));
							} else {
								onSuccess();
							}
							close();
						} catch (CommitException e) {
							Notification.show("   Chybná vstupní data\n\n   "
									+ e.getCause().getMessage(),
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
