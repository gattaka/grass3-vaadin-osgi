package cz.gattserver.grass3.ui.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableFunction;

import cz.gattserver.grass3.ui.components.DeleteButton;

public class TokenField extends Div {

	private static final long serialVersionUID = -4556540987839489629L;

	private Map<String, Button> tokens = new HashMap<>();
	private Div tokensLayout;

	private ComboBox<String> comboBox;

	public TokenField(FetchItemsCallback<String> fetchItemsCallback,
			SerializableFunction<String, Integer> serializableFunction) {
		tokensLayout = new ButtonLayout();
		add(tokensLayout);

		comboBox = new ComboBox<>();
		comboBox.setDataProvider(fetchItemsCallback, serializableFunction);
		comboBox.addCustomValueSetListener(e -> commitValue(e.getDetail()));
		comboBox.addValueChangeListener(e -> commitValue(e.getValue()));
		add(comboBox);
	}

	public TokenField setPlaceholder(String placeholder) {
		comboBox.setPlaceholder(placeholder);
		return this;	
	}

	private void commitValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			if (!tokens.containsKey(value)) {
				addToken(value);
				// tohle funguje i u custom value, narozdíl od clear(),
				// které dělá nastavení na null, což value u custom-value
				// stále je, takže se pole nevyčistí, protože nedošlo ke změně
				// hodnot (null -> null)
				comboBox.setValue("");
			}
		}
	}

	public void addToken(String string) {
		if (!tokens.containsKey(string)) {
			Button tokenComponent = new DeleteButton(string, e -> deleteToken(string));
			tokens.put(string, tokenComponent);
			tokensLayout.add(tokenComponent);
		}
	}

	public void deleteToken(String string) {
		Button tokenComponent = tokens.get(string);
		if (tokenComponent != null) {
			tokensLayout.remove(tokenComponent);
			tokens.remove(string);
		}
	}

	public TokenField setValues(Collection<String> tokens) {
		for (String s : tokens)
			addToken(s);
		return this;
	}

	public Set<String> getValues() {
		return new HashSet<>(tokens.keySet());
	}

}
