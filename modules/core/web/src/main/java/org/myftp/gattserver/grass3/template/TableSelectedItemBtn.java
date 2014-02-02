package org.myftp.gattserver.grass3.template;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

public abstract class TableSelectedItemBtn<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public TableSelectedItemBtn(String caption, final AbstractSelect table,
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
	protected T getSelectedValue(AbstractSelect table) {
		return (T) table.getValue();
	}

	protected abstract Button.ClickListener getClickListener(AbstractSelect table,
			Component... triggerComponents);
}
