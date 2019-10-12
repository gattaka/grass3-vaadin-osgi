package cz.gattserver.grass3.medic.web;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.medic.dto.MedicamentDTO;
import cz.gattserver.grass3.medic.facade.MedicFacade;
import cz.gattserver.grass3.ui.components.SaveCloseButtons;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class MedicamentCreateWindow extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient MedicFacade medicFacade;

	public MedicamentCreateWindow() {
		this(null);
	}

	public MedicamentCreateWindow(MedicamentDTO originalDTO) {
		setWidth("300px");

		MedicamentDTO formDTO = new MedicamentDTO();
		Binder<MedicamentDTO> binder = new Binder<>(MedicamentDTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField("Název");
		add(nameField);
		nameField.setWidth("100%");
		binder.forField(nameField).bind("name");

		final TextArea toleranceField = new TextArea("Reakce, nežádoucí účinky");
		add(toleranceField);
		toleranceField.setWidth("100%");
		binder.forField(toleranceField).bind("tolerance");

		add(new SaveCloseButtons(e -> {
			MedicamentDTO writeDTO = originalDTO == null ? new MedicamentDTO() : originalDTO;
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
