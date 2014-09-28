package cz.gattserver.grass3.ui.progress;

import java.text.DecimalFormat;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProgressWindow extends Window {

	private static final long serialVersionUID = 2779568469991016255L;
	private static DecimalFormat myFormatter = new DecimalFormat("##0.0");

	private BaseProgressBar progressbar;
	private Label progressItemLabel;
	private Label descriptionLabel;

	/**
	 * Volá se při dokončení operace a zavření progressokna
	 */
	protected void onClose() {
	}

	public int getTotal() {
		return progressbar.getTotal();
	}

	public void closeOnDone() {
		getUI().access(new Runnable() {
			@Override
			public void run() {
				setClosable(true);
				close();
			}
		});
	}

	public void indicateProgress(String msg) {
		progressbar.increaseProgress();
		progressItemLabel.setValue(myFormatter.format(progressbar.getProgress() * 100) + "%");
		descriptionLabel.setValue(msg);
	}

	public ProgressWindow(BaseProgressBar progressBar) {
		this(null, progressBar);
	}

	public ProgressWindow(final Component fireComponent, BaseProgressBar progressBar) {
		super("Průběh operace");

		setWidth("300px");
		setHeight("170px");
		center();

		setResizable(false);

		VerticalLayout processWindowLayout = new VerticalLayout();
		setContent(processWindowLayout);

		processWindowLayout.setMargin(true);
		processWindowLayout.setSpacing(true);
		processWindowLayout.setSizeFull();

		progressItemLabel = new Label("0.0%");
		progressItemLabel.setWidth(null);
		progressbar = progressBar;
		descriptionLabel = new Label();
		descriptionLabel.setWidth(null);

		progressbar.setIndeterminate(false);
		progressbar.setValue(0f);

		processWindowLayout.addComponent(progressItemLabel);
		processWindowLayout.setComponentAlignment(progressItemLabel, Alignment.MIDDLE_CENTER);

		processWindowLayout.addComponent(progressbar);
		processWindowLayout.setComponentAlignment(progressbar, Alignment.MIDDLE_CENTER);

		processWindowLayout.addComponent(descriptionLabel);
		processWindowLayout.setComponentAlignment(descriptionLabel, Alignment.MIDDLE_CENTER);

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

}
