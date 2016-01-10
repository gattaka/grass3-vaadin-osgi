package cz.gattserver.grass3.wexp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.gattserver.grass3.wexp.in.impl.UI;

public class Dispatcher {

	public static final String ACTION_PARAM_NAME = "action";

	private static final Map<String, Dispatcher> sessionInstance = new HashMap<String, Dispatcher>();

	public static Dispatcher getSessionInstance(String id) {
		Dispatcher instance = sessionInstance.get(id);
		if (instance == null) {
			instance = new Dispatcher();
			sessionInstance.put(id, instance);
		}
		return instance;
	}

	private Dispatcher() {
	}

	private Map<Integer, DispatchAction> actionMap = new HashMap<Integer, DispatchAction>();
	private UI mainUI;

	public void setMainUI(UI mainUI) {
		this.mainUI = mainUI;
	}

	// TODO ... jednu akci můžu použít i dvakrát -- měl bych kontrolovat, kdy se která zavolá
	public Integer addActionAndCreateUUID(DispatchAction action) {
		Integer hash = action.hashCode();
		actionMap.put(hash, action);
		System.out.println("actionMap size: " + actionMap.size());
		return hash;
	}

	public UI getMainUI() {
		return mainUI;
	}

	public void write(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		OutputStream out = resp.getOutputStream();

		String actionHash = req.getParameter(ACTION_PARAM_NAME);

		if (actionHash != null) {
			Integer hash = Integer.parseInt(actionHash);
			DispatchAction action = actionMap.get(hash);
			if (action == null) {
				// TODO 404
			} else {
				UI ui = action.dispatch();
				if (ui == null) {
					throw new IllegalStateException("Action result UI was not set");
				} else {
					ui.construct().write(out);
				}
			}
		} else {
			if (mainUI == null) {
				throw new IllegalStateException("Main UI was not set");
			} else {
				mainUI.construct().write(out);
			}
		}
		out.flush();
		out.close();
	}

}
