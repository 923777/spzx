package com.spzx.order.api;


import com.spzx.common.core.domain.R;
import com.spzx.order.domain.OrderInfo;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RemoteOrderFallbackFactory implements FallbackFactory<RemoteOrderService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOrderFallbackFactory.class);

    @Override
    public RemoteOrderService create(Throwable throwable)
    {

        return new RemoteOrderService()
        {


            @Override
            public R<OrderInfo> getOrderInfoByOrderNo(String orderNo, String inner) {
                return R.fail("调用远程服务失败")   ;
            }
        };
    }

}
