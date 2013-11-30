package org.myftp.gattserver.grass3.ui.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Speciální úprava data-stringu elementu pro tabulky, které potřebují, aby byl
 * typ v daném sloupci {@link Comparable} jinak sloupec nejde řadit (nelze řadit
 * podle něj) - v případě data je potřeba vypisovat formátované datum, ale
 * porovnávat ho jako datum, nikoliv jako string
 * 
 * @author Gattaka
 * 
 */
public class ComparableStringDate implements Comparable<ComparableStringDate> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"d.M.yyyy HH:mm:ss");

	private Date date;
	private String stringDate;

	public ComparableStringDate(Date date) {
		this.date = date;
		this.stringDate = date == null ? "" : dateFormat.format(date);
	}

	@Override
	public String toString() {
		return stringDate;
	}

	public Date getDate() {
		return date;
	}

	public int compareTo(ComparableStringDate o) {
		return date == null ? -1 : this.getDate().compareTo(o.getDate());
	}

}
