package com.hulkhiretech.payment.service.helper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.exception.PaypalProviderException;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {


	private static final String PAYER_ACTION_REQUIRED = "PAYER_ACTION_REQUIRED";
	private final JsonUtil jsonUtil;
	@Value("${paypal.provider.createOrder.url}")
	private String paypalProviderCreateOrderUrl;

	public HttpRequest prepareCreateOrderPaypalRequest() {

		HttpHeaders customHttpHeaders = new HttpHeaders();
		//customHttpHeaders.add("Paypal-Request-Id", UUID.randomUUID().toString());
		customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
		//Note:Access token will be added in PaypalClient

		
		String reqAsJson=jsonUtil.convertObjectToJson(null);

		log.info("PaypalCreateOrderRequest as Json : {}",reqAsJson);

		HttpRequest httpRequest=HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url(paypalProviderCreateOrderUrl)
				.httpHeaders(customHttpHeaders)
				.body(reqAsJson)
				.build();
		return httpRequest;
	}
/*

	public PaypalOrderRes processCreateOrderResponse(ResponseEntity<String> httpResponse) {
		log.info("processing paypal create order response :{}",httpResponse);

		if(httpResponse.getStatusCode().is2xxSuccessful()) {

			PaypalOrderRes paypalresponseObj=jsonUtil.convertJsonToObject(
					httpResponse.getBody(),PaypalOrderRes.class);
			log.info("converting paypal response json to paypalOrdereRes"+ "Object :{}",paypalresponseObj);


			if(paypalresponseObj!=null 
					&&paypalresponseObj.getId()!=null
					&& PAYER_ACTION_REQUIRED.equalsIgnoreCase(paypalresponseObj.getStatus())){
				log.info("paypal response is success");
				return paypalresponseObj;

			}
			log.error("paypal create order response is not successful or does not contain expected data"
					+"paypalresponseObj:{}",paypalresponseObj);
		}

			// if 4xx or 5xx
			if(httpResponse.getStatusCode().is4xxClientError()
					|| httpResponse.getStatusCode().is5xxServerError()) {
				log.error("paypal create order response is not successful." +"httpResponse:{}",httpResponse);

				PaypalError errorObj=jsonUtil.convertJsonToObject(httpResponse.getBody(), PaypalError.class);
				
				
				
				String errorMessage = PaypalErrorUtil.generateErrorMessage(errorObj);
				log.error("Paypal error details: {}", errorMessage);



               throw new PaypalProviderException( 
						ErrorCodeEnum.PAYPAL_CREATE_ORDER_ERROR.getErrorCode(),
						errorMessage,
						HttpStatus.valueOf(httpResponse.getStatusCode().value()));

			}
			log.error("paypal error response is null or could not be parsed."+"httpResponse:{}",httpResponse);


		log.error("paypal create order response is not successful or does not contain expected data"
				+"httpResponse:{}",httpResponse);
		throw new PaypalProviderException( 
				ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorCode(),
				ErrorCodeEnum.PAYPAL_CREATE_ORDER_FAILED.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);


	}
	*/
}


