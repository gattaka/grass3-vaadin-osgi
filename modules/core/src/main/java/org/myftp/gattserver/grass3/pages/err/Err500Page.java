package org.myftp.gattserver.grass3.pages.err;

import org.myftp.gattserver.grass3.pages.template.ErrorPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

@org.springframework.stereotype.Component("err500")
@Scope("prototype")
public class Err500Page extends ErrorPage {

	public Err500Page(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = -2679323424889989397L;

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}
	
	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/500.png");
	}

}
