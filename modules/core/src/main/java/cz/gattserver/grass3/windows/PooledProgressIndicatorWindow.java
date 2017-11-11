package cz.gattserver.grass3.windows;

import java.text.DecimalFormat;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.components.PooledProgressBar;
import cz.gattserver.grass3.components.PooledProgressBar.ProgressThread;

public abstract class PooledProgressIndicatorWindow extends Window {

	private static final long serialVersionUID = 2779568469991016255L;
	private static DecimalFormat myFormatter = new DecimalFormat("##0.0");

	private final PooledProgressBar progressbar;

	private int total;

	/**
	 * Je voláno vláknem v hlavní metodě - zde se provádí činnost, které
	 * odpovídá progressbar
	 * 
	 * @param thread
	 *            pracující vlákno, ve kterém je operace spuštěna - je možné na
	 *            něm volat metodu {@code increaseProgress() }, kterou se
	 *            zvyšuje čítač zpracovaných elementů a tím i stav progressbaru
	 */
	protected abstract void process(final ProgressThread thread);

	/**
	 * Volá se při dokončení operace a zavření progressokna
	 */
	protected void onClose() {
		UI.getCurrent().setPollInterval(-1);
	}

	protected int getTotal() {
		return total;
	}

	public PooledProgressIndicatorWindow(int total) {
		this(null, total);
	}

	public void closeOnDone() {
		VaadinSession session = getSession();
		if (session == null) {
			setClosable(true);
			close();
		} else {
			getUI().access(new Runnable() {
				@Override
				public void run() {
					setClosable(true);
					close();
				}
			});
		}
	}

	public PooledProgressIndicatorWindow(final Component fireComponent, final int total) {
		super("Průběh operace");

		this.total = total;

		setWidth("300px");
		setHeight("170px");
		center();

		// okno se nesmí dát zavřít
		setClosable(false);

		VerticalLayout processWindowLayout = new VerticalLayout();
		setContent(processWindowLayout);

		processWindowLayout.setMargin(true);
		processWindowLayout.setSpacing(true);
		processWindowLayout.setSizeFull();

		final Label progressItemLabel = new Label("0.0%");

		progressbar = new PooledProgressBar(total) {

			private static final long serialVersionUID = 6817511244777617827L;

			@Override
			protected void indicateProgress(float progress) {
				progressItemLabel.setValue(myFormatter.format(progress * 100) + "%");
			}

			@Override
			protected void process(final ProgressThread thread) {
				PooledProgressIndicatorWindow.this.process(thread);
				closeOnDone();
			}

		};
		UI.getCurrent().setPollInterval(200);
		progressbar.setIndeterminate(false);
		progressbar.setValue(0f);

		processWindowLayout.addComponent(progressbar);
		processWindowLayout.setComponentAlignment(progressbar, Alignment.MIDDLE_CENTER);

		processWindowLayout.addComponent(progressItemLabel);
		processWindowLayout.setComponentAlignment(progressItemLabel, Alignment.MIDDLE_CENTER);

		// aby to nešlo pustit dvakrát vedle sebe
		if (fireComponent != null)
			fireComponent.setEnabled(false);

		addCloseListener(new Window.CloseListener() {

			private static final long serialVersionUID = -3163982269047432857L;

			@Override
			public void windowClose(CloseEvent e) {

				// opět povol zahajující element
				if (fireComponent != null)
					fireComponent.setEnabled(true);

				onClose();
			}
		});

	}

	public PooledProgressBar getProgressbar() {
		return progressbar;
	}
}
