package org.myftp.gattserver.grass3.hw.web;

import java.util.Locale;

import org.myftp.gattserver.grass3.hw.dto.HWItemState;

import com.vaadin.data.util.converter.Converter;

public class StringToHWItemStateConverter implements Converter<String, HWItemState> {
	private static final long serialVersionUID = 5485483888763389456L;

	@Override
	public HWItemState convertToModel(String value, Class<? extends HWItemState> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return HWItemState.valueOf(value);
	}

	@Override
	public String convertToPresentation(HWItemState value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.getName();
	}

	@Override
	public Class<HWItemState> getModelType() {
		return HWItemState.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
