package org.myftp.gattserver.grass3.backup.domain;

import org.myftp.gattserver.grass3.hw.domain.HWItem;

/**
 * Disk, flashka, jakékoliv úložiště ze kterého/na které se zálohuje
 */
public class Storage {

	/**
	 * Identifikátor hw
	 */
	private long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Fyzická jednotka, na které je úložiště
	 */
	private HWItem hwItem;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HWItem getHwItem() {
		return hwItem;
	}

	public void setHwItem(HWItem hwItem) {
		this.hwItem = hwItem;
	}

}
