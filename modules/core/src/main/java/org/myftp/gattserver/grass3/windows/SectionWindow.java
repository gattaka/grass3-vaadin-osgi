package org.myftp.gattserver.grass3.windows;

import java.net.URL;
import java.util.Map;

import org.myftp.gattserver.grass3.windows.template.TwoColumnWindow;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class SectionWindow extends TwoColumnWindow {

	private static final long serialVersionUID = 2474374292329895766L;

	private Label path;
	
	public SectionWindow() {
		setName("section");
		setCaption("Gattserver");
	}

	@Override
	protected void createLeftColumnContent(HorizontalLayout layout) {
	}

	@Override
	protected void createRightColumnContent(HorizontalLayout layout) {
		path = new Label(""); 
		layout.addComponent(path);
	}

	@Override
	public DownloadStream handleURI(URL context, String relativeUri) {
		
		// postačí naparsovat relativeUri a je z toho cesta :)
		path.setValue(relativeUri);
		
		return super.handleURI(context, relativeUri);
	}

	@Override
	public void handleParameters(Map<String, String[]> parameters) {
		
		// parameters jsou přímo v mapě :)
		
		super.handleParameters(parameters);
	}

}
