package com.spzx.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.factory.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.common.rabbit.service.RabbitService;
import com.spzx.order.configure.DelayedMqOrderConfig;
import com.spzx.order.domain.*;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.mapper.OrderItemMapper;
import com.spzx.order.mapper.OrderLogMapper;
import com.spzx.order.service.IOrderInfoService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.SkuLockVo;
import com.spzx.product.domain.SkuPrice;
import com.spzx.user.api.RemoteUserInfoService;
import com.spzx.user.domain.UserAddress;
import org.apache.poi.hpsf.Decimal;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class IOrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper,OrderInfo> implements IOrderInfoService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RemoteCartService remoteCartService;
    @Autowired
    private RemoteProductService remoteProductService;
    @Autowired
    private RemoteUserInfoService remoteUserInfoService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Autowired
    private RabbitService rabbitService;

    @Override
    public TradeVo trade() {
        TradeVo tradeVo = new TradeVo();
        Long userId = SecurityContextHolder.getUserId();
        String tradeNo = generateTradeNo(userId);
        tradeVo.setTradeNo(tradeNo);

        List<CartInfo> cartCheckedList = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER).getData();
        List<OrderItem> orderItemList = cartCheckedList.stream().map(cartInfo -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setSkuPrice(cartInfo.getSkuPrice());
            orderItem.setThumbImg(cartInfo.getThumbImg());
            return orderItem;
        }).collect(Collectors.toList());
        tradeVo.setOrderItemList(orderItemList);
        BigDecimal totalAmount = new BigDecimal(0);
        for (CartInfo cartInfo : cartCheckedList) {
            totalAmount = totalAmount.add(cartInfo.getSkuPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
        }
        tradeVo.setTotalAmount(totalAmount);

        return tradeVo;
    }

    @Override
    public Long submitOrder(OrderForm orderForm) {
        //检验用户流水号，是否过期
        Long userId = SecurityContextHolder.getUserId();
        String userTradeKey = "user:tradeNo:" + userId;
        String  tradeNo = (String)redisTemplate.opsForValue().get(userTradeKey);
        Assert.isTrue(tradeNo.equals(tradeNo), "交易码无效");
        redisTemplate.delete(userTradeKey);// 交易码用过一次后删除

        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderForm.getTradeNo());
        orderInfo.setUserId(userId);
        orderInfo.setFeightFee(orderForm.getFeightFee());

        //计算总金额
        //查询现在的价格是否相等
        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        List<Long> skuIds = orderItemList.stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        List<SkuPrice> skuPrices = remoteProductService.getSkuPriceList(skuIds, "inner").getData();
        Map<Long,BigDecimal> nowSkuPrices =  skuPrices.stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
        for (OrderItem orderItem : orderItemList) {
           Assert.isTrue(nowSkuPrices.get(orderItem.getSkuId()).compareTo(orderItem.getSkuPrice()) == 0, "商品价格发生变化");
        }
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
            totalAmount= totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));

        }

        orderInfo.setTotalAmount(totalAmount);
        //通过adressid获取收货地址
//        UserAddress address = remoteUserInfoService.getById(orderForm.getUserAddressId(), "inner").getData();
//        orderInfo.setReceiverName(address.getName());
//        orderInfo.setReceiverPhone(address.getPhone());
//        String provinceCode = address.getProvinceCode();
//
//        orderInfo.setReceiverProvince(remoteUserInfoService.Select(provinceCode, "inner"));
//        orderInfo.setReceiverCity(remoteUserInfoService.Select(address.getCityCode(), "inner"));
//        orderInfo.setReceiverDistrict(remoteUserInfoService.Select(address.getDistrictCode(), "inner"));
//        orderInfo.setReceiverName(address.getName());
//        orderInfo.setReceiverAddress(address.getAddress());


