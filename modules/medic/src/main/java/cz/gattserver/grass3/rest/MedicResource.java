package cz.gattserver.grass3.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import cz.gattserver.grass3.medic.facade.IMedicFacade;

@Controller
@RequestMapping("/medic")
public class MedicResource {

	private static Logger logger = LoggerFactory.getLogger(MedicResource.class);

	@Autowired
	private IMedicFacade medicFacade;

	@RequestMapping(value = "visit", headers = "Accept=application/json")
	@ResponseBody
	public List<ScheduledVisitDTO> getInstitutions() {
		return medicFacade.getAllScheduledVisits();
	}

	public MedicResource() {
		logger.info("Medic resource online");
	}

}
