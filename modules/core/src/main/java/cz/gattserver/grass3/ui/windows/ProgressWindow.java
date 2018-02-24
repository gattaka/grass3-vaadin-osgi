package cz.gattserver.grass3.ui.windows;

import java.text.DecimalFormat;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.ui.components.BaseProgressBar;

public class ProgressWindow extends Window {

	private static final long serialVersionUID = 2779568469991016255L;
	private static DecimalFormat myFormatter = new DecimalFormat("##0.0");

	private BaseProgressBar progressBar;
	private Label progressItemLabel;
	private Label descriptionLabel;
	private UI ui;

	public static void runInUI(Runnable r, UI ui) {
		if (ui.getSession() == null) {
			r.run();
		} else {
			ui.access(r);
		}
	}

	@Override
	public void close() {
		runInUI(() -> {
			ui.setPollInterval(-1);
			ProgressWindow.super.close();
		});
	}

	public void runInUI(Runnable r) {
		ProgressWindow.runInUI(r, ui);
	}

	public void indicateProgress(String msg) {
		progressBar.increaseProgress();
		progressItemLabel.setValue(myFormatter.format(progressBar.getProgress() * 100) + "%");
		descriptionLabel.setValue(msg);
	}

	public ProgressWindow setTotal(int total) {
		progressBar.setTotal(total);
		return this;
	}

	public int getTotal() {
		return progressBar.getTotal();
	}

	public ProgressWindow() {
		super("Průběh operace");

		this.ui = UI.getCurrent();

		ui.setPollInterval(200);

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

		progressBar = new BaseProgressBar();
		progressBar.setIndeterminate(false);
		progressBar.setValue(0f);

		descriptionLabel = new Label();
		descriptionLabel.setWidth(null);

		processWindowLayout.addComponent(progressItemLabel);
		processWindowLayout.setComponentAlignment(progressItemLabel, Alignment.MIDDLE_CENTER);

		processWindowLayout.addComponent(progressBar);
		processWindowLayout.setComponentAlignment(progressBar, Alignment.MIDDLE_CENTER);

		processWindowLayout.addComponent(descriptionLabel);
		processWindowLayout.setComponentAlignment(descriptionLabel, Alignment.MIDDLE_CENTER);
	}

}
