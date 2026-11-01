package com.hulkhiretech.payment.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.http.HttpServiceEngine;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalClient {
	
	private final HttpServiceEngine httpServiceEngine;
	public ResponseEntity<String> makeCall(HttpRequest httpRequest) {
		log.info("PaypalClient.makePaypalCall called"+" httpRequest :{}",httpRequest);
		
		
		//Make Http call using HttpServiceEngine
		ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpServiceEngine.makeHttpcall httpResponse:{}",httpResponse);
	   
		return httpResponse;
	
	}

}
