package cz.gattserver.grass3.wexp.in.impl;

import java.util.HashMap;
import java.util.Map;

import cz.gattserver.grass3.wexp.DispatchAction;
import cz.gattserver.grass3.wexp.Dispatcher;
import cz.gattserver.grass3.wexp.in.Component;
import cz.gattserver.grass3.wexp.out.WebElement;
import cz.gattserver.grass3.wexp.out.impl.DivElement;
import cz.gattserver.grass3.wexp.out.impl.LinkElement;
import cz.gattserver.grass3.wexp.out.impl.TextElement;
import cz.gattserver.grass3.wexp.servlet.WexpServlet;

public class Link extends Component {

	private String caption;
	private String address;
	private DispatchAction action;
	private String target;

	private Map<String, String> params;

	public Link setParam(String name, String value) {
		if (params == null)
			params = new HashMap<String, String>();
		params.put(name, value);
		return this;
	}

	public String getParam(String name) {
		if (params == null)
			return null;
		return params.get(name);
	}

	public Link(String caption, String address) {
		setCaption(caption);
		this.address = address;
	}

	public Link(String caption, DispatchAction action) {
		setCaption(caption);
		this.action = action;
	}

	public String getCaption() {
		return caption;
	}

	public Link setCaption(String caption) {
		this.caption = caption;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public Link setAddress(String address) {
		this.action = null;
		this.address = address;
		return this;
	}

	public DispatchAction getAction() {
		return action;
	}

	public Link setAction(DispatchAction action) {
		this.address = null;
		this.action = action;
		return this;
	}

	public String getTarget() {
		return target;
	}

	public Link setTarget(String target) {
		this.target = target;
		return this;
	}

	@Override
	public WebElement construct() {
		DivElement div = new DivElement();
		div.setClass("grass-label");
		String paramAddress = "";
		if (address != null) {
			paramAddress = address;
		} else {
			paramAddress = "?" + Dispatcher.ACTION_PARAM_NAME + "="
					+ WexpServlet.getDispatcher().addActionAndCreateUUID(action).toString();
		}
		LinkElement linkElement = new LinkElement(paramAddress);
		linkElement.setTarget(target);
		linkElement.addChild(new TextElement(caption));
		div.addChild(linkElement);
		return div;
	}

}
