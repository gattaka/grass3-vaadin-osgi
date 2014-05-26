package org.myftp.gattserver.grass3.pages.err.factories;

import org.myftp.gattserver.grass3.pages.err.Err500Page;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("err500Factory")
public class Err500Factory extends AbstractPageFactory {

	private static final long serialVersionUID = -8588396224579592218L;

	public Err500Factory() {
		super("err500");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new Err500Page(request);
	}
}
