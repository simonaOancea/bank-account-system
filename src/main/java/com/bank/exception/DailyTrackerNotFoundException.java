package com.bank.exception;

public class DailyTrackerNotFoundException extends RuntimeException {
	public DailyTrackerNotFoundException(String message) {
		super(message);
	}

	public DailyTrackerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
