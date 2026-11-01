package com.hulkhiretech.payments.service.helper;

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
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest;
import com.hulkhiretech.payments.paypal.PaypalError;
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest.Amount;
import com.hulkhiretech.payments.paypal.PaypalCreateOrderRequest.ExperienceContext;
import com.hulkhiretech.payments.paypal.PaypalOrderRes;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.util.JsonUtil;
import com.hulkhiretech.payments.util.PaypalErrorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {


	private static final String PAYER_ACTION_REQUIRED = "PAYER_ACTION_REQUIRED";
	private final JsonUtil jsonUtil;
	@Value("${paypal.create.order.url}")
	private String createOrderUrl;

	public HttpRequest prepareCreateOrderPaypalRequest(CreateOrderReq createOrderReq) {

		HttpHeaders customHttpHeaders = new HttpHeaders();
		customHttpHeaders.add("Paypal-Request-Id", UUID.randomUUID().toString());
		customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
		//Note:Access token will be added in PaypalClient

		ExperienceContext experienceContext = PaypalCreateOrderRequest.ExperienceContext.builder()
				.paymentMethodPreference(Constant.PAYMENT_METHOD_PREFERENCE)
				.landingPage(Constant.LANDING_PAGE)
				.shippingPreference(Constant.SHIPPING_PREFERENCE)
				.userAction(Constant.USER_ACTION)
				.returnUrl(createOrderReq.getReturnUrl())
				.cancelUrl(createOrderReq.getCancelUrl())
				.build();
		Amount amount = PaypalCreateOrderRequest.Amount.builder()
				.currencyCode(createOrderReq.getCurrencyCode())
				.value(String.valueOf(createOrderReq.getAmount()))
				.build();
		PaypalCreateOrderRequest createOrderRequestObj = PaypalCreateOrderRequest.builder()
				.intent(Constant.ORDER_INTENT_CAPTURE)
				.paymentSource(
						PaypalCreateOrderRequest.PaymentSource.builder()
						.paypal(
								PaypalCreateOrderRequest.Paypal.builder()
								.experienceContext(experienceContext)
								.build()
								)
						.build()
						)
				.purchaseUnits(
						List.of(
								PaypalCreateOrderRequest.PurchaseUnit.builder()
								.amount(amount)
								.build()
								)
						)
				.build();
		String reqAsJson=jsonUtil.convertObjectToJson(createOrderRequestObj);

		log.info("PaypalCreateOrderRequest as Json : {}",reqAsJson);

		HttpRequest httpRequest=HttpRequest.builder()
				.httpMethod(HttpMethod.POST)
				.url(createOrderUrl)
				.httpheaders(customHttpHeaders)
				.body(reqAsJson)
				.build();
		return httpRequest;
	}

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
	
}


