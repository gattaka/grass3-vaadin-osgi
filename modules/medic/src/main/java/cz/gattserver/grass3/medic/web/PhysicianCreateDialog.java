package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.PhysicianTO;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.EditWebDialog;
import cz.gattserver.web.common.ui.window.ErrorDialog;

public abstract class PhysicianCreateDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	public PhysicianCreateDialog() {
		this(null);
	}

	public PhysicianCreateDialog(PhysicianTO originalTO) {
		setWidth("300px");

		Binder<PhysicianTO> binder = new Binder<>(PhysicianTO.class);
		binder.setBean(new PhysicianTO());

		final TextField nameField = new TextField("Jméno");
		add(nameField);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		nameField.setWidthFull();
		binder.forField(nameField).bind("name");

		add(new SaveCloseLayout(e -> {
			PhysicianTO writeTO = originalTO == null ? new PhysicianTO() : originalTO;
			if (binder.writeBeanIfValid(writeTO)) {
				try {
					SpringContextHelper.getBean(MedicFacade.class).savePhysician(writeTO);
					onSuccess(writeTO);
					close();
				} catch (Exception ex) {
					new ErrorDialog("Nezdařilo se vytvořit nový záznam").open();
				}
			}
		}, e -> close()));

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	protected abstract void onSuccess(PhysicianTO to);

}
