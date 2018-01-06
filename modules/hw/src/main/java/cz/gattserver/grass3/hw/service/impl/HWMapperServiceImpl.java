package cz.gattserver.grass3.hw.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cz.gattserver.common.util.DateUtil;
import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;
import cz.gattserver.grass3.hw.model.domain.HWItem;
import cz.gattserver.grass3.hw.model.domain.HWItemType;
import cz.gattserver.grass3.hw.model.domain.ServiceNote;
import cz.gattserver.grass3.hw.service.HWMapperService;

@Component
public class HWMapperServiceImpl implements HWMapperService {

	public HWItemTypeDTO mapHWItemType(HWItemType e) {
		if (e == null)
			return null;

		HWItemTypeDTO dto = new HWItemTypeDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public HWItemType mapHWItem(HWItemTypeDTO dto) {
		if (dto == null)
			return null;

		HWItemType e = new HWItemType();
		e.setId(dto.getId());
		e.setName(dto.getName());
		return e;
	}

	public Set<HWItemTypeDTO> mapHWItemTypes(Collection<HWItemType> list) {
		if (list == null)
			return null;

		Set<HWItemTypeDTO> dtos = new LinkedHashSet<HWItemTypeDTO>();
		for (HWItemType e : list) {
			dtos.add(mapHWItemType(e));
		}
		return dtos;
	}

	public ServiceNoteDTO mapServiceNote(ServiceNote e) {
		if (e == null)
			return null;

		ServiceNoteDTO dto = new ServiceNoteDTO();
		dto.setId(e.getId());
		dto.setDate(DateUtil.toLocalDate(e.getDate()));
		dto.setDescription(e.getDescription());
		dto.setState(e.getState());
		dto.setUsedInName(e.getUsage());
		return dto;
	}

	public List<ServiceNoteDTO> mapServiceNotes(Collection<ServiceNote> list) {
		if (list == null)
			return null;

		List<ServiceNoteDTO> dtos = new ArrayList<ServiceNoteDTO>();
		for (ServiceNote e : list) {
			dtos.add(mapServiceNote(e));
		}
		return dtos;
	}

	public HWItemDTO mapHWItem(HWItem e) {
		if (e == null)
			return null;

		HWItemDTO dto = new HWItemDTO();
		dto.setDestructionDate(DateUtil.toLocalDate(e.getDestructionDate()));
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setUsedIn(mapHWItemOverview(e.getUsedIn()));
		dto.setPrice(e.getPrice());
		dto.setPurchaseDate(DateUtil.toLocalDate(e.getPurchaseDate()));
		dto.setServiceNotes(mapServiceNotes(e.getServiceNotes()));
		dto.setSupervizedFor(e.getSupervizedFor());
		dto.setState(e.getState());
		dto.setTypes(mapHWItemTypes(e.getTypes()));
		dto.setWarrantyYears(e.getWarrantyYears());
		return dto;
	}

	public HWItemOverviewDTO mapHWItemOverview(HWItem e) {
		if (e == null)
			return null;

		HWItemOverviewDTO dto = new HWItemOverviewDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setUsedInName(e.getUsedIn() == null ? null : e.getUsedIn().getName());
		dto.setSupervizedFor(e.getSupervizedFor());
		dto.setPrice(e.getPrice());
		dto.setPurchaseDate(DateUtil.toLocalDate(e.getPurchaseDate()));
		dto.setState(e.getState());
		return dto;
	}

	public List<HWItemOverviewDTO> mapHWItems(Collection<HWItem> list) {
		if (list == null)
			return null;

		List<HWItemOverviewDTO> dtos = new ArrayList<HWItemOverviewDTO>();
		for (HWItem e : list) {
			dtos.add(mapHWItemOverview(e));
		}
		return dtos;
	}

}
