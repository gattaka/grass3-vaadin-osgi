package org.myftp.gattserver.grass3.articles.windows;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.myftp.gattserver.grass3.articles.config.ArticlesConfiguration;
import org.myftp.gattserver.grass3.articles.dto.ArticleDTO;
import org.myftp.gattserver.grass3.articles.facade.ArticleFacade;
import org.myftp.gattserver.grass3.config.ConfigurationFileError;
import org.myftp.gattserver.grass3.config.ConfigurationManager;
import org.myftp.gattserver.grass3.config.ConfigurationUtils;
import org.myftp.gattserver.grass3.facades.ContentTagFacade;
import org.myftp.gattserver.grass3.subwindows.ConfirmSubwindow;
import org.myftp.gattserver.grass3.subwindows.GrassSubWindow;
import org.myftp.gattserver.grass3.windows.template.SettingsWindow;

import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ArticlesSettingsWindow extends SettingsWindow {

	private static final long serialVersionUID = -2646594946025400908L;

	private ArticleFacade articleFacade = ArticleFacade.INSTANCE;
	private ContentTagFacade contentTagFacade = ContentTagFacade.INSTANCE;

	private VerticalLayout settingsLayout = new VerticalLayout();

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
		protected boolean isValidString(String value) {
			try {
				int number = Integer.parseInt(value);
				return number > 0;
			} catch (Exception e) {
				return false;
			}
		}

	}

	public ArticlesSettingsWindow() {
		setName("article-settings");
	}

	@Override
	protected void createRightColumnContent(VerticalLayout layout) {

		layout.setMargin(true);
		layout.setSpacing(true);
		layout.addComponent(settingsLayout);

	}

	private ArticlesConfiguration loadConfiguration() {
		try {
			return new ConfigurationUtils<ArticlesConfiguration>(
					new ArticlesConfiguration(),
					ArticlesConfiguration.CONFIG_PATH)
					.loadExistingOrCreateNewConfiguration();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
			return null;
		}
	}

	private void storeConfiguration(ArticlesConfiguration configuration) {
		try {
			ConfigurationManager.getInstance().storeConfiguration(
					ArticlesConfiguration.CONFIG_PATH, configuration);
		} catch (ConfigurationFileError e) {
			e.printStackTrace();
			showError500();
		} catch (JAXBException e) {
			e.printStackTrace();
			showError500();
		}
	}

	@Override
	protected void onShow() {

		final ArticlesConfiguration configuration = loadConfiguration();

		settingsLayout.removeAllComponents();
		settingsLayout.addComponent(new Label("<h2>Nastavení</h2>",
				Label.CONTENT_XHTML));

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
		tabLengthField.setValue(configuration.getTabLength());
		settingsFieldsLayout.addComponent(tabLengthField);

		/**
		 * Prodleva mezi průběžnými zálohami článku
		 */
		final TextField backupTimeout = new TextField("Prodleva mezi zálohami");
		backupTimeout.addValidator(new PositiveIntegerValidator(
				"Prodleva mezi zálohami musí být celé číslo"));
		backupTimeout.setValue(configuration.getBackupTimeout());
		settingsFieldsLayout.addComponent(backupTimeout);

		/**
		 * Save tlačítko
		 */
		Button saveButton = new Button("Uložit", new Button.ClickListener() {

			private static final long serialVersionUID = 8490964871266821307L;

			public void buttonClick(ClickEvent event) {

				if (tabLengthField.isValid() && backupTimeout.isValid())

					configuration.setTabLength((Integer) tabLengthField
							.getValue());
				configuration.setBackupTimeout((Integer) backupTimeout
						.getValue());
				storeConfiguration(configuration);
			}
		});
		settingsFieldsLayout.addComponent(saveButton);

		/**
		 * Reprocess tlačítko
		 */
		settingsLayout.addComponent(new Label("<h2>Přegenerování obsahů</h2>",
				Label.CONTENT_XHTML));

		// Nadpis zůstane odsazen a jednotlivá pole se můžou mezi sebou rozsázet
		VerticalLayout reprocessLayout = new VerticalLayout();
		settingsLayout.addComponent(reprocessLayout);
		reprocessLayout.setSpacing(true);
		reprocessLayout.setSizeFull();

		Button reprocessButton = new Button("Přegenerovat všechny články",
				new Button.ClickListener() {

					private static final long serialVersionUID = 8490964871266821307L;

					public void buttonClick(ClickEvent event) {

						addWindow(new ConfirmSubwindow(
								"Přegenerování všech článků může zabrat delší čas a dojde během něj zřejmě k mnoha drobným změnám - opravdu přegenerovat ?") {

							private static final long serialVersionUID = -1214461419119865670L;

							@Override
							protected void onConfirm(ClickEvent event) {

								// GrassSubWindow processSubWindow = new
								// GrassSubWindow(
								// "Průbeh operace");
								// addWindow(processSubWindow);

								// tohle se musí hodit do vlákna dle
								// http://demo.vaadin.com/sampler#ProgressIndicators

								List<ArticleDTO> articles = articleFacade
										.getAllArticles();

								for (ArticleDTO article : articles) {

									String tags = contentTagFacade
											.serializeTags(article
													.getContentNode()
													.getContentTags());
									articleFacade.modifyArticle(article
											.getContentNode().getName(),
											article.getText(), tags, article
													.getContentNode()
													.getPublicated(), article);

								}

							}
						});

					}
				});
		reprocessLayout.addComponent(reprocessButton);

		super.onShow();
	}
}
