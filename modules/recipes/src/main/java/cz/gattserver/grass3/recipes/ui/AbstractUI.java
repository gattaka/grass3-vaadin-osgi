package cz.gattserver.grass3.recipes.ui;

import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.grass3.SpringContextHelper;
import cz.gattserver.grass3.recipes.facades.IRecipeFacade;
import cz.gattserver.grass3.wexp.in.impl.CSSRule;
import cz.gattserver.grass3.wexp.in.impl.UI;
import cz.gattserver.grass3.wexp.in.impl.VerticalLayout;

public class AbstractUI extends UI {

	@Autowired
	protected IRecipeFacade facade;

	protected VerticalLayout layout = new VerticalLayout();

	public AbstractUI() {

		SpringContextHelper.inject(this);

		// body CSS
		CSSRule bodyCSS = new CSSRule("body");
		bodyCSS.setStyle("font-family", "sans-serif");
		bodyCSS.setStyle("font-size", "25px");
		setClass(bodyCSS);

		// recepty-header CSS
		CSSRule receptyHeaderCSS = new CSSRule(".recepty-header.grass-label");
		receptyHeaderCSS.setStyle("font-size", "35px");
		receptyHeaderCSS.setStyle("margin-bottom", "15px");
		setClass(receptyHeaderCSS);

		setContent(layout);
		layout.setWidth("800px");

	}
}