//        orderInfo.setRemark(orderForm.getRemark());
//        //查询用户信息
//        orderInfo.setNickName(address.getName());
//        //收货人信息在地址

        //删除购物车
        remoteCartService.deleteCartCheckedList(userId, SecurityConstants.INNER);


        //锁定库存
        List<SkuLockVo> skuLockVos = orderItemList.stream().map(orderItem -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(orderItem.getSkuId());
            skuLockVo.setSkuNum(orderItem.getSkuNum());
            return skuLockVo;
        }).collect(Collectors.toList());
        String errMsg = remoteProductService.checkAndLock(tradeNo,skuLockVos, SecurityConstants.INNER).getData();// 接口二
        Assert.isTrue(StringUtils.isEmpty(errMsg), errMsg);// 返回结果断言，如果是非空说明锁定失败，抛出异常信息
        Long orderId = saveOrder(orderForm);
        //延迟关单
        rabbitService.sendDealyMessage(DelayedMqOrderConfig.EXCHANGE_ORDER_CANCEL_DELAY,DelayedMqOrderConfig.ROUTING_ORDER_CANCEL_DELAY,orderForm.getTradeNo(),60*3
        );


        return orderId;
    }

    @Override
    public void cencel(String tradeNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, tradeNo));
        Integer orderStatus = orderInfo.getOrderStatus();
        if (orderStatus == 0) {
            orderInfo.setOrderStatus(-1);
            orderInfoMapper.updateById(orderInfo);
            rabbitService.sendMessage(MqConst.EXCHANGE_PRODUCT,MqConst.ROUTING_UNLOCK,tradeNo);

        }else{
            return;
        }
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {

        return orderInfoMapper.selectById(orderId);
    }

    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderInfo.getId()));
        orderInfo.setOrderItems(orderItems);
        return orderInfo;
    }

    @Override
    public void updateOrderStaue(String orderNo) {
        orderInfoMapper.update(new OrderInfo(), new LambdaUpdateWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo).set(OrderInfo::getOrderStatus, 1));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(OrderForm orderForm) {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        String userName = SecurityContextHolder.getUserName();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderForm.getTradeNo());// 将交易码当作订单编码使用
        orderInfo.setUserId(userId);
        orderInfo.setNickName(userName);
        orderInfo.setRemark(orderForm.getRemark());
        UserAddress userAddress = remoteUserInfoService.getById(orderForm.getUserAddressId(), SecurityConstants.INNER).getData();// 接口四
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());

//        List<OrderItem> orderItemList = orderForm.getOrderItemList();
//        BigDecimal totalAmount = new BigDecimal(0);
//        for (OrderItem orderItem : orderItemList) {
//            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
//        }
//        orderInfo.setTotalAmount(totalAmount);
//        orderInfo.setCouponAmount(new BigDecimal(0));
//        orderInfo.setOriginalTotalAmount(totalAmount);


        List<OrderItem> orderItemList = orderForm.getOrderItemList();
        List<Long> skuIds = orderItemList.stream().map(OrderItem::getSkuId).collect(Collectors.toList());
        List<SkuPrice> skuPrices = remoteProductService.getSkuPriceList(skuIds, "inner").getData();
        Map<Long,BigDecimal> nowSkuPrices =  skuPrices.stream().collect(Collectors.toMap(SkuPrice::getSkuId, SkuPrice::getSalePrice));
        for (OrderItem orderItem : orderItemList) {
            Assert.isTrue(nowSkuPrices.get(orderItem.getSkuId()).compareTo(orderItem.getSkuPrice())==0,"商品价格发生变化");
        }
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderForm.getOrderItemList()) {
            totalAmount= totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));

        }

        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setFeightFee(orderForm.getFeightFee());
        //OrderInfo类的orderStatus属性的类型改为Integer
        orderInfo.setOrderStatus(0);
        orderInfo.setCreateTime(new Date());
        orderInfoMapper.insert(orderInfo);

        //保存订单明细
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
            orderItemMapper.insert(orderItem);
        }

        //记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0);
        orderLog.setNote("提交订单");
        orderLogMapper.insert(orderLog);
        return orderInfo.getId();
    }

    private String generateTradeNo(Long userId) {
        //1.构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;
        //2.构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        //3.将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey, tradeNo, 5, TimeUnit.MINUTES);
        return tradeNo;
    }
}
