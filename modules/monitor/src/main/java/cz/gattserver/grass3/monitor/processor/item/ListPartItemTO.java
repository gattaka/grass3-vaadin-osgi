package cz.gattserver.grass3.monitor.processor.item;

import java.util.ArrayList;
import java.util.List;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

public abstract class ListPartItemTO<T extends MonitorItemTO> extends MonitorItemTO {

	private List<T> items;

	protected abstract T createItem(JsonObject jsonObject);

	public ListPartItemTO() {
		items = new ArrayList<T>();
	}

	public ListPartItemTO(JsonObject jsonObject) {
		this();
		if (jsonObject == null)
			return;
		JsonArray array = jsonObject.getArray("items");
		for (int i = 0; i < array.length(); i++)
			items.add(createItem(array.getObject(i)));
	}

	public List<T> getItems() {
		return items;
	}

}
