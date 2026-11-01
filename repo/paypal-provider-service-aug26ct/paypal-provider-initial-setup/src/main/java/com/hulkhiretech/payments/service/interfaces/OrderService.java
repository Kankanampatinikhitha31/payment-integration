package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;

public interface OrderService {
	 OrderRes createOrder(CreateOrderReq createOrderReq);
	
	OrderRes  captureOrder(String orderId);

	
}
