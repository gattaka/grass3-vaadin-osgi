package cz.gattserver.grass3.monitor.web.label;

import cz.gattserver.grass3.monitor.processor.ConsoleOutputTO;

public class MonitorItemFactory {

	public static MonitorItem createMonitorItem(ConsoleOutputTO consoleOutputTO) {
		if (consoleOutputTO.isError()) {
			return new ERRORMonitorItem(consoleOutputTO.getOutput());
		} else {
			return new OKMonitorItem(consoleOutputTO.getOutput());
		}
	}

}
