package cz.gattserver.grass3.monitor.processor.item;

import elemental.json.JsonObject;
import elemental.json.JsonType;

/**
 * TO popisující stav monitorovaného předmětu
 * 
 * @author Hynek
 *
 */
public abstract class MonitorItemTO {

	protected MonitorState monitorState = MonitorState.ERROR;
	protected String stateDetails;
	protected String type;

	public MonitorItemTO() {
		type = this.getClass().getName();
	}

	public MonitorItemTO(JsonObject jsonObject) {
		monitorState = MonitorState.valueOf(jsonObject.getString("monitorState"));
		if (JsonType.NULL == jsonObject.get("stateDetails").getType())
			return;
		stateDetails = jsonObject.getString("stateDetails");
	}

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

	public String getType() {
		return type;
	}

}
