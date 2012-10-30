package org.myftp.gattserver.grass3.template;

import java.io.UnsupportedEncodingException;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.ui.LoginForm;

public class GrassLoginForm extends LoginForm {

	private static final long serialVersionUID = 5799011321738902785L;

	private int buttonTopSpacing = 18;
	private int textFieldTopSpacing = 7;

	@Override
	protected byte[] getLoginHTML() {
		String appUri = getApplication().getURL().toString()
				+ getWindow().getName() + "/";

		try {
			return ("<!DOCTYPE html PUBLIC \"-//W3C//DTD "
					+ "XHTML 1.0 Transitional//EN\" "
					+ "\"http://www.w3.org/TR/xhtml1/"
					+ "DTD/xhtml1-transitional.dtd\">\n" + "<html>"
					+ "<head><script type='text/javascript'>"
					+ "var setTarget = function() {" + "var uri = '"
					+ appUri
					+ "loginHandler"
					+ "'; var f = document.getElementById('loginf');"
					+ "document.forms[0].action = uri;document.forms[0].username.focus();};"
					+ ""
					+ "var styles = window.parent.document.styleSheets;"
					+ "for(var j = 0; j < styles.length; j++) {\n"
					+ "if(styles[j].href) {"
					+ "var stylesheet = document.createElement('link');\n"
					+ "stylesheet.setAttribute('rel', 'stylesheet');\n"
					+ "stylesheet.setAttribute('type', 'text/css');\n"
					+ "stylesheet.setAttribute('href', styles[j].href);\n"
					+ "document.getElementsByTagName('head')[0].appendChild(stylesheet);\n"
					+ "}"
					+ "}\n"
					+ "function submitOnEnter(e) { var keycode = e.keyCode || e.which;"
					+ " if (keycode == 13) {document.forms[0].submit();}  } \n"
					+ "</script>"
					+ "</head><body onload='setTarget();' style='margin:0;padding:0; background:transparent;' class=\""
					+ ApplicationConnection.GENERATED_BODY_CLASSNAME
					+ "\">"
					+ "<div class='v-app v-app-loginpage' style=\"background:transparent;\">"
					+ "<iframe name='logintarget' style='width:0;height:0;"
					+ "border:0;margin:0;padding:0;'></iframe>"
					+ "<form id='loginf' target='logintarget' onkeypress=\"submitOnEnter(event)\" method=\"post\">"
					+ "<div>"
					+ getUsernameCaption()
					+ "</div><div style='padding-bottom: "
					+ textFieldTopSpacing
					+ "px;'>"
					+ "<input class='v-textfield' style='display:block;' type='text' name='username'></div>"
					+ "<div>"
					+ getPasswordCaption()
					+ "</div>"
					+ "<div style='padding-bottom: "
					+ buttonTopSpacing
					+ "px;'><input class='v-textfield' style='display:block;' type='password' name='password'></div>"
					+ "<div><div onclick=\"document.forms[0].submit();\" tabindex=\"0\" class=\"v-button\" role=\"button\" ><span class=\"v-button-wrap\"><span class=\"v-button-caption\">"
					+ getLoginButtonCaption()
					+ "</span></span></div></div></form></div>" + "</body></html>")
					.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 encoding not avalable", e);
		}
	}

	public int getButtonTopSpacing() {
		return buttonTopSpacing;
	}

	public void setButtonTopSpacing(int buttonTopSpacing) {
		this.buttonTopSpacing = buttonTopSpacing;
	}

	public int getTextFieldTopSpacing() {
		return textFieldTopSpacing;
	}

	public void setTextFieldTopSpacing(int textFieldTopSpacing) {
		this.textFieldTopSpacing = textFieldTopSpacing;
	}

}
