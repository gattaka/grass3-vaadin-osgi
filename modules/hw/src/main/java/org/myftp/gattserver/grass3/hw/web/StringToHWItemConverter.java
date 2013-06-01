package org.myftp.gattserver.grass3.hw.web;

import java.util.Locale;

import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;

import com.vaadin.data.util.converter.Converter;

public class StringToHWItemConverter implements Converter<String, HWItemDTO> {

	private static final long serialVersionUID = 5485483888763389456L;

	@Override
	public HWItemDTO convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return null;
	}

	@Override
	public String convertToPresentation(HWItemDTO value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value == null ? "" : value.getName();
	}

	@Override
	public Class<HWItemDTO> getModelType() {
		return HWItemDTO.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
