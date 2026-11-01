package com.hulkhiretech.payments.pojo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateOrderReq {

	private String returnUrl;

	private String cancelUrl;

	private String currencyCode;

	private BigDecimal amount;

}
