package cz.gattserver.grass3.articles.ui.pages.settings;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;

import cz.gattserver.grass3.articles.config.ArticlesConfiguration;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessProgressEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass3.articles.events.impl.ArticlesProcessStartEvent;
import cz.gattserver.grass3.articles.services.ArticleService;
import cz.gattserver.grass3.events.EventBus;
import cz.gattserver.grass3.services.ConfigurationService;
import cz.gattserver.grass3.ui.components.button.ImageButton;
import cz.gattserver.grass3.ui.components.button.SaveButton;
import cz.gattserver.grass3.ui.dialogs.ProgressDialog;
import cz.gattserver.grass3.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass3.ui.pages.template.GrassPage;
import cz.gattserver.grass3.ui.util.DoubleToIntegerConverter;
import cz.gattserver.grass3.ui.util.UIUtils;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.window.ConfirmDialog;
import net.engio.mbassy.listener.Handler;

public class ArticlesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Autowired
	private ArticleService articleFacade;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private EventBus eventBus;

	private ProgressDialog progressIndicatorDialog;
	private UUID uuid;

	private Button reprocessButton;

	@Override
	public void createFragment(Div layout) {
		final ArticlesConfiguration configuration = loadConfiguration();

		layout.add(new H2("Nastavení článků"));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		FormLayout settingsFieldsLayout = new FormLayout();
		layout.add(settingsFieldsLayout);

		Binder<ArticlesConfiguration> binder = new Binder<>();
		binder.setBean(new ArticlesConfiguration());
		binder.readBean(configuration);

		/**
		 * Délka tabulátoru ve znacích
		 */
		final NumberField tabLengthField = new NumberField("Délka tabulátoru");
		tabLengthField.setStep(1);
		binder.forField(tabLengthField).withConverter(new DoubleToIntegerConverter())
				.bind(ArticlesConfiguration::getTabLength, ArticlesConfiguration::setTabLength);
		tabLengthField.setValue((double) configuration.getTabLength());
		settingsFieldsLayout.add(tabLengthField);

		/**
		 * Prodleva mezi průběžnými zálohami článku
		 */
		final NumberField backupTimeoutField = new NumberField("Prodleva mezi zálohami");
		backupTimeoutField.setStep(1);
		binder.forField(backupTimeoutField).withConverter(new DoubleToIntegerConverter())
				.bind(ArticlesConfiguration::getBackupTimeout, ArticlesConfiguration::setBackupTimeout);
		backupTimeoutField.setValue((double) configuration.getTabLength());
		settingsFieldsLayout.add(backupTimeoutField);

		/**
		 * Save tlačítko
		 */
		SaveButton saveButton = new SaveButton(event -> {
			if (binder.writeBeanIfValid(configuration)) {
				storeConfiguration(configuration);
			}
		});
		settingsFieldsLayout.add(saveButton);

		/**
		 * Reprocess tlačítko
		 */
		layout.add(new H2("Přegenerování obsahů"));

		reprocessButton = new ImageButton("Přegenerovat všechny články", ImageIcon.GEAR2_16_ICON, event -> {
			ConfirmDialog dialog = new ConfirmDialog(
					"Přegenerování všech článků může zabrat delší čas a dojde během něj zřejmě k mnoha drobným změnám - opravdu přegenerovat ?",
					e -> {
						eventBus.subscribe(ArticlesSettingsPageFragmentFactory.this);
						progressIndicatorDialog = new ProgressDialog();
						uuid = UUID.randomUUID();
						articleFacade.reprocessAllArticles(uuid, GrassPage.getContextPath());
					});
			dialog.setWidth("460px");
			dialog.setHeight("230px");
			dialog.open();
		});
		layout.add(reprocessButton);
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
		progressIndicatorDialog.runInUI(() -> {
			progressIndicatorDialog.setTotal(event.getCountOfStepsToDo());
			progressIndicatorDialog.open();
			;
		});
	}

	@Handler
	protected void onProcessProgress(ArticlesProcessProgressEvent event) {
		progressIndicatorDialog.runInUI(() -> progressIndicatorDialog.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final ArticlesProcessResultEvent event) {
		progressIndicatorDialog.runInUI(() -> {
			if (progressIndicatorDialog != null)
				progressIndicatorDialog.close();
			reprocessButton.setEnabled(true);

			if (event.isSuccess()) {
				UIUtils.showInfo("Přegenerování článků proběhlo úspěšně");
			} else {
				UIUtils.showWarning("Přegenerování článků se nezdařilo");
			}
		});
		eventBus.unsubscribe(ArticlesSettingsPageFragmentFactory.this);
	}

}
