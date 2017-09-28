package cz.gattserver.grass3.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.search.service.SearchConnector;

@Component("connectorAggregator")
public class ConnectorAggregatorImpl implements ConnectorAggregator {

	@Autowired(required = false)
	private List<SearchConnector> searchConnectors;

	private Map<String, SearchConnector> searchConnectorsById = new HashMap<String, SearchConnector>();

	@PostConstruct
	private void init() {
		if (searchConnectors == null)
			searchConnectors = new ArrayList<SearchConnector>();

		for (SearchConnector connector : searchConnectors) {
			searchConnectorsById.put(connector.getModuleId(), connector);
		}
	}

	public List<SearchConnector> getSearchConnectors() {
		return searchConnectors;
	}

	public Map<String, SearchConnector> getSearchConnectorsById() {
		return searchConnectorsById;
	}

}
