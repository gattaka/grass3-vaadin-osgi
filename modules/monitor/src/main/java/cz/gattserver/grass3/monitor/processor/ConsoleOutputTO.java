package cz.gattserver.grass3.monitor.processor;

public class ConsoleOutputTO {

	private String output;
	private boolean success;

	public ConsoleOutputTO(String output) {
		this(output, true);
	}

	public ConsoleOutputTO(String output, boolean success) {
		this.output = output;
		this.success = success;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
