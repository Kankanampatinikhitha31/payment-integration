
package com.hulkhiretech.payment.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payment.constant.ErrorCodeEnum;
import com.hulkhiretech.payment.exception.ProcessingException;
import com.hulkhiretech.payment.http.HttpRequest;
import com.hulkhiretech.payment.paypalprovider.OrderRes;
import com.hulkhiretech.payment.paypalprovider.PPErrorResponse;
import com.hulkhiretech.payment.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaypalProviderCaptureOrderHelper {

    private static final String COMPLETED = "COMPLETED";

    private final JsonUtil jsonUtil;

    @Value("${paypal.provider.captureOrder.url.template}")
    private String paypalProviderCaptureOrderUrlTemplate;

    public HttpRequest prepareRequest(String providerReference) {

        HttpHeaders customHttpHeaders = new HttpHeaders();
        customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String url = paypalProviderCaptureOrderUrlTemplate
                .replace("{orderId}", providerReference);

        HttpRequest httpRequest = HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .url(url)
                .httpHeaders(customHttpHeaders)
                .body("")
                .build();

        return httpRequest;
    }

    public OrderRes processResponse(ResponseEntity<String> httpResponse) {

        log.info("Processing PayPal capture order response: {}", httpResponse);

        if (httpResponse.getStatusCode().is2xxSuccessful()) {

            OrderRes paypalResponseObj = jsonUtil.convertJsonToObject(
                    httpResponse.getBody(), OrderRes.class);

            log.info("PayPal response object: {}", paypalResponseObj);

            if (paypalResponseObj != null
                    && paypalResponseObj.getOrderId() != null
                    && COMPLETED.equalsIgnoreCase(
                            paypalResponseObj.getPaypalStatus())) {

                log.info("PayPal capture order successful");
                return paypalResponseObj;
            }

            log.error("PayPal capture response has invalid or missing data");
        }

        if (httpResponse.getStatusCode().is4xxClientError()
                || httpResponse.getStatusCode().is5xxServerError()) {

            log.error("PayPal capture order failed: {}", httpResponse);

            PPErrorResponse errorObj = jsonUtil.convertJsonToObject(
                    httpResponse.getBody(), PPErrorResponse.class);

            if (errorObj != null) {
                throw new ProcessingException(
                        errorObj.getErrorCode(),
                        errorObj.getErrorMessage(),
                        HttpStatus.valueOf(
                                httpResponse.getStatusCode().value()));
            }

            log.error("PayPal error response could not be parsed");
        }

        throw new ProcessingException(
                ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_FAILED.getErrorCode(),
                ErrorCodeEnum.PAYPAL_CAPTURE_ORDER_FAILED.getErrorMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
