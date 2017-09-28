package cz.gattserver.grass3.search;

import java.util.List;
import java.util.Map;

import cz.gattserver.grass3.search.service.SearchConnector;

public interface ConnectorAggregator {

	public List<SearchConnector> getSearchConnectors();

	public Map<String, SearchConnector> getSearchConnectorsById();

}
