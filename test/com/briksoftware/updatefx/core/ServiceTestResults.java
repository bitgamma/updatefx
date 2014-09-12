package com.briksoftware.updatefx.core;

import javafx.concurrent.Worker;

public class ServiceTestResults<T> {
	public Throwable exception;
	public Worker.State state;
	public T serviceResult;

	public ServiceTestResults(Worker.State state, T serviceResult, Throwable exception) {
		this.state = state;
		this.serviceResult = serviceResult;
		this.exception = exception;
	}
}
