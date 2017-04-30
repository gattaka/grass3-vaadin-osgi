package cz.gattserver.grass3.ui.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class StringToLongDateConverter implements Converter<String, Long> {
	private static final long serialVersionUID = -2914696445291603483L;

	private String dateFormat;

	public StringToLongDateConverter() {
		dateFormat = "d.MM.yyyy";
	}

	public StringToLongDateConverter(String format) {
		dateFormat = format;
	}

	protected DateFormat getFormat(Locale locale) {
		return new SimpleDateFormat(dateFormat);
	}

	public DateFormat getFormat() {
		return getFormat(null);
	}

	@Override
	public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (targetType != getModelType()) {
			throw new ConversionException("Converter only supports " + getModelType().getName() + " (targetType was "
					+ targetType.getName() + ")");
		}

		if (value == null) {
			return null;
		}

		// Remove leading and trailing white space
		value = value.trim();

		ParsePosition parsePosition = new ParsePosition(0);
		Long parsedValue = getFormat(locale).parse(value, parsePosition).getTime();
		if (parsePosition.getIndex() != value.length()) {
			throw new ConversionException("Could not convert '" + value + "' to " + getModelType().getName());
		}

		return parsedValue;
	}

	@Override
	public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (value == null) {
			return null;
		}

		return getFormat(locale).format(new Date(value));
	}

	@Override
	public Class<Long> getModelType() {
		return Long.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
