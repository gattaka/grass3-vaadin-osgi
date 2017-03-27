package cz.gattserver.grass3.recipes.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import cz.gattserver.grass3.recipes.facades.IRecipesFacade;
import cz.gattserver.grass3.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass3.template.CreateButton;
import cz.gattserver.grass3.template.ModifyButton;
import cz.gattserver.web.common.window.WebWindow;

public abstract class RecipeWindow extends WebWindow {

	private static final long serialVersionUID = 6803519662032576371L;

	@Autowired
	private IRecipesFacade recipesFacade;

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
		addComponent(name);

		final TextArea desc = new TextArea("Popis");
		desc.setWidth("100%");
		desc.setHeight("500px");
		if (to != null)
			desc.setValue(recipesFacade.breaklineToEol(to.getDescription()));
		addComponent(desc);

		Button b;
		if (to != null)
			addComponent(b = new ModifyButton() {
				private static final long serialVersionUID = 1669142984184558228L;

				@Override
				public void onClick(ClickEvent event) {
					onSave(name.getValue(), desc.getValue(), to.getId());
					close();
				}
			});
		else {
			addComponent(b = new CreateButton() {
				private static final long serialVersionUID = 4632843818680202003L;

				@Override
				public void onClick(ClickEvent event) {
					onSave(name.getValue(), desc.getValue(), null);
					close();
				}
			});
		}
		setComponentAlignment(b, Alignment.MIDDLE_CENTER);
	}

}
