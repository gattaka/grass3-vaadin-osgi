package cz.gattserver.grass3.articles.tabs;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.gattserver.grass3.articles.config.ArticlesConfiguration;
import cz.gattserver.grass3.articles.events.ArticlesProcessProgressEvent;
import cz.gattserver.grass3.articles.events.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.events.ArticlesProcessStartEvent;
import cz.gattserver.grass3.articles.facade.ArticleFacade;
import cz.gattserver.grass3.config.ConfigurationService;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.tabs.template.AbstractSettingsTab;
import cz.gattserver.grass3.ui.progress.BaseProgressBar;
import cz.gattserver.grass3.ui.progress.ProgressWindow;
import cz.gattserver.grass3.ui.util.GrassRequest;
import cz.gattserver.web.common.ui.FieldUtils;
import cz.gattserver.web.common.ui.H2Label;
import cz.gattserver.web.common.window.ConfirmWindow;
import net.engio.mbassy.listener.Handler;

public class ArticlesSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	@Autowired
	private ArticleFacade articleFacade;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private EventBus eventBus;

	private UI ui = UI.getCurrent();
	private ProgressWindow progressIndicatorWindow;

	private Button reprocessButton;

	public ArticlesSettingsTab(GrassRequest request) {
		super(request);
	}

	@Override
	protected Component createContent() {

		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(true);
		layout.setSpacing(true);

		VerticalLayout settingsLayout = new VerticalLayout();
		layout.addComponent(settingsLayout);

		final ArticlesConfiguration configuration = loadConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new H2Label("Nastavení"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setMargin(false);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Délka tabulátoru ve znacích
		 */
		final TextField tabLengthField = new TextField("Délka tabulátoru");
		FieldUtils.addValidator(tabLengthField,
				new IntegerRangeValidator("Délka tabulátoru musí být kladné celé číslo", 0, Integer.MAX_VALUE));
		tabLengthField.setValue(String.valueOf(configuration.getTabLength()));
		settingsFieldsLayout.addComponent(tabLengthField);

		/**
		 * Prodleva mezi průběžnými zálohami článku
		 */
		final TextField backupTimeout = new TextField("Prodleva mezi zálohami");
		FieldUtils.addValidator(backupTimeout,
				new IntegerRangeValidator("Prodleva mezi zálohami musí být kladné celé číslo", 0, Integer.MAX_VALUE));
		backupTimeout.setValue(String.valueOf(configuration.getBackupTimeout()));
		settingsFieldsLayout.addComponent(backupTimeout);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {
				// TODO
				if (tabLengthField.getComponentError() == null && backupTimeout.getComponentError() == null) {
					configuration.setTabLength(Integer.parseInt(tabLengthField.getValue()));
					configuration.setBackupTimeout(Integer.parseInt(backupTimeout.getValue()));
					storeConfiguration(configuration);
				}
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		/**
		 * Reprocess tlačítko
		 */
		settingsLayout.addComponent(new H2Label("Přegenerování obsahů"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout reprocessLayout = new VerticalLayout();
		settingsLayout.addComponent(reprocessLayout);
		reprocessLayout.setMargin(false);
		reprocessLayout.setSpacing(true);
		reprocessLayout.setSizeFull();

		reprocessButton = new Button("Přegenerovat všechny články");
		reprocessButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {

				ConfirmWindow confirmSubwindow = new ConfirmWindow(
						"Přegenerování všech článků může zabrat delší čas a dojde během něj zřejmě k mnoha drobným změnám - opravdu přegenerovat ?",
						e -> {
							eventBus.subscribe(ArticlesSettingsTab.this);
							ui.setPollInterval(200);
							articleFacade.reprocessAllArticles(getRequest().getContextRoot());
						});
				confirmSubwindow.setWidth("460px");
				confirmSubwindow.setHeight("230px");
				getUI().addWindow(confirmSubwindow);

			}
		});
		reprocessLayout.addComponent(reprocessButton);

		return layout;

	}

	private ArticlesConfiguration loadConfiguration() {
		ArticlesConfiguration configuration = new ArticlesConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	private void storeConfiguration(ArticlesConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	@Handler
	protected void onProcessStart(final ArticlesProcessStartEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {
				BaseProgressBar progressBar = new BaseProgressBar(event.getCountOfStepsToDo());
				progressBar.setIndeterminate(false);
				progressBar.setValue(0f);
				progressIndicatorWindow = new ProgressWindow(progressBar);
				ui.addWindow(progressIndicatorWindow);
			}
		});
	}

	@Handler
	protected void onProcessProgress(ArticlesProcessProgressEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {
				progressIndicatorWindow.indicateProgress(event.getStepDescription());
			}
		});
	}

	@Handler
	protected void onProcessResult(final ArticlesProcessResultEvent event) {
		ui.access(new Runnable() {
			@Override
			public void run() {
				// ui.setPollInterval(-1);
				if (progressIndicatorWindow != null)
					progressIndicatorWindow.closeOnDone();
				reprocessButton.setEnabled(true);

				if (event.isSuccess()) {
					showInfo("Přegenerování článků proběhlo úspěšně");
				} else {
					showWarning("Přegenerování článků se nezdařilo");
				}
			}
		});
		eventBus.unsubscribe(ArticlesSettingsTab.this);
	}

}
