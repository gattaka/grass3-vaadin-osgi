package cz.gattserver.grass3.ui.progress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;

public abstract class PooledProgressBar extends ProgressBar {

	private static final long serialVersionUID = 9190321446320873139L;

	private static final int THREAD_POOL_SIZE = 10;
	private static ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	private ProgressThread progressThread;

	private int total;
	private int current;

	/**
	 * Je voláno vláknem v hlavní metodě - zde se provádí činnost, které
	 * odpovídá progressbar
	 * 
	 * @param thread
	 *            pracující vlákno, ve kterém je operace spuštěna - je možné na
	 *            něm volat metodu {@code increaseProgress() }, kterou se
	 *            zvyšuje čítač zpracovaných elementů a tím i stav progressbaru
	 */
	protected abstract void process(ProgressThread thread);

	/**
	 * Je-li potřeba ještě jinde indikovat stav operace, dá se přetížit tato
	 * metoda, která je volána při každém posuvu progressbaru
	 * 
	 * @param progress
	 *            procentuální stav operace
	 */
	protected void indicateProgress(float progress) {
	};

	/**
	 * 
	 * @param total
	 *            celkový počet elementů ke zpracování
	 */
	public PooledProgressBar(int total) {
		this.total = total + 1; // +1 protože se musí započítat i samotné
								// generování procesu (jinak se bude dělit 0)
		progressThread = new ProgressThread();
		executor.execute(progressThread);
	}

	public class ProgressThread extends Thread {

		@Override
		public void run() {
			current = 0;
			process(this);
		}

		/**
		 * inkrementuje stav
		 */
		public void increaseProgress() {
			current++;

			UI ui = getUI();

			// UI connectorUI = getUI();
			// UI currentUI = UI.getCurrent();
			// if (connectorUI.equals(currentUI)) {
			// System.out.println("OK");
			// } else {
			// System.out.println("WTF");
			// }

			VaadinSession session = getSession();
			if (session == null) {
				incrementState();
			} else {
				ui.access(new Runnable() {
					@Override
					public void run() {
						incrementState();
					}
				});
			}
		}

		private void incrementState() {
			PooledProgressBar.this.setValue(getProgress());
			indicateProgress(getProgress());
		}

		/**
		 * @return procentuální stav hotové práce
		 */
		private float getProgress() {
			return (float) current / total;
		}

	}

	public ProgressThread getProgressThread() {
		return progressThread;
	}
}