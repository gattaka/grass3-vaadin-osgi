package cz.gattserver.grass3.search;

import java.util.List;
import java.util.Map;

import cz.gattserver.grass3.search.service.ISearchConnector;

public interface IConnectorAggregator {

	public List<ISearchConnector> getSearchConnectors();

	public Map<String, ISearchConnector> getSearchConnectorsById();

}
