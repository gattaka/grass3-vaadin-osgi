package cz.gattserver.grass3.monitor.processor.item;

/**
 * TO popisující stav monitorovaného předmětu
 * 
 * @author Hynek
 *
 */
public class MonitorItemTO {

	protected MonitorState monitorState = MonitorState.ERROR;

	protected String stateDetails;

	/**
	 * Získá stav monitorování
	 */
	public MonitorState getMonitorState() {
		return monitorState;
	}

	public void setMonitorState(MonitorState monitorState) {
		this.monitorState = monitorState;
	}

	public String getStateDetails() {
		return stateDetails;
	}

	public void setStateDetails(String stateDetails) {
		this.stateDetails = stateDetails;
	}

}
