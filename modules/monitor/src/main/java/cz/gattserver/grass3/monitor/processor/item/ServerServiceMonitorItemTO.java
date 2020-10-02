package cz.gattserver.grass3.monitor.processor.item;

import elemental.json.JsonObject;

public class ServerServiceMonitorItemTO extends MonitorItemTO {

	private String name;
	private String address;
	private int responseCode;

	public ServerServiceMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		name = jsonObject.getString("name");
		address = jsonObject.getString("address");
		responseCode = (int) jsonObject.getNumber("responseCode");
	}

	public ServerServiceMonitorItemTO(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}
