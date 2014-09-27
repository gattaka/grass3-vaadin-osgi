package cz.gattserver.grass3.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.search.service.ISearchConnector;

@Component("connectorAggregator")
public class ConnectorAggregatorImpl implements IConnectorAggregator {

	@Autowired(required = false)
	private List<ISearchConnector> searchConnectors;

	private Map<String, ISearchConnector> searchConnectorsById = new HashMap<String, ISearchConnector>();

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		if (searchConnectors == null)
			searchConnectors = new ArrayList<ISearchConnector>();

		for (ISearchConnector connector : searchConnectors) {
			searchConnectorsById.put(connector.getModuleId(), connector);
		}
	}

	public List<ISearchConnector> getSearchConnectors() {
		return searchConnectors;
	}

	public Map<String, ISearchConnector> getSearchConnectorsById() {
		return searchConnectorsById;
	}

}
