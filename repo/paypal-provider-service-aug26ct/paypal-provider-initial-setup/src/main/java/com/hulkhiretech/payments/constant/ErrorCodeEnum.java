package com.hulkhiretech.payments.constant;

/**
 * ErrorCodeEnum - Enum for error codes and messages
 */
public enum ErrorCodeEnum {
	
	GENERIC_ERROR("30000", "Unable to process the request. Please try again later."),
	INVALID_NAME("30001", "Name cannot be null"),
	UNABLE_TO_CONNECT_TO_EXTERNAL_SERVICE("30002", "Unable to connect to external service. Please try again later."),
	PAYPAL_CREATE_ORDER_FAILED("30003","failed to create order with paypal.please try again later"),
	PAYPAL_CREATE_ORDER_ERROR("30004","<paypal error.prepare message dynamically from paypal response>"), 
	PAYPAL_CAPTURE_ORDER_FAILED("30005","failed to capture order with paypal. please try again later."),
	PAYPAL_CAPTURE_ORDER_ERROR("30006","<paypal error.prepare message dynamically from paypal response>");
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
