package cz.gattserver.grass3.monitor.processor.item;

import elemental.json.JsonObject;

public class ServersPartItemTO extends ListPartItemTO<ServersMonitorItemTO> {

	public ServersPartItemTO() {
	}

	public ServersPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected ServersMonitorItemTO createItem(JsonObject jsonObject) {
		return new ServersMonitorItemTO(jsonObject);
	}

}
