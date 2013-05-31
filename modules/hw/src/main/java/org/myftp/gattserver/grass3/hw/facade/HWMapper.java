package org.myftp.gattserver.grass3.hw.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myftp.gattserver.grass3.hw.domain.HWItem;
import org.myftp.gattserver.grass3.hw.domain.HWItemType;
import org.myftp.gattserver.grass3.hw.domain.ServiceNote;
import org.myftp.gattserver.grass3.hw.dto.HWItemDTO;
import org.myftp.gattserver.grass3.hw.dto.HWItemTypeDTO;
import org.myftp.gattserver.grass3.hw.dto.ServiceNoteDTO;
import org.springframework.stereotype.Component;

@Component("hwMapper")
public class HWMapper {

	public HWItemTypeDTO mapHWItemType(HWItemType e) {
		if (e == null)
			return null;

		HWItemTypeDTO dto = new HWItemTypeDTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public Set<HWItemTypeDTO> mapHWItemTypes(Collection<HWItemType> list) {
		if (list == null)
			return null;

		Set<HWItemTypeDTO> dtos = new HashSet<HWItemTypeDTO>();
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
		dto.setDate(e.getDate());
		dto.setDescription(e.getDescription());
		dto.setState(e.getState());
		dto.setUsage(e.getUsage());
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
		dto.setDestructionDate(e.getDestructionDate());
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setUsage(e.getUsage());
		dto.setPrice(e.getPrice());
		dto.setPurchaseDate(e.getPurchaseDate());
		dto.setServiceNotes(mapServiceNotes(e.getServiceNotes()));
		dto.setState(e.getState());
		dto.setTypes(mapHWItemTypes(e.getTypes()));
		return dto;
	}

	public List<HWItemDTO> mapHWItems(Collection<HWItem> list) {
		if (list == null)
			return null;

		List<HWItemDTO> dtos = new ArrayList<HWItemDTO>();
		for (HWItem e : list) {
			dtos.add(mapHWItem(e));
		}
		return dtos;
	}

}
