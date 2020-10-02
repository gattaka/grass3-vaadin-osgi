package cz.gattserver.grass3.monitor.processor.item;

import elemental.json.JsonObject;

public class ServerServicePartItemTO extends ListPartItemTO<ServerServiceMonitorItemTO> {

	public ServerServicePartItemTO() {
	}

	public ServerServicePartItemTO(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected ServerServiceMonitorItemTO createItem(JsonObject jsonObject) {
		return new ServerServiceMonitorItemTO(jsonObject);
	}

}
