package cz.gattserver.grass3.monitor.processor.item;

/**
 * TO popisující stav monitorovaného předmětu
 * 
 * @author Hynek
 *
 */
public class MonitorItemTO {

	protected MonitorState monitorState;

	/**
	 * Získá stav monitorování
	 */
	public MonitorState getMonitorState() {
		return monitorState;
	}

	public void setMonitorState(MonitorState monitorState) {
		this.monitorState = monitorState;
	}

}
