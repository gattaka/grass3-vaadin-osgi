package cz.gattserver.grass3.campgames.ui.windows;

import java.util.HashSet;
import java.util.Set;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class CampgameCreateWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameCreateWindow(Long originalId) {
		init(originalId == null ? null : getCampgameService().getCampgame(originalId));
	}

	public CampgameCreateWindow() {
		init(null);
	}

	public CampgameCreateWindow(CampgameTO originalDTO) {
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
		setCaption(originalDTO == null ? "Založení nové hry" : "Oprava údajů existující hry");

		CampgameTO formDTO = new CampgameTO();
		formDTO.setName("");

		GridLayout winLayout = new GridLayout(2, 5);
		layout.addComponent(winLayout);
		winLayout.setWidth("400px");
		winLayout.setSpacing(true);

		Binder<CampgameTO> binder = new Binder<>(CampgameTO.class);
		binder.setBean(formDTO);

		TextField nameField = new TextField("Název");
		nameField.setWidth("100%");
		binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
		winLayout.addComponent(nameField, 0, 0, 1, 0);

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setWidth("100%");
		descriptionField.setHeight("200px");
		binder.forField(descriptionField).bind("description");
		winLayout.addComponent(descriptionField, 0, 1, 1, 1);

		TextField originField = new TextField("Původ hry");
		originField.setWidth("100%");
		binder.forField(originField).bind("origin");
		winLayout.addComponent(originField, 0, 2);

		TextField playersField = new TextField("Počet hráčů");
		playersField.setWidth("100%");
		binder.forField(playersField).bind("players");
		winLayout.addComponent(playersField, 1, 2);

		TextField playTimeField = new TextField("Délka hry");
		playTimeField.setWidth("100%");
		binder.forField(playTimeField).bind("playTime");
		winLayout.addComponent(playTimeField, 0, 3);

		TextField preparationTimeField = new TextField("Délka přípravy");
		preparationTimeField.setWidth("100%");
		binder.forField(preparationTimeField).bind("preparationTime");
		winLayout.addComponent(preparationTimeField, 1, 3);

		AdvancedTokenField keywords = new AdvancedTokenField();
		keywords.isEnabled();
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		Set<CampgameKeywordTO> contentTypes = getCampgameService().getAllCampgameKeywords();
		contentTypes.forEach(t -> {
			Token to = new Token(t.getName());
			keywords.addTokenToInputField(to);
		});

		if (originalDTO != null)
			for (String keyword : originalDTO.getKeywords())
				keywords.addToken(new Token(keyword));
		winLayout.addComponent(keywords, 0, 4, 1, 4);

		Button createBtn;
		createBtn = new Button("Uložit", e -> {
			try {
				CampgameTO writeDTO = originalDTO == null ? new CampgameTO() : originalDTO;
				binder.writeBean(writeDTO);
				Set<String> keywordsTokens = new HashSet<>();
				keywords.getTokens().forEach(t -> keywordsTokens.add(t.getValue()));
				writeDTO.setKeywords(keywordsTokens);
				writeDTO.setId(getCampgameService().saveCampgame(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ve) {
				Notification.show(
						"Chybná vstupní data\n\n   " + ve.getValidationErrors().iterator().next().getErrorMessage(),
						Notification.Type.ERROR_MESSAGE);
			} catch (Exception ve) {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
		});
		layout.addComponent(createBtn);
		layout.setComponentAlignment(createBtn, Alignment.BOTTOM_RIGHT);
		setContent(layout);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		removeAllCloseShortcuts();
	}

	protected abstract void onSuccess(CampgameTO dto);

}
