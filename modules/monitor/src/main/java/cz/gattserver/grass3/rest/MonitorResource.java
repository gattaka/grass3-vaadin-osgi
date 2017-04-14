package cz.gattserver.grass3.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.monitor.facade.IMonitorFacade;

@Controller
@RequestMapping("/system-monitor")
public class MonitorResource {

	private static Logger logger = LoggerFactory.getLogger(MonitorResource.class);

	@Autowired
	private IMonitorFacade monitorFacade;

	@RequestMapping(value = "online", headers = "Accept=application/json")
	@ResponseBody
	public boolean isOnline() {
		return true;
	}

	public MonitorResource() {
		logger.info("System monitor resource online");
	}

}
