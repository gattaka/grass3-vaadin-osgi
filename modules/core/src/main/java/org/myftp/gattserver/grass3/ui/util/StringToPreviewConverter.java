package org.myftp.gattserver.grass3.ui.util;

import java.util.Locale;

import org.myftp.gattserver.grass3.util.StringPreviewCreator;

import com.vaadin.data.util.converter.Converter;

public class StringToPreviewConverter implements Converter<String, String> {

	private static final long serialVersionUID = -5333583811109685442L;

	private int previewLength;

	public StringToPreviewConverter(int previewLength) {
		this.previewLength = previewLength;
	}

	@Override
	public String convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value;
	}

	@Override
	public String convertToPresentation(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return StringPreviewCreator.createPreview(value, previewLength);
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}