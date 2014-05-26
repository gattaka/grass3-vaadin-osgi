package org.myftp.gattserver.grass3.pages.err.factories;

import org.myftp.gattserver.grass3.pages.err.Err404Page;
import org.myftp.gattserver.grass3.pages.factories.template.AbstractPageFactory;
import org.myftp.gattserver.grass3.pages.template.IGrassPage;
import org.myftp.gattserver.grass3.ui.util.GrassRequest;
import org.springframework.stereotype.Component;

@Component("err404Factory")
public class Err404Factory extends AbstractPageFactory {

	private static final long serialVersionUID = 1538011525952923446L;

	public Err404Factory() {
		super("err404");
	}

	@Override
	protected boolean isAuthorized() {
		return true;
	}

	@Override
	protected IGrassPage createPage(GrassRequest request) {
		return new Err404Page(request);
	}
}
