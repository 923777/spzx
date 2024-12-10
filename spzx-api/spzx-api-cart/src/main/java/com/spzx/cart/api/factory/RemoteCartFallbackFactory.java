package com.spzx.cart.api.factory;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.domain.R;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RemoteCartFallbackFactory implements FallbackFactory<RemoteCartService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteCartFallbackFactory.class);

    @Override
    public RemoteCartService create(Throwable throwable)
    {

        return new RemoteCartService()
        {

            @Override
            public R<List<CartInfo>> getCartCheckedList(Long userId, String source) {
                return R.fail("获取用户购物车选中数据失败:" + throwable.getMessage());
            }

            @Override
            public R deleteCartCheckedList(Long userId, String source) {
                return R.fail("删除用户购物车选中数据失败:" + throwable.getMessage());

            }

        };
    }

}
