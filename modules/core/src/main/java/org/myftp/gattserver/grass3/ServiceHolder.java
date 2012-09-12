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
	 * Bind listenery - pro každou třídu/ifce služeb je list listenerů. TODO ...
	 * memory leak ??? Když budou instance aplikací mizet, zmizí i jejich
	 * BindListener ?
	 */
	private Map<Class<?>, List<BindListener<?>>> listenerMap = new HashMap<Class<?>, List<BindListener<?>>>();

	public void registerBindListener(Class<?> clazz, BindListener<?> listener) {
		List<BindListener<?>> listeners = listenerMap.get(clazz);
		if (listeners == null) {
			listeners = new ArrayList<BindListener<?>>();
			listenerMap.put(clazz, listeners);
		}
		listeners.add(listener);
	}

	/**
	 * Sekce
	 */
	private List<ISection> sectionServices = Collections
			.synchronizedList(new ArrayList<ISection>());

	public List<ISection> getSectionServices() {
		return sectionServices;
	}

	public void setSectionServices(List<ISection> sectionServices) {
		this.sectionServices = sectionServices;
	}

	/**
	 * TODO - generics !!!
	 * 
	 * @param section
	 */
	public void bindSection(ISection section) {
		System.out.println("bind");
		for (BindListener bindListener : listenerMap.get(ISection.class)) {
			bindListener.onBind(section);
		}
	}

	/**
	 * TODO - generics !!!
	 * 
	 * @param section
	 */
	public void unbindSection(ISection section) {
		System.out.println("unbind");
		for (BindListener bindListener : listenerMap.get(ISection.class)) {
			bindListener.onUnbind(section);
		}
	}

}
