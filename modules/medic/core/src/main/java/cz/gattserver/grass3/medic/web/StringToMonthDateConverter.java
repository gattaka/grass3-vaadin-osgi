package cz.gattserver.grass3.medic.web;

import cz.gattserver.grass3.ui.util.StringToDateConverter;

public class StringToMonthDateConverter extends StringToDateConverter {
	private static final long serialVersionUID = -2914696445291603483L;

	public StringToMonthDateConverter() {
		super("MMMMM yyyy");
	}

}
