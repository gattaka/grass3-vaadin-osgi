package cz.gattserver.grass3.wexp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass3.recipes.ui.MainUI;
import cz.gattserver.grass3.wexp.Dispatcher;

public class WexpServlet extends HttpServlet {
	private static final long serialVersionUID = 7172666112080085629L;

	public static final ThreadLocal<Dispatcher> threadLocal = new ThreadLocal<Dispatcher>();

	@Override
	public void init() throws ServletException {
		super.init();
	}

	public static Dispatcher getDispatcher() {
		return threadLocal.get();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// získej dispatcher dle session (každá session má jeden dispatcher)
		Dispatcher disp = Dispatcher.getSessionInstance(req.getSession().getId());
		// aby byl dostupný z celého kontextu vlákna
		threadLocal.set(disp);

		if (disp.getMainUI() == null) {
			disp.setMainUI(new MainUI());
		}

		disp.write(req, resp);
	}
}
