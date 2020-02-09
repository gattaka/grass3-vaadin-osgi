package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.medic.interfaces.MedicamentTO;
import cz.gattserver.grass3.ui.components.SaveCloseLayout;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicamentCreateDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicamentCreateDialog() {
		this(null);
	}

	public MedicamentCreateDialog(MedicamentTO originalDTO) {
		setWidth("300px");

		MedicamentTO formDTO = new MedicamentTO();
		Binder<MedicamentTO> binder = new Binder<>(MedicamentTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		add(nameField);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		nameField.setWidthFull();
		binder.forField(nameField).bind("name");

		final TextArea toleranceField = new TextArea("Reakce, nežádoucí účinky");
		add(toleranceField);
		toleranceField.setHeight("200px");
		toleranceField.setWidthFull();
		binder.forField(toleranceField).bind("tolerance");

		add(new SaveCloseLayout(e -> {
			MedicamentTO writeDTO = originalDTO == null ? new MedicamentTO() : originalDTO;
			if (binder.writeBeanIfValid(writeDTO)) {
				try {
					getMedicFacade().saveMedicament(writeDTO);
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

	protected MedicFacade getMedicFacade() {
		if (medicFacade == null)
			medicFacade = SpringContextHelper.getBean(MedicFacade.class);
		return medicFacade;
	}

	protected abstract void onSuccess();

}
