package org.myftp.gattserver.grass3.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.myftp.gattserver.grass3.search.service.ISearchConnector;

public class ConnectorAggregator {

	/**
	 * Musí být singleton, aby bylo možné jednoduše volat jeho instanci
	 * odkudkoliv z programu, zatímco vytvoření instance bude mít na starost
	 * Blueprint
	 */
	private static ConnectorAggregator instance;

	private ConnectorAggregator() {
	};

	public synchronized static ConnectorAggregator getInstance() {
		if (instance == null)
			instance = new ConnectorAggregator();
		return instance;
	}
	
	private Map<String, ISearchConnector> searchConnectorsById = new HashMap<String, ISearchConnector>();
	
	private List<ISearchConnector> searchConnectors = Collections
			.synchronizedList(new ArrayList<ISearchConnector>());

	public synchronized List<ISearchConnector> getSearchConnectors() {
		return searchConnectors;
	}

	public synchronized void setSearchConnectors(
			List<ISearchConnector> searchConnectors) {
		this.searchConnectors = searchConnectors;
	}
	
	public synchronized void bindConnector(ISearchConnector searchConnector) {
		searchConnectorsById.put(searchConnector.getModuleId(), searchConnector);
	}

	public synchronized void unbindConnector(ISearchConnector searchConnector) {
		searchConnectorsById.remove(searchConnector.getModuleId());
	}

	public Map<String, ISearchConnector> getSearchConnectorsById() {
		return searchConnectorsById;
	}
	
}
