package org.myftp.gattserver.grass3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityListener {

	/**
	 * Musí být singleton, aby bylo možné jednoduše volat jeho instanci
	 * odkudkoliv z programu, zatímco vytvoření instance bude mít na starost
	 * Blueprint
	 */
	private static EntityListener instance;

	private EntityListener() {
	};

	/**
	 * TODO Tady mám trochu pochybnosti, jestli by to nešlo napsat lépe.
	 * Aktuální implementace spoléhá na to, že tuto metodu zavolá jako první
	 * Blueprint kontejner. V případě, že by to dělalo problémy mě napadá udělat
	 * místo "factory" metody factory třídu (která bude jinak ven neviditelná) a
	 * nějak to přes to vyřešit.
	 * 
	 * 23.9.2012 - přesně se stalo, co jsem čekal - statický singleton se
	 * pochopitelně aplikuje pouze na daný classloader, kterých je teď ale víc -
	 * proto se vytvoří dva a v jednom informace, které se měly předat logicky
	 * chybí.
	 * 
	 * Řešení je další vrstva - nějaký ListenerAgregator, který bude jako
	 * singleton v rámci daného classloaderu, ale bude vytvořen Blueprintem,
	 * takže do něj bude možno nainjectovat existující trans-bundle instance
	 * jako je například tento EntityListener
	 */
	public synchronized static EntityListener getInstance() {
		if (instance == null)
			instance = new EntityListener();
		return instance;
	}

	/**
	 * Verze "sestavení" entit. Došlo k nějaké změně ? Má se přegenerovat
	 * SessionFactory ?
	 */
	private Long version = 1L;

	/**
	 * Entity
	 */
	private List<DBUnitService> services = Collections
			.synchronizedList(new ArrayList<DBUnitService>());

	public synchronized List<DBUnitService> getServices() {
		return services;
	}

	public synchronized void setServices(List<DBUnitService> sectionServices) {
		this.services = sectionServices;
	}

	public synchronized void bind(DBUnitService section) {
		System.out.println("Registred DBUnitService");
		synchronized (version) {
			version++;
		}
	}

	public synchronized void unbind(DBUnitService section) {
		System.out.println("UnRegistred DBUnitService");
		synchronized (version) {
			version++;
		}
	}

	public synchronized Long getVersion() {
		return version;
	}

}
