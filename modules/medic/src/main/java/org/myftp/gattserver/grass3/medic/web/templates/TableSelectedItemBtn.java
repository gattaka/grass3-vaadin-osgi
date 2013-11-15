package org.myftp.gattserver.grass3.medic.web.templates;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public abstract class TableSelectedItemBtn<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public TableSelectedItemBtn(String caption, final Table table,
			Component... triggerComponents) {
		setCaption(caption);
		setEnabled(false);
		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				TableSelectedItemBtn.this.setEnabled(enabled);
			}
		});
		addClickListener(getClickListener(table, triggerComponents));
	}

	@SuppressWarnings("unchecked")
	protected T getSelectedValue(Table table) {
		return (T) table.getValue();
	}

	protected abstract Button.ClickListener getClickListener(Table table,
			Component... triggerComponents);
}
