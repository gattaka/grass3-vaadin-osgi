package cz.gattserver.grass3.pages.template;

public class GridUtils {

	public static final int ICON_COLUMN_WIDTH = 15 + 16 + 15;
	
	public static int processHeight(int dataAmount) {
		int element = 31;
		int header = 31;
		int min = header + 3 * element;
		int max = 15 * element + header;

		int size = dataAmount * element;

		if (size < min)
			size = min;
		if (size > max)
			size = max;
		size += header;
		return size;
	}

}
