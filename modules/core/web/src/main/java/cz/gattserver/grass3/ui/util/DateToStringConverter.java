package cz.gattserver.grass3.ui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class DateToStringConverter implements Converter<Date, String> {
	private static final long serialVersionUID = -2914696445291603483L;

	private String dateFormat;

	public DateToStringConverter() {
		dateFormat = "d.MM.yyyy";
	}

	public DateToStringConverter(String format) {
		dateFormat = format;
	}

	@Override
	public String convertToModel(Date value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value == null ? null : getFormat().format(value);
	}

	@Override
	public Date convertToPresentation(String value, Class<? extends Date> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return value == null ? null : getFormat().parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public DateFormat getFormat() {
		return new SimpleDateFormat(dateFormat);
	}

	@Override
	public Class<String> getModelType() {
		return String.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return Date.class;
	}

}