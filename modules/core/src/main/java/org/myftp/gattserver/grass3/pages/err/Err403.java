package org.myftp.gattserver.grass3.pages.err;

import org.myftp.gattserver.grass3.pages.template.ErrorPage;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

@org.springframework.stereotype.Component("err403")
@Scope("prototype")
public class Err403 extends ErrorPage {

	public Err403(GrassRequest request) {
		super(request);
	}

	private static final long serialVersionUID = 3728073040878360420L;

	@Override
	protected String getErrorText() {
		return "403 - Nemáte oprávnění k provedení této operace";
	}

	@Override
	protected Resource getErrorImage() {
		return new ThemeResource("img/403.png");
	}

}
