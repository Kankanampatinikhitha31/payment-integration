package com.hulkhiretech.payments.constant;

public class Constant {
	
	public static String PAYPAL_REQUEST_ID_HEADER;
	private Constant() {}
	
	public static final String CLIENT_CREDENTIALS = "client_credentials";

	public static final String GRANT_TYPE = "grant_type";

	// PayPal Order Creation Constants
	public static final String ORDER_INTENT_CAPTURE = "CAPTURE";
	public static final String PAYMENT_METHOD_PREFERENCE = "IMMEDIATE_PAYMENT_REQUIRED";
	public static final String LANDING_PAGE = "LOGIN";
	public static final String SHIPPING_PREFERENCE = "NO_SHIPPING";
	public static final String USER_ACTION = "PAY_NOW";

}