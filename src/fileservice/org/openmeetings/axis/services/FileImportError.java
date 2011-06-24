package org.openmeetings.axis.services;

public class FileImportError {
	
	private int exitValue = 0;
	private String error = "";
	private String process = "";
	private String command = "";
	
	public int getExitValue() {
		return exitValue;
	}
	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

}
