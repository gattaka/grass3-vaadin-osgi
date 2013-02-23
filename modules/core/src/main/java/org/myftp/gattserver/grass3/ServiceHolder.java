package org.myftp.gattserver.grass3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.service.IEntityServiceListener;
import org.myftp.gattserver.grass3.service.IContentService;
import org.myftp.gattserver.grass3.service.ISectionService;
import org.myftp.gattserver.grass3.service.ISettingsService;

/**
 * {@link ServiceHolder} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public class ServiceHolder {

	/**
	 * Musí být singleton, aby bylo možné jednoduše volat jeho instanci
	 * odkudkoliv z programu, zatímco vytvoření instance bude mít na starost
	 * Blueprint
	 */
	private static ServiceHolder instance;

	private ServiceHolder() {
	};

	/**
	 * TODO Tady mám trochu pochybnosti, jestli by to nešlo napsat lépe.
	 * Aktuální implementace spoléhá na to, že tuto metodu zavolá jako první
	 * Blueprint kontejner. V případě, že by to dělalo problémy mě napadá udělat
	 * místo "factory" metody factory třídu (která bude jinak ven neviditelná) a
	 * nějak to přes to vyřešit.
	 */
	public synchronized static ServiceHolder getInstance() {
		if (instance == null)
			instance = new ServiceHolder();
		return instance;
	}

	/**
	 * Instance aplikace - reprezentující jednotlivé sessions - potřebují být
	 * notifikovány o přidání sekce, settings apod. aby si přidali instanci okna
	 * atd.
	 */
	private Set<GrassUI> applications = new HashSet<GrassUI>();

	/**
	 * Přidání instance aplikace, která tak bude notifikována aby si přidala
	 * okna
	 */
	public void registerListenerApp(GrassUI application) {
		applications.add(application);

		// dej jí vědět o již zaregistrovaných sekcích
		for (ISectionService service : sectionServices)
			application.addWindow(service.getSectionWindowNewInstance());

		// dej jí vědět o již zaregistrovaných nastaveních
		for (ISettingsService service : settingsServices)
			application.addWindow(service.getSettingsWindowNewInstance());

		// dej jí vědět o již zaregistrovaných službách obsahů
		for (IContentService service : contentServices) {
			application.addWindow(service.getContentEditorWindowNewInstance());
			application.addWindow(service.getContentViewerWindowNewInstance());
		}
	}

	/**
	 * DB entity listener service
	 */
	private IEntityServiceListener entityServiceListener;

	public IEntityServiceListener getEntityServiceListener() {
		return entityServiceListener;
	}

	public void setEntityServiceListener(IEntityServiceListener entityListener) {
		this.entityServiceListener = entityListener;

		// TODO .. tohle není úplně košér, ale jinak se mi to nedaří provázat
		AbstractDAO.serviceListener = entityListener;
		System.out.println("EntityListener version: "
				+ entityListener.getVersion());
	}

	/**
	 * Obsahy
	 */
	private List<IContentService> contentServices = Collections
			.synchronizedList(new ArrayList<IContentService>());

	public synchronized List<IContentService> getContentServices() {
		return contentServices;
	}

	public synchronized void setContentServices(
			List<IContentService> contentServices) {
		this.contentServices = contentServices;
	}

	public synchronized IContentService getContentServiceByName(
			String contentReaderID) {
		for (IContentService contentService : contentServices) {
			if (contentService.getContentID().equals(contentReaderID))
				return contentService;
		}
		return null;
	}

	public synchronized void bindContentService(IContentService contentService) {
		for (GrassUI application : applications) {
			application.addWindow(contentService.getContentEditorWindowNewInstance());
			application.addWindow(contentService.getContentViewerWindowNewInstance());
		}
	}

	public synchronized void unbindContentService(IContentService contentService) {
		for (GrassUI application : applications) {
			application.removeWindow(contentService.getContentEditorWindowNewInstance());
			application.removeWindow(contentService.getContentViewerWindowNewInstance());
		}
	}

	/**
	 * Sekce
	 */
	private List<ISectionService> sectionServices = Collections
			.synchronizedList(new ArrayList<ISectionService>());

	public synchronized List<ISectionService> getSectionServices() {
		return sectionServices;
	}

	public synchronized void setSectionServices(
			List<ISectionService> sectionServices) {
		this.sectionServices = sectionServices;
	}

	public synchronized void bindSection(ISectionService section) {
		for (GrassUI application : applications) {
			application.addWindow(section.getSectionWindowNewInstance());
		}
	}

	public synchronized void unbindSection(ISectionService section) {
		for (GrassUI application : applications) {
			application.removeWindow(section.getSectionWindowClass());
		}
	}

	/**
	 * Settings
	 */
	private List<ISettingsService> settingsServices = Collections
			.synchronizedList(new ArrayList<ISettingsService>());

	public synchronized List<ISettingsService> getSettingsServices() {
		return settingsServices;
	}

	public synchronized void setSettingsServices(
			List<ISettingsService> settingsServices) {
		this.settingsServices = settingsServices;
	}

	public synchronized void bindSettings(ISettingsService settingsService) {
		for (GrassUI application : applications) {
			application.addWindow(settingsService.getSettingsWindowNewInstance());
		}
	}

	public synchronized void unbindSettings(ISettingsService settingsService) {
		for (GrassUI application : applications) {
			application.removeWindow(settingsService.getSettingsWindowClass());
		}
	}

}
