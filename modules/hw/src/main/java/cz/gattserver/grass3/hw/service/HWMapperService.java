package cz.gattserver.grass3.hw.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.hw.interfaces.HWItemDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.interfaces.HWItemTypeDTO;
import cz.gattserver.grass3.hw.interfaces.ServiceNoteDTO;
import cz.gattserver.grass3.hw.model.domain.HWItem;
import cz.gattserver.grass3.hw.model.domain.HWItemType;
import cz.gattserver.grass3.hw.model.domain.ServiceNote;

public interface HWMapperService {

	public HWItemTypeDTO mapHWItemType(HWItemType e);

	public HWItemType mapHWItem(HWItemTypeDTO dto);

	public Set<HWItemTypeDTO> mapHWItemTypes(Collection<HWItemType> list);

	public ServiceNoteDTO mapServiceNote(ServiceNote e);

	public List<ServiceNoteDTO> mapServiceNotes(Collection<ServiceNote> list);

	public HWItemDTO mapHWItem(HWItem e);

	public HWItemOverviewDTO mapHWItemOverview(HWItem e);

	public List<HWItemOverviewDTO> mapHWItems(Collection<HWItem> list);

}
