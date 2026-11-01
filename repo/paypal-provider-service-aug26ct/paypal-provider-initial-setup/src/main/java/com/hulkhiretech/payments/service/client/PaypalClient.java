package com.hulkhiretech.payments.service.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalClient {
	
	private final TokenService tokenService;
	private final HttpServiceEngine httpServiceEngine;
	public ResponseEntity<String> makeCall(HttpRequest httpRequest) {
		log.info("PaypalClient.makePaypalCall called"+" httpRequest :{}",httpRequest);
		
		String accessToken=tokenService.getAccessToken();
		HttpHeaders headers=httpRequest.getHttpheaders();
		if(headers==null) {
			headers=new HttpHeaders();
			httpRequest.setHttpheaders(headers);
		}
		headers.setBearerAuth(accessToken);
		
		//Make Http call using HttpServiceEngine
		ResponseEntity<String> httpResponse=httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpServiceEngine.makeHttpcall httpResponse:{}",httpResponse);
	   
		return httpResponse;
	
	}

}
