package cz.gattserver.grass3.template;

import com.vaadin.ui.Button;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.ui.AbstractSelect;

public abstract class TableSelectedItemBtn<T> extends Button {

	private static final long serialVersionUID = -5924239277930098183L;

	public TableSelectedItemBtn(String caption, final AbstractSelect table) {
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
		addClickListener(getClickListener(table));
	}

	@SuppressWarnings("unchecked")
	protected T getSelectedValue(AbstractSelect table) {
		return (T) table.getValue();
	}

	protected abstract Button.ClickListener getClickListener(AbstractSelect table);
}
