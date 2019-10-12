package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.PhysicianTO;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class PhysicianCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	public PhysicianCreateDialog() {
		this(null);
	}

	public PhysicianCreateDialog(PhysicianTO originalDTO) {
		setWidth("300px");

		PhysicianTO formDTO = new PhysicianTO();
		Binder<PhysicianTO> binder = new Binder<>(PhysicianTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Jméno");
		add(nameField);
		nameField.addClassName("top-clean");
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		add(new SaveCloseButtons(e -> {
			PhysicianTO writeDTO = originalDTO == null ? new PhysicianTO() : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					SpringContextHelper.getBean(MedicFacade.class).savePhysician(formDTO);
					onSuccess();
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se vytvořit nový záznam").open();
				}
			}
		}, e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);
	}

	protected abstract void onSuccess();

}
