package sandbox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import sandbox.EditorPage.EditorPageFactory;
import sandbox.HomePage.HomePageFactory;
import sandbox.ViewPage.ViewPageFactory;
import sandbox.interfaces.IPageFactory;
import sandbox.util.GrassRequest;
import sandbox.util.PageFactoriesMap;
import sandbox.util.URLPathAnalyzer;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * The Application's "main" class
 */
@Title("Gattserver")
@Theme("grass")
@Scope("prototype")
@Component("grassUI")
public class MyVaadinUI extends UI {

	private static final long serialVersionUID = -7296936167498820319L;
	private static Logger logger = LoggerFactory.getLogger(MyVaadinUI.class);

	PageFactoriesMap factoriesMap = new PageFactoriesMap(
			HomePageFactory.INSTANCE);

	public MyVaadinUI() {
		factoriesMap.put(EditorPageFactory.INSTANCE);
		factoriesMap.put(ViewPageFactory.INSTANCE);
	}

	@Override
	protected void init(VaadinRequest request) {

		String path = request.getPathInfo();
		logger.info("Path: [" + path + "]");

		GrassRequest grassRequest = new GrassRequest(request);
		URLPathAnalyzer analyzer = grassRequest.getAnalyzer();

		IPageFactory factory = factoriesMap.get(analyzer.getPathToken(0));
		setContent(factory.createPage(grassRequest));

		VaadinSession.getCurrent().addRequestHandler(new RequestHandler() {

			private static final long serialVersionUID = 7154339775034959876L;

			@Override
			public boolean handleRequest(VaadinSession session,
					VaadinRequest request, VaadinResponse response)
					throws IOException {

				if ("/vaadin-sandbox/image".equals(request.getPathInfo())) {

					File file = new File(
							"/home/gatt/Downloads/user_male_portrait.png");
					InputStream in = new BufferedInputStream(
							new FileInputStream(file));

					byte[] buffer = new byte[1024];

					int bytesRead = in.read(buffer);
					while (bytesRead > -1) {
						response.getOutputStream().write(buffer, 0, bytesRead);
						bytesRead = in.read(buffer);
					}

					response.setContentType("image/png png");

					return true; // We wrote a response
				} else
					return false; // No response was written
			}
		});

	}
}
