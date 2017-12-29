package cz.gattserver.grass3.server;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import com.vaadin.server.VaadinServlet;

import cz.gattserver.web.common.spring.SpringContextHelper;

public class GrassVaadinServlet extends VaadinServlet {

	private static final long serialVersionUID = -5411796850116104693L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * FIX pro registrování requestHandlerů - aktuální implementace
		 * nefungovala správně, protože registrovala handlery v UI init metodě,
		 * která se provádí pozdě a navíc registruje handlery opakovaně
		 */
		// https://vaadin.com/old-forum/-/message_boards/view_message/3535024
		// https://vaadin.com/forum#!/thread/3072331
		// https://vaadin.com/book/vaadin7/-/page/advanced.requesthandler.html
		getService().addSessionInitListener(event -> {
			ApplicationContext context = SpringContextHelper.getContext();
			if (context != null) {
				Collection<GrassRequestHandler> grassRequestHandlers = context.getBeansOfType(GrassRequestHandler.class)
						.values();
				for (GrassRequestHandler requestHandler : grassRequestHandlers)
					event.getSession().addRequestHandler(requestHandler);
			}
		});

		super.service(request, response);
	}

}
