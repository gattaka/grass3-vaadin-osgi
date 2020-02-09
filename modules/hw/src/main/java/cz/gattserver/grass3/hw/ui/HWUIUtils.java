package cz.gattserver.grass3.hw.ui;

import cz.gattserver.grass3.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.web.common.ui.ImageIcon;

public class HWUIUtils {

	public static ImageIcon chooseImageIcon(HWItemOverviewTO to) {
		if (to.getState() == null)
			return null;
		switch (to.getState()) {
		case FIXED:
			return ImageIcon.INFO_16_ICON;
		case FAULTY:
			return ImageIcon.WARNING_16_ICON;
		case BROKEN:
			return ImageIcon.DELETE_16_ICON;
		case DISASSEMBLED:
			return ImageIcon.TRASH_16_ICON;
		case NOT_USED:
			return ImageIcon.CLOCK_16_ICON;
		case NEW:
		default:
			return null;
		}
	}
}
