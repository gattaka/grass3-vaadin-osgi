package cz.gattserver.grass3.recipes.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import cz.gattserver.grass3.recipes.facades.RecipesService;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.ui.components.button.CreateButton;
import cz.gattserver.grass3.ui.components.button.ModifyButton;
import cz.gattserver.web.common.ui.window.WebDialog;

public abstract class RecipeWindow extends WebDialog {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private RecipesService recipesFacade;

	public RecipeWindow() {
		this(null);
	}

	protected abstract void onSave(String name, String desc, Long id);

	public RecipeWindow(final RecipeDTO to) {
		super(to == null ? "Založit" : "Upravit" + " recept");

		setWidth("600px");

		final TextField name = new TextField("Název");
		name.setWidth("100%");
		if (to != null)
			name.setValue(to.getName());
		add(name);

		final TextArea desc = new TextArea("Popis");
		desc.setWidth("100%");
		desc.setHeight("500px");
		if (to != null)
			desc.setValue(recipesFacade.breaklineToEol(to.getDescription()));
		add(desc);

		if (to != null)
			add(new ModifyButton(event -> {
				onSave(name.getValue(), desc.getValue(), to.getId());
				close();
			}));
		else {
			add(new CreateButton(event -> {
				onSave(name.getValue(), desc.getValue(), null);
				close();
			}));
		}
	}

}
