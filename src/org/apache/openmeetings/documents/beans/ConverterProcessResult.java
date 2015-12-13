/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.documents.beans;

/**
 * Object created by each conversion step,
 * each conversion step that invokes and external tool like ImageMagick is one single 
 * ConverterProcessMessage
 * 
 * if exitValue equals "-1" then there was an issue.
 * 
 * @author sebawagner
 *
 */
public class ConverterProcessResult {
	
	private String process;
	private String command;
	private String exception;
	private String error;
	private String exitValue;
	private String out;
	
	public ConverterProcessResult() {
		
	}
	
	public ConverterProcessResult (String process,
			String error, Exception ex) {
		setProcess(process);
		setException(ex.toString());
		setError(error);
		setExitValue("-1");
	}
	
	public ConverterProcessResult(String error) {
		setError(error);
		setExitValue("-1");
	}

	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getExitValue() {
		return exitValue;
	}
	public void setExitValue(String exitValue) {
		this.exitValue = exitValue;
	}
	
	public String buildLogMessage() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("process: ");
		strBuilder.append(getProcess());
		strBuilder.append("\r\n");
		strBuilder.append("command; ");
		strBuilder.append(getCommand());
		strBuilder.append("\r\n");
		strBuilder.append("exception: ");
		strBuilder.append(getException());
		strBuilder.append("\r\n");
		strBuilder.append("error: ");
		strBuilder.append(getError());
		strBuilder.append("\r\n");
		strBuilder.append("exitValue: ");
		strBuilder.append(getException());
		strBuilder.append("\r\n");
		strBuilder.append("out: ");
		strBuilder.append(getOut());
		strBuilder.append("\r\n");
		return strBuilder.toString();
	}

}
