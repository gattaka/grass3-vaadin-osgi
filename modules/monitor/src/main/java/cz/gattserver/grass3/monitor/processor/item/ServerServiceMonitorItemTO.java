package cz.gattserver.grass3.monitor.processor.item;

public class ServerServiceMonitorItemTO extends MonitorItemTO {

	private String name;
	private String address;
	private int responseCode;

	public ServerServiceMonitorItemTO(String name, String address) {
		super();
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
