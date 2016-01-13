package cz.gattserver.grass3.recipes.ui;

import java.util.List;

import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.servlet.WebApp;

public class RecipesWebApp implements WebApp {

	@Override
	public void init() {
	}

	@Override
	public UI createMainUI() {
		return new MainUI();
	}

	@Override
	public UI createUI(List<String> pathChunks) {
		switch (pathChunks.get(0)) {
		default:
			return new MainUI();
		case ListUI.PATH:
			return new ListUI();
		case DetailUI.PATH:
			return new DetailUI(pathChunks.get(1));
		case CreateUI.PATH:
			return new CreateUI();
		}
	}

}
