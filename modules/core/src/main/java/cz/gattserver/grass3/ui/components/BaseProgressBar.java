package cz.gattserver.grass3.ui.components;

import com.vaadin.ui.ProgressBar;

public class BaseProgressBar extends ProgressBar {

	private static final long serialVersionUID = 9190321446320873139L;

	private int total;
	private int current;

	/**
	 * 
	 * @param total
	 *            celkový počet elementů ke zpracování
	 */
	public BaseProgressBar(int total) {
		// +1 protože se musí započítat i samotné
		// generování procesu (jinak se bude dělit v
		// případě, že není co dělat 0)
		this.total = total + 1;
		current = 0;
	}

	public int getTotal() {
		return total;
	}

	/**
	 * inkrementuje stav
	 * 
	 * @return <code>true</code> pokud je to poslední increment, který byl
	 *         plánován (dle total count)
	 */
	public boolean increaseProgress() {
		current++;
		getUI().access(new Runnable() {
			@Override
			public void run() {
				BaseProgressBar.this.setValue(getProgress());
			}
		});
		return current == total - 1;
	}

	/**
	 * @return procentuální stav hotové práce
	 */
	public float getProgress() {
		return (float) current / total;
	}

}
