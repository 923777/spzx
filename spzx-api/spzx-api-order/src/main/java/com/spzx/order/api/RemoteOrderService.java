package com.spzx.order.api;


import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.order.domain.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(contextId = "remoteOrderService",value = "spzx-order", fallbackFactory = RemoteOrderFallbackFactory.class)
public interface RemoteOrderService {
    @GetMapping("/getOrderInfoByOrderNo/{orderNo}")
    R<OrderInfo> getOrderInfoByOrderNo(@PathVariable("orderNo") String orderNo,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
