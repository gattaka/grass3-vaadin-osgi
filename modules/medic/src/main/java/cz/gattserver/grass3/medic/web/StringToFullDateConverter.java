package cz.gattserver.grass3.medic.web;

import cz.gattserver.grass3.ui.util.StringToDateConverter;

public class StringToFullDateConverter extends StringToDateConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	public StringToFullDateConverter() {
		super("d. MMMMM yyyy, H:mm");
	}

}
