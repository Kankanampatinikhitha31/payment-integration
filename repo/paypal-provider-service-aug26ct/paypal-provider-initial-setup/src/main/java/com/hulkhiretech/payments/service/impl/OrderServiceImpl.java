package com.hulkhiretech.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.PaypalProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.PaypalOrderRes;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.service.client.PaypalClient;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.interfaces.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final PaypalClient paypalClient;
	private final CreateOrderHelper createOrderHelper;
	private final CaptureOrderHelper captureOrderHelper;
	private final ObjectMapper objectMapper;
	
	
	@Override
	public OrderRes createOrder(CreateOrderReq createOrderReq) {
		log.info("OrderService.createOrder called"+ "CreateOrderReq : {} ",createOrderReq);
		
		String name="";
		name.length();
		
	
		if(name==null) {
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_NAME.getErrorCode(), 
					ErrorCodeEnum.INVALID_NAME.getErrorMessage(), 
				
					HttpStatus.BAD_REQUEST);
		}

		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderPaypalRequest(createOrderReq);
		log.info("Prepared HttpRequest for Paypal create Order : {}",httpRequest);
		
			ResponseEntity<String>httpResponse=paypalClient.makeCall(httpRequest);
			log.info("PaypalClient.makeCall response:{}",httpResponse);

			PaypalOrderRes paypalresponseObj =createOrderHelper. processCreateOrderResponse(httpResponse); 
			log.info("processed paypal response in paypalorderRes:{}",paypalresponseObj);

			OrderRes orderRes=new OrderRes();
			orderRes.setOrderId(paypalresponseObj.getId());
			orderRes.setPaypalStatus(paypalresponseObj.getStatus());
			orderRes.setRedirectUrl(paypalresponseObj.getLinks().stream()
					.filter(link -> "payer-action".equals(link.getRel()))
					.map(PaypalOrderRes.Link::getHref)
					.findFirst()
					.orElse(null));
			log.info("Mapped PaypalOrderRes to OrderRes: {}", orderRes);

			return orderRes;



	}


	@Override
	public OrderRes captureOrder(String orderId) {
		log.info("OrderService: captureOrder called, orderId: {}", orderId);
		HttpRequest httpRequest = captureOrderHelper.prepareRequest(orderId);
		log.info("Prepared HttpRequest for Paypal create Order : {}",httpRequest);
		
		
		
		ResponseEntity<String>httpResponse=paypalClient.makeCall(httpRequest);
		log.info("PaypalClient.makeCall response:{}",httpResponse);
		
		
		PaypalOrderRes paypalresponseObj =captureOrderHelper. processResponse(httpResponse); 
		log.info("processed paypal response in paypalorderRes:{}",paypalresponseObj);
		
		
		
		OrderRes orderRes=new OrderRes();
		orderRes.setOrderId(paypalresponseObj.getId());
		orderRes.setPaypalStatus(paypalresponseObj.getStatus());
		log.info("mapped PaypalOrderRes to orderRes:{}",orderRes);
		
		return orderRes;
	}

	
}
