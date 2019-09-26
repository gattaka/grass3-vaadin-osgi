package cz.gattserver.grass3.campgames.ui.windows;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class CampgameKeywordDialog extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameKeywordDialog(CampgameKeywordTO originalDTO) {
		super("Úprava klíčového slova");
		init(originalDTO);
	}

	public CampgameKeywordDialog() {
		super("Nové klíčové slovo");
		init(null);
	}

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	public void init(CampgameKeywordTO originalDTO) {
		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setPadding(true);
		winLayout.setSpacing(true);

		CampgameKeywordTO formDTO = new CampgameKeywordTO();
		formDTO.setName("");
		Binder<CampgameKeywordTO> binder = new Binder<>(CampgameKeywordTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		binder.bind(nameField, "name");

		winLayout.add(nameField);
		winLayout.add(new Button("Uložit", e -> {
			try {
				CampgameKeywordTO writeDTO = originalDTO == null ? new CampgameKeywordTO() : originalDTO;
				binder.writeBean(writeDTO);
				getCampgamesService().saveCampgameKeyword(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ex) {
				new ErrorDialog("Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next()).open();
			} catch (Exception ex) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		add(winLayout);
		setCloseOnEsc(false);
	}

	protected abstract void onSuccess(CampgameKeywordTO campgameKeywordTO);

}
