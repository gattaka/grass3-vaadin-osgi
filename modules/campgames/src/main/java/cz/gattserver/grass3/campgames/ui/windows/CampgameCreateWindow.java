package cz.gattserver.grass3.campgames.ui.windows;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.grass3.ui.util.TokenField;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorDialog;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class CampgameCreateWindow extends WebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameCreateWindow(Long originalId) {
		super(originalId == null ? "Založení nové hry" : "Oprava údajů existující hry");
		init(originalId == null ? null : getCampgameService().getCampgame(originalId));
	}

	public CampgameCreateWindow() {
		super("Založení nové hry");
		init(null);
	}

	public CampgameCreateWindow(CampgameTO originalDTO) {
		super(originalDTO == null ? "Založení nové hry" : "Oprava údajů existující hry");
		init(originalDTO);
	}

	private CampgamesService getCampgameService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	/**
	 * @param originalId
	 *            opravuji údaje existující položky, nebo vytvářím novou (
	 *            {@code null}) ?
	 */
	private void init(CampgameTO originalDTO) {
		CampgameTO formDTO = new CampgameTO();
		formDTO.setName("");

		FormLayout winLayout = new FormLayout();
		layout.add(winLayout);
		winLayout.setWidth("400px");

		Binder<CampgameTO> binder = new Binder<>(CampgameTO.class);
		binder.setBean(formDTO);

		TextField nameField = new TextField("Název");
		nameField.setWidth("100%");
		binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
		winLayout.add(nameField);

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidth("100%");
		descriptionField.setHeight("200px");
		binder.forField(descriptionField).bind("description");
		winLayout.add(descriptionField);

		TextField originField = new TextField("Původ hry");
		originField.setWidth("100%");
		binder.forField(originField).bind("origin");
		winLayout.add(originField);

		TextField playersField = new TextField("Počet hráčů");
		playersField.setWidth("100%");
		binder.forField(playersField).bind("players");
		winLayout.add(playersField);

		TextField playTimeField = new TextField("Délka hry");
		playTimeField.setWidth("100%");
		binder.forField(playTimeField).bind("playTime");
		winLayout.add(playTimeField);

		TextField preparationTimeField = new TextField("Délka přípravy");
		preparationTimeField.setWidth("100%");
		binder.forField(preparationTimeField).bind("preparationTime");
		winLayout.add(preparationTimeField);

		TokenField keywords = new TokenField(getCampgameService().getAllCampgameKeywordNames());
		keywords.isEnabled();
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		if (originalDTO != null)
			for (String keyword : originalDTO.getKeywords())
				keywords.addToken(keyword);
		winLayout.add(keywords);

		Button createBtn;
		createBtn = new Button("Uložit", e -> {
			try {
				CampgameTO writeDTO = originalDTO == null ? new CampgameTO() : originalDTO;
				binder.writeBean(writeDTO);
				writeDTO.setKeywords(keywords.getValues());
				writeDTO.setId(getCampgameService().saveCampgame(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ve) {
				new ErrorDialog(
						"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage())
								.open();
			} catch (Exception ve) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		});
		layout.add(createBtn);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setCloseOnEsc(false);
	}

	protected abstract void onSuccess(CampgameTO dto);

}
