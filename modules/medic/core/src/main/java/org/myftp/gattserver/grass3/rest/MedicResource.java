package org.myftp.gattserver.grass3.rest;

import java.util.List;

import org.myftp.gattserver.grass3.medic.dto.ScheduledVisitDTO;
import org.myftp.gattserver.grass3.medic.facade.IMedicFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/medic")
public class MedicResource {

	private static Logger logger = LoggerFactory.getLogger(MedicResource.class);

	@Autowired
	private IMedicFacade medicFacade;

	@RequestMapping(value = "visit")
	@ResponseBody
	public List<ScheduledVisitDTO> getQuote() {
		return medicFacade.getAllScheduledVisits(true);
	}

	public MedicResource() {
		logger.info("Medic resource online");
	}

}
