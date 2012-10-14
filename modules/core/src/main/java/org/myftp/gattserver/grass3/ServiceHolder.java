package org.myftp.gattserver.grass3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.myftp.gattserver.grass3.model.AbstractDAO;
import org.myftp.gattserver.grass3.model.service.IEntityServiceListener;
import org.myftp.gattserver.grass3.service.IContentServiceListener;
import org.myftp.gattserver.grass3.service.ISectionService;

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
	 * ContentService listener service
	 */
	private IContentServiceListener contentServiceListener;

	public IContentServiceListener getContentServiceListener() {
		return contentServiceListener;
	}

	public void setContentServiceListener(
			IContentServiceListener contentServiceListener) {
		this.contentServiceListener = contentServiceListener;
	}

	/**
	 * Bind listenery - pro každou třídu/ifce služeb je list listenerů. TODO ...
	 * memory leak ??? Když budou instance aplikací mizet, zmizí i jejich
	 * BindListener ?
	 */
	private Map<Class<?>, List<IListenerBinding<?>>> listenerMap = new HashMap<Class<?>, List<IListenerBinding<?>>>();

	/**
	 * <p>
	 * Tato generická metoda typově chrání {@link ServiceHolder} před vložením
	 * třídy jiného typu než je parametr vkládaného {@link IListenerBinding}
	 * listeneru - nemůže se tak stát, že bych vložil {@link Class}&lt;A&gt; a
	 * přitom {@link IListenerBinding}&lt;B&gt; - tím pádem pak můžu bezpečně
	 * přetypovat v notify metodách
	 * </p>
	 * <p>
	 * Můžu tedy vložit toto:
	 * </p>
	 * 
	 * <p>
	 * <code>
	 * registerBindListener(A.class, new BindListener&lt;A&gt;() { ... });
	 * </code>
	 * </p>
	 * 
	 * <p>
	 * ale ne toto (compile error):
	 * </p>
	 * 
	 * <p>
	 * <code>
	 * registerBindListener(A.class, new BindListener&lt;B&gt;() { ... });
	 * </code>
	 * </p>
	 * 
	 * @param clazz
	 * @param listener
	 */
	public synchronized <T> void registerBindListener(Class<T> clazz,
			IListenerBinding<T> listener) {
		List<IListenerBinding<?>> listeners = listenerMap.get(clazz);
		if (listeners == null) {
			listeners = new ArrayList<IListenerBinding<?>>();
			listenerMap.put(clazz, listeners);
		}
		listeners.add(listener);

		// TODO jinak
		if (clazz.equals(ISectionService.class)) {
			// dej novému odběrateli zpětně vědět o již existujících services
			for (ISectionService service : sectionServices) {
				((IListenerBinding<ISectionService>) listener).onBind(service);
			}
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

	@SuppressWarnings("unchecked")
	private void notifyBindSectionListeners(ISectionService section) {
		if (listenerMap.get(ISectionService.class) == null)
			return;
		for (IListenerBinding<?> bindListener : listenerMap
				.get(ISectionService.class)) {
			((IListenerBinding<ISectionService>) bindListener).onBind(section);
		}
	}

	@SuppressWarnings("unchecked")
	private void notifyUnbindSectionListeners(ISectionService section) {
		for (IListenerBinding<?> bindListener : listenerMap
				.get(ISectionService.class)) {
			((IListenerBinding<ISectionService>) bindListener)
					.onUnbind(section);
		}
	}

	public synchronized void bindSection(ISectionService section) {
		notifyBindSectionListeners(section);
	}

	public synchronized void unbindSection(ISectionService section) {
		notifyUnbindSectionListeners(section);
	}

}
