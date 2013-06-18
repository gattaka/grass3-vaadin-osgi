package org.myftp.gattserver.grass3.articles.tabs;

import java.util.List;

import javax.annotation.Resource;

import org.myftp.gattserver.grass3.articles.config.ArticlesConfiguration;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.IArticleFacade;
import org.myftp.gattserver.grass3.config.IConfigurationService;
import org.myftp.gattserver.grass3.facades.IContentTagFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.tabs.template.AbstractSettingsTab;
import org.myftp.gattserver.grass3.util.GrassRequest;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@org.springframework.stereotype.Component("articlesSettingsTab")
@Scope("prototype")
public class ArticlesSettingsTab extends AbstractSettingsTab {

	private static final long serialVersionUID = 2474374292329895766L;

	@Resource(name = "articleFacade")
	private IArticleFacade articleFacade;

	@Resource(name = "contentTagFacade")
	private IContentTagFacade contentTagFacade;

	@Resource(name = "configurationService")
	private IConfigurationService configurationService;

	private Window progressSubWindow;
	private ProgressThread progressThread;
	private ProgressIndicator progressbar;
	private Label progressItemLabel;
	private Button reprocessButton;

	public ArticlesSettingsTab(GrassRequest request) {
		super(request);
	}

	/**
	 * Validátor pro validaci kladný celých čísel (celá čísla větší než nula)
	 * 
	 */
	private static class PositiveIntegerValidator extends
			AbstractStringValidator {

		private static final long serialVersionUID = 6306586184856533108L;

		public PositiveIntegerValidator(String errorMessage) {
			super(errorMessage);
		}

		@Override
		protected boolean isValidValue(String value) {
			try {
				int number = Integer.parseInt(value);
				return number > 0;
			} catch (Exception e) {
				return false;
			}
		}

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
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>",
				ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout settingsFieldsLayout = new VerticalLayout();
		settingsLayout.addComponent(settingsFieldsLayout);
		settingsFieldsLayout.setSpacing(true);
		settingsFieldsLayout.setSizeFull();

		/**
		 * Délka tabulátoru ve znacích
		 */
		final TextField tabLengthField = new TextField("Délka tabulátoru");
		tabLengthField.addValidator(new PositiveIntegerValidator(
				"Délka tabulátoru musí být celé číslo"));
		tabLengthField.setValue(String.valueOf(configuration.getTabLength()));
		settingsFieldsLayout.addComponent(tabLengthField);

		/**
		 * Prodleva mezi průběžnými zálohami článku
		 */
		final TextField backupTimeout = new TextField("Prodleva mezi zálohami");
		backupTimeout.addValidator(new PositiveIntegerValidator(
				"Prodleva mezi zálohami musí být celé číslo"));
		backupTimeout
				.setValue(String.valueOf(configuration.getBackupTimeout()));
		settingsFieldsLayout.addComponent(backupTimeout);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {

				if (tabLengthField.isValid() && backupTimeout.isValid())

					configuration.setTabLength(Integer.parseInt(tabLengthField
							.getValue()));
				configuration.setBackupTimeout(Integer.parseInt(backupTimeout
						.getValue()));
				storeConfiguration(configuration);
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		/**
		 * Reprocess tlačítko
		 */
		settingsLayout.addComponent(new Label(
				"</br><h2>Přegenerování obsahů</h2>", ContentMode.HTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout reprocessLayout = new VerticalLayout();
		settingsLayout.addComponent(reprocessLayout);
		reprocessLayout.setSpacing(true);
		reprocessLayout.setSizeFull();

		reprocessButton = new Button("Přegenerovat všechny články");
		reprocessButton.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {

				ConfirmSubwindow confirmSubwindow = new ConfirmSubwindow(
						"Přegenerování všech článků může zabrat delší čas a dojde během něj zřejmě k mnoha drobným změnám - opravdu přegenerovat ?") {

					private static final long serialVersionUID = -1214461419119865670L;

					@Override
					protected void onConfirm(ClickEvent event) {

						progressSubWindow = new ProgressSubWindow();
						getUI().addWindow(progressSubWindow);

					}
				};
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

	public class ProgressSubWindow extends Window {

		private static final long serialVersionUID = 2717898756701926156L;

		public ProgressSubWindow() {
			super("Průběh operace");

			setWidth("300px");
			setHeight("170px");
			center();

			// okno se nesmí dát zavřít
			setReadOnly(true);

			VerticalLayout processWindowLayout = new VerticalLayout();
			setContent(processWindowLayout);

			processWindowLayout.setMargin(true);
			processWindowLayout.setSpacing(true);
			processWindowLayout.setSizeFull();

			progressbar = new ProgressIndicator();
			progressbar.setPollingInterval(100);
			progressbar.setIndeterminate(false);
			progressbar.setEnabled(false);
			processWindowLayout.addComponent(progressbar);
			processWindowLayout.setComponentAlignment(progressbar,
					Alignment.MIDDLE_CENTER);

			progressItemLabel = new Label("");
			processWindowLayout.addComponent(progressItemLabel);
			processWindowLayout.setComponentAlignment(progressItemLabel,
					Alignment.MIDDLE_CENTER);

			// aby to nešlo pustit dvakrát vedle sebe
			reprocessButton.setEnabled(false);

			progressThread = new ProgressThread();
			progressThread.start();
			progressbar.setEnabled(true);
			progressbar.setValue(0f);

		}

	}

	/**
	 * Je volán vláknem aby se aktualizoval stav progressbaru
	 */
	public void prosessed() {
		float progress = progressThread.getProgress();
		String progressItemName = progressThread.getCurrentName();
		progressbar.setValue(progress);
		progressItemLabel.setValue(progressItemName);
		if (progress == 1) {
			progressbar.setEnabled(false);
			reprocessButton.setEnabled(true);
			getUI().removeWindow(progressSubWindow);
		}
	}

	public class ProgressThread extends Thread {

		private int total;
		private int current;
		private String currentName;

		@Override
		public void run() {

			List<ArticleDTO> articles = articleFacade
					.getAllArticlesForReprocess();
			total = articles.size();
			current = 0;

			for (ArticleDTO article : articles) {

				String tags = contentTagFacade.serializeTags(article
						.getContentNode().getContentTags());
				articleFacade.modifyArticle(article.getContentNode().getName(),
						article.getText(), tags, article.getContentNode()
								.isPublicated(), article, getRequest()
								.getContextRoot());

				synchronized (getUI()) {
					current++;
					currentName = "(" + current + "/" + total + ") "
							+ article.getContentNode().getName();
					prosessed(); // aktualizuj stav progressbaru
				}
			}
		}

		/**
		 * @return procentuální stav hotové práce
		 */
		public float getProgress() {
			return (float) current / total;
		}

		public String getCurrentName() {
			return currentName;
		}

	}

}
