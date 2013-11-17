package org.myftp.gattserver.grass3.medic.web.templates;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public abstract class TableSelectedItemBtn<T> extends GrassBtn {

	private static final long serialVersionUID = -5924239277930098183L;

	private Table table;
	
	public TableSelectedItemBtn(String caption, final Table table,
			Component... triggerComponents) {
		super(caption, triggerComponents);
		this.table = table;
		setEnabled(false);
		table.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8943196289027284739L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean enabled = table.getValue() != null;
				TableSelectedItemBtn.this.setEnabled(enabled);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected T getSelectedValue(Table table) {
		return (T) table.getValue();
	}

	@Override
	protected ClickListener getClickListener(Component... triggerComponents) {
		return getClickListener(table, triggerComponents);
	}
	
	protected abstract Button.ClickListener getClickListener(Table table,
			Component... triggerComponents);
}
