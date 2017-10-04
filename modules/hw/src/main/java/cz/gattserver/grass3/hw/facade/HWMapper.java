package cz.gattserver.grass3.hw.facade;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.hw.domain.HWItem;
import cz.gattserver.grass3.hw.domain.HWItemType;
import cz.gattserver.grass3.hw.domain.ServiceNote;
import cz.gattserver.grass3.hw.dto.HWItemDTO;
import cz.gattserver.grass3.hw.dto.HWItemOverviewDTO;
import cz.gattserver.grass3.hw.dto.HWItemTypeDTO;
import cz.gattserver.grass3.hw.dto.ServiceNoteDTO;

public interface HWMapper {

	public HWItemTypeDTO mapHWItemType(HWItemType e);

	public HWItemType mapHWItem(HWItemTypeDTO dto);

	public Set<HWItemTypeDTO> mapHWItemTypes(Collection<HWItemType> list);

	public ServiceNoteDTO mapServiceNote(ServiceNote e);

	public List<ServiceNoteDTO> mapServiceNotes(Collection<ServiceNote> list);

	public HWItemDTO mapHWItem(HWItem e);

	public HWItemOverviewDTO mapHWItemOverview(HWItem e);

	public List<HWItemOverviewDTO> mapHWItems(Collection<HWItem> list);

}
