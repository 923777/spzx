package com.spzx.order.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItem extends BaseEntity {

    private Long orderId;
    private Long skuId;
    private String skuName;
    private String thumbImg;
    private BigDecimal skuPrice;
    private Integer skuNum;


}
