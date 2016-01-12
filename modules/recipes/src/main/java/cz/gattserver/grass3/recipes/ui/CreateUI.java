package cz.gattserver.grass3.recipes.ui;

import javax.servlet.http.HttpServletRequest;

import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.in.impl.Form;
import cz.gattserver.grass3.wexp.in.impl.Label;
import cz.gattserver.grass3.wexp.in.impl.Link;
import cz.gattserver.grass3.wexp.in.impl.SubmitButton;
import cz.gattserver.grass3.wexp.in.impl.TextArea;
import cz.gattserver.grass3.wexp.in.impl.TextInput;
import cz.gattserver.grass3.wexp.in.impl.UI;

public class CreateUI extends AbstractUI {

	private static final long serialVersionUID = 3785137433928397710L;

	public CreateUI(UI mainUI, UI prevUI) {
		this(mainUI, prevUI, null, null);
	}

	public CreateUI(UI mainUI, UI prevUI, Long editId) {
		this(mainUI, prevUI, editId, null);
	}

	public CreateUI(UI mainUI, UI prevUI, boolean formResult) {
		this(mainUI, prevUI, null, formResult);
	}

	private CreateUI(UI mainUI, UI prevUI, Long editId, Boolean formResult) {

		Label headerLabel = new Label(editId == null ? "přidat recept" : "upravit recept");
		headerLabel.setCSSClass("recepty-centered-header");
		layout.addChild(headerLabel);

		if (formResult != null) {
			Label resultLabel = new Label(formResult == true ? "Uložení dopadlo úspěšně" : "Uložení se nezdařilo");
			layout.addChild(resultLabel);
		}

		Label nameLabel = new Label("Název");
		TextInput nameInput = new TextInput("name");
		nameInput.setWidth("100%");

		Label descLabel = new Label("Popis");
		TextArea descArea = new TextArea("desc");
		descArea.setWidth("100%");
		descArea.setHeight("200px");

		if (editId != null) {
			RecipeDTO dto = facade.getRecipeById(editId);
			nameInput.setValue(dto.getName());
			descArea.setValue(facade.breaklineToEol(dto.getDescription()));
		}

		SubmitButton button = new SubmitButton("Uložit");

		Form form = new Form(new DispatchAction() {
			private static final long serialVersionUID = -3976758813255848523L;

			@Override
			public UI dispatch(HttpServletRequest req) {

				String nameValue = req.getParameter(nameInput.getName());
				String descValue = req.getParameter(descArea.getName());

				boolean result = facade.saveRecipe(nameValue, descValue, editId);

				if (editId != null && result) {
					return new DetailUI(mainUI, new ListUI(mainUI, mainUI), editId);
				} else {
					return new CreateUI(mainUI, prevUI, result);
				}
			}
		});
		layout.addChild(form);
		form.addChild(nameLabel);
		form.addChild(nameInput);
		form.addChild(descLabel);
		form.addChild(descArea);
		form.addChild(button);

		Link backLink;
		layout.addChild(backLink = new Link("zpět", new DispatchAction() {
			private static final long serialVersionUID = -2550135641464964288L;

			@Override
			public UI dispatch(HttpServletRequest req) {
				return prevUI;
			}
		}));
		backLink.setCSSClass("back-item");
	}
}
