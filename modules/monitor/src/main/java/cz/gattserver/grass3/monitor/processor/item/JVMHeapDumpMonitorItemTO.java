package cz.gattserver.grass3.monitor.processor.item;

import java.util.List;

public class JVMHeapDumpMonitorItemTO extends MonitorItemTO {

	private List<String> dump;

	public List<String> getDump() {
		return dump;
	}

	public void setDump(List<String> dump) {
		this.dump = dump;
	}

}
