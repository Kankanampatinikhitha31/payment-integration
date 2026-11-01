package com.hulkhiretech.payment.service.interfaces;

import com.hulkhiretech.payment.pojo.PaymentResponse;
import com.hulkhiretech.payment.pojo.CreateTxnReq;
import com.hulkhiretech.payment.pojo.InitiateTxnReq;

public interface PaymentService {
    
	/**
	 * Creates a new payment
	 * @param createTxnReq
	 * @return PaymentResponse populated with created transaction details
	 */
	PaymentResponse createPayment(CreateTxnReq createTxnReq);
	
	PaymentResponse capturePayment(String txnReference);
	PaymentResponse initiatePayment(String txnReference, InitiateTxnReq initiateTxnReq);
	
    
}
