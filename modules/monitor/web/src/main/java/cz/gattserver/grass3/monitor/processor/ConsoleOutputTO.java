package cz.gattserver.grass3.monitor.processor;

public class ConsoleOutputTO {

	private String output;
	private boolean error;

	public ConsoleOutputTO(String output) {
		this(output, false);
	}

	public ConsoleOutputTO(String output, boolean error) {
		this.output = output;
		this.error = error;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

}
