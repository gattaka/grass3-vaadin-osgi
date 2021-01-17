package cz.gattserver.grass3.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass3.interfaces.UserInfoTO;
import cz.gattserver.grass3.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.DiskStatusPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SMARTPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServersPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.ServicesPartItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass3.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass3.monitor.services.MonitorService;
import cz.gattserver.grass3.services.SecurityService;

@Controller
@RequestMapping("/system-monitor")
public class MonitorResource {

	private static Logger logger = LoggerFactory.getLogger(MonitorResource.class);

	@Autowired
	private MonitorService monitorFacade;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "online", headers = "Accept=application/json")
	@ResponseBody
	public boolean isOnline() {
		return true;
	}

	@RequestMapping("/services")
	public ResponseEntity<ServicesPartItemTO> getServicesStatus() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getServicesStatus(), HttpStatus.OK);
	}

	@RequestMapping("/servers")
	public ResponseEntity<ServersPartItemTO> getServersStatus() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getServersStatus(), HttpStatus.OK);
	}

	@RequestMapping("/system-uptime")
	public ResponseEntity<SystemUptimeMonitorItemTO> getSystemUptime() {
		return new ResponseEntity<>(monitorFacade.getSystemUptime(), HttpStatus.OK);
	}

	@RequestMapping("/system-memory-status")
	public ResponseEntity<SystemMemoryMonitorItemTO> getSystemMemoryStatus() {
		return new ResponseEntity<>(monitorFacade.getSystemMemoryStatus(), HttpStatus.OK);
	}

	@RequestMapping("/system-swap-status")
	public ResponseEntity<SystemSwapMonitorItemTO> getSystemSwapStatus() {
		return new ResponseEntity<>(monitorFacade.getSystemSwapStatus(), HttpStatus.OK);
	}

	@RequestMapping("/jvm-uptime")
	public ResponseEntity<JVMUptimeMonitorItemTO> getJVMUptime() {
		return new ResponseEntity<>(monitorFacade.getJVMUptime(), HttpStatus.OK);
	}

	@RequestMapping("/jvm-pid")
	public ResponseEntity<JVMPIDMonitorItemTO> getJVMPID() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getJVMPID(), HttpStatus.OK);
	}

	@RequestMapping("/jvm-threads")
	public ResponseEntity<JVMThreadsMonitorItemTO> getJVMThreads() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getJVMThreads(), HttpStatus.OK);
	}

	@RequestMapping("/jvm-memory")
	public ResponseEntity<JVMMemoryMonitorItemTO> getJVMMemory() {
		return new ResponseEntity<>(monitorFacade.getJVMMemory(), HttpStatus.OK);
	}

	@RequestMapping("/backup-status")
	public ResponseEntity<BackupStatusPartItemTO> getBackupDiskMounted() {
		return new ResponseEntity<>(monitorFacade.getBackupStatus(), HttpStatus.OK);
	}

	@RequestMapping("/disk-status")
	public ResponseEntity<DiskStatusPartItemTO> getDiskStatus() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getDiskStatus(), HttpStatus.OK);
	}

	@RequestMapping("/smart-info")
	public ResponseEntity<SMARTPartItemTO> getSMARTInfo() {
		UserInfoTO user = securityService.getCurrentUser();
		if (user.getId() == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(monitorFacade.getSMARTInfo(), HttpStatus.OK);
	}

	public MonitorResource() {
		logger.info("System monitor resource online");
	}

}
