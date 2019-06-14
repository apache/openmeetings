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
package org.apache.openmeetings.util.process;

/**
 * Object created by each conversion step, each conversion step that invokes and
 * external tool like ImageMagick is one single ConverterProcessMessage
 *
 * if exitValue equals "-1" then there was an issue.
 *
 * @author sebawagner
 *
 */
public class ProcessResult {
	public static final Integer ZERO = Integer.valueOf(0);

	private String process;
	private String command;
	private String exception;
	private String error;
	private Integer exitCode;
	private String out;
	private boolean optional = false;

	public ProcessResult() {
		this(null, null, null);
	}

	public ProcessResult(String error) {
		this(null, error, null);
	}

	public ProcessResult(String process, String error, Exception ex) {
		setProcess(process);
		setException(ex == null ? null : ex.toString());
		setError(error);
		setExitCode(-1);
	}

	public String getOut() {
		return out;
	}

	public ProcessResult setOut(String out) {
		this.out = out;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public ProcessResult setCommand(String command) {
		this.command = command;
		return this;
	}

	public String getProcess() {
		return process;
	}

	public ProcessResult setProcess(String process) {
		this.process = process;
		return this;
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

	public ProcessResult setError(String error) {
		this.error = error;
		return this;
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public ProcessResult setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
		return this;
	}

	public boolean isOptional() {
		return optional;
	}

	public ProcessResult setOptional(boolean optional) {
		this.optional = optional;
		return this;
	}

	public boolean isOk() {
		return optional || !isWarn();
	}

	public boolean isWarn() {
		return !ZERO.equals(exitCode);
	}

	public String buildLogMessage() {
		return new StringBuilder()
			.append("process: ").append(process).append("\r\n")
			.append("command: ").append(command).append("\r\n")
			.append("exception: ").append(exception).append("\r\n")
			.append("error: ").append(error).append("\r\n")
			.append("exitValue: ").append(exitCode).append("\r\n")
			.append("optional: ").append(optional).append("\r\n")
			.append("out: ").append(out).append("\r\n").toString();
	}
}
