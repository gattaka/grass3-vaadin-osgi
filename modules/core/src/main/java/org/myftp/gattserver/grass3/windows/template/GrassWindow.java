package org.myftp.gattserver.grass3.windows.template;

import java.net.URL;

import com.vaadin.terminal.DownloadStream;
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
	
	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {
		onShow();
		return super.handleURI(context, relativeUri);
	}
	
	protected abstract void onShow();
	
}
