package cz.gattserver.grass3.ui.windows;

import java.text.DecimalFormat;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.gattserver.grass3.ui.components.BaseProgressBar;

public class ProgressWindow extends Window {

	private static final long serialVersionUID = 2779568469991016255L;
	private static DecimalFormat myFormatter = new DecimalFormat("##0.0");

	private BaseProgressBar progressbar;
	private Label progressItemLabel;
	private Label descriptionLabel;

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
	}

}
