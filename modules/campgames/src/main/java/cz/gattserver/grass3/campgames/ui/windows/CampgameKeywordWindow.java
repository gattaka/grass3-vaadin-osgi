package cz.gattserver.grass3.campgames.ui.windows;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.service.CampgamesService;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.window.ErrorWindow;
import cz.gattserver.web.common.ui.window.WebWindow;

public abstract class CampgameKeywordWindow extends WebWindow {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameKeywordWindow(CampgameKeywordTO originalDTO) {
		init(originalDTO);
	}

	public CampgameKeywordWindow() {
		init(null);
	}

	@Override
	protected void addCloseShortCut() {
	}
	
	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	public void init(CampgameKeywordTO originalDTO) {
		setCaption("Úprava klíčového slova");

		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setMargin(true);
		winLayout.setSpacing(true);

		CampgameKeywordTO formDTO = new CampgameKeywordTO();
		formDTO.setName("");
		Binder<CampgameKeywordTO> binder = new Binder<>(CampgameKeywordTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		binder.bind(nameField, "name");

		winLayout.addComponent(nameField);
		winLayout.addComponent(new Button("Uložit", e -> {
			try {
				CampgameKeywordTO writeDTO = originalDTO == null ? new CampgameKeywordTO() : originalDTO;
				binder.writeBean(writeDTO);
				getCampgamesService().saveCampgameKeyword(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ex) {
				Notification.show("   Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next(),
						Notification.Type.TRAY_NOTIFICATION);
			} catch (Exception ex) {
				UI.getCurrent().addWindow(new ErrorWindow("Uložení se nezdařilo"));
			}
		}));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setContent(winLayout);
	}

	protected abstract void onSuccess(CampgameKeywordTO campgameKeywordTO);

}
