package org.myftp.gattserver.grass3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Bind listenery - pro každou třídu/ifce služeb je list listenerů. TODO ...
	 * memory leak ??? Když budou instance aplikací mizet, zmizí i jejich
	 * BindListener ?
	 */
	private Map<Class<?>, List<BindListener<?>>> listenerMap = new HashMap<Class<?>, List<BindListener<?>>>();

	/**
	 * <p>
	 * Tato generická metoda typově chrání {@link ServiceHolder} před vložením
	 * třídy jiného typu než je parametr vkládaného {@link BindListener}
	 * listeneru - nemůže se tak stát, že bych vložil {@link Class}&lt;A&gt; a
	 * přitom {@link BindListener}&lt;B&gt; - tím pádem pak můžu bezpečně
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
			BindListener<T> listener) {
		List<BindListener<?>> listeners = listenerMap.get(clazz);
		if (listeners == null) {
			listeners = new ArrayList<BindListener<?>>();
			listenerMap.put(clazz, listeners);
		}
		listeners.add(listener);

		// TODO jinak
		if (clazz.equals(ISection.class)) {
			// dej novému odběrateli zpětně vědět o již existujících services
			for (ISection service : sectionServices) {
				((BindListener<ISection>) listener).onBind(service);
			}
		}
	}

	/**
	 * Sekce
	 */
	private List<ISection> sectionServices = Collections
			.synchronizedList(new ArrayList<ISection>());

	public synchronized List<ISection> getSectionServices() {
		return sectionServices;
	}

	public synchronized void setSectionServices(List<ISection> sectionServices) {
		this.sectionServices = sectionServices;
	}

	@SuppressWarnings("unchecked")
	private void notifyBindSectionListeners(ISection section) {
		if (listenerMap.get(ISection.class) == null)
			return;
		for (BindListener<?> bindListener : listenerMap.get(ISection.class)) {
			((BindListener<ISection>) bindListener).onBind(section);
		}
	}

	@SuppressWarnings("unchecked")
	private void notifyUnbindSectionListeners(ISection section) {
		for (BindListener<?> bindListener : listenerMap.get(ISection.class)) {
			((BindListener<ISection>) bindListener).onUnbind(section);
		}
	}

	public synchronized void bindSection(ISection section) {
		notifyBindSectionListeners(section);
	}

	public synchronized void unbindSection(ISection section) {
		notifyUnbindSectionListeners(section);
	}

}
