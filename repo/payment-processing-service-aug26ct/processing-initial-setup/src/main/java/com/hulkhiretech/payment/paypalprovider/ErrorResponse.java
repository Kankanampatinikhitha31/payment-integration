package com.hulkhiretech.payment.paypalprovider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ErrorResponse - POJO for error response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

	private String errorCode;
	private String errorMessage;
}
