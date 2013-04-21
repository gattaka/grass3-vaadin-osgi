package org.myftp.gattserver.grass3.backup.domain;

/**
 * Složka, adresář, oddíl - nějaká položka, která se zálohuje
 */
public class BackupItem {

	/**
	 * Identifikátor hw
	 */
	private long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Úložiště ze kterého se položka načítá
	 */
	private Storage storage;

	/**
	 * Úložiště na které se zálohuje
	 */
	private Storage backupStorage;

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

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Storage getBackupStorage() {
		return backupStorage;
	}

	public void setBackupStorage(Storage backupStorage) {
		this.backupStorage = backupStorage;
	}

}
