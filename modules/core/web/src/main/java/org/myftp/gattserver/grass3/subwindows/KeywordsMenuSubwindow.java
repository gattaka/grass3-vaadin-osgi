package org.myftp.gattserver.grass3.subwindows;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;

@Deprecated
public abstract class KeywordsMenuSubwindow<T> extends GrassSubWindow {

	private static final long serialVersionUID = 4548327306216857107L;

	public KeywordsMenuSubwindow(String caption, Collection<T> data,
			String captionPropertyId) {
		super(caption);

		VerticalLayout subWindowLayout = (VerticalLayout) getContent();
		subWindowLayout.setSpacing(true);
		subWindowLayout.setMargin(true);

		final ListSelect list = new ListSelect();
		subWindowLayout.addComponent(list);

		list.setWidth("100%");
		list.setRows(10);
		list.setNullSelectionAllowed(true);
		list.setMultiSelect(true);
		list.setImmediate(true);

		list.setItemCaptionPropertyId(captionPropertyId);

		for (T item : data) {
			list.addItem(item);
		}

		list.addListener(new Listener() {

			private static final long serialVersionUID = 3965168241339214082L;

			@Override
			public void componentEvent(Event event) {
				if (event instanceof ItemClickEvent) {
					ItemClickEvent itemClickEvent = (ItemClickEvent) event;
					if (itemClickEvent.isDoubleClick())
						onDoubleClick(itemClickEvent.getItem());
				}
			}
		});

	}

	protected abstract void onDoubleClick(Item item);

}
