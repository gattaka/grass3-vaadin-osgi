package org.myftp.gattserver.grass3.windows.template;

import com.vaadin.ui.Window;

/**
 * Kořenový předek všech oken, která lze zobrazit v Grass systému
 * 
 * @author gatt
 *
 */
public abstract class GrassWindow extends Window {

	private static final long serialVersionUID = 8889472078008074552L;

	protected abstract void buildLayout();
	
}
