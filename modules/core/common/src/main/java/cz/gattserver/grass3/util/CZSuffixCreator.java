package cz.gattserver.grass3.util;

public class CZSuffixCreator {

	private String suf1, suf2to4, suf5AndMore;

	public CZSuffixCreator(String suf1, String suf2to4, String suf5AndMore) {
		this.suf1 = suf1;
		this.suf2to4 = suf2to4;
		this.suf5AndMore = suf5AndMore;
	}

	public String createStringWithSuffix(Integer number) {
		if (number == null)
			return "-";
		switch (number) {
		case 0:
			return "-";
		case 1:
			return number + " " + suf1;
		case 2:
		case 3:
		case 4:
			return number + " " + suf2to4;
		default:
			return number + " " + suf5AndMore;
		}
	}

}
