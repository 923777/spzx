package com.spzx.cart.service.impl;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.Product;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.domain.SkuPrice;
import io.lettuce.core.ScriptOutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ICartServiceImpl implements ICartService {
    @Autowired
    private RemoteProductService productService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addCart(Long skuId, Long skuNum) {
        Long userId = SecurityContextHolder.getUserId();
        CartInfo cartInfo = new CartInfo();
        Integer threshold = 99;
        BoundHashOperations<String,String,CartInfo> Hash = redisTemplate.boundHashOps(getCartKey(userId).toString());
        String cartKey = getCartKey(userId);
        if(Hash.hasKey(skuId.toString())){
            //更新
            CartInfo cartInfo1 = Hash.get(skuId.toString());
            int total =(int)(cartInfo1.getSkuNum()+skuNum) ;
            cartInfo1.setSkuNum(total > threshold ? threshold : total);
            Hash.put(skuId.toString(),cartInfo1);
        }else{

            R<ProductSku> RproductSku = productService.getProductSku(skuId, SecurityConstants.INNER);
            ProductSku productSku = RproductSku.getData();
            cartInfo.setSkuId(skuId);
            String skuName = productSku.getSkuName();
            cartInfo.setSkuName(skuName);

            cartInfo.setSkuNum(1);

            cartInfo.setCartPrice(productSku.getSalePrice());
            cartInfo.setIsChecked(1);
            R<SkuPrice> RskuPrice = productService.getSkuPrice(skuId, SecurityConstants.INNER);
            SkuPrice skuPrice = RskuPrice.getData();
            BigDecimal salePrice = skuPrice.getSalePrice();
            cartInfo.setSkuPrice(salePrice);

            cartInfo.setThumbImg(productSku.getThumbImg());

            cartInfo.setUserId(userId);
            cartInfo.setUpdateTime(new Date());
            cartInfo.setCreateTime(new Date());
            Hash.put(skuId.toString(),cartInfo);
        }



    }

    @Override
    public List<CartInfo> cartList() {
        Long userId = SecurityContextHolder.getUserId();
        BoundHashOperations<String,String,CartInfo> Hash = redisTemplate.boundHashOps(getCartKey(userId).toString());
        List<CartInfo> values = Hash.values();
return values;
    }

    @Override
    public void deleteCart(Long skuId) {
        redisTemplate.boundHashOps(getCartKey(SecurityContextHolder.getUserId())).delete(skuId.toString());
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {
        BoundHashOperations<String,String,CartInfo> ops = redisTemplate.boundHashOps(getCartKey(SecurityContextHolder.getUserId()).toString());
        CartInfo cartInfo = ops.get(skuId.toString());
        cartInfo.setIsChecked(isChecked);
        ops.put(skuId.toString(),cartInfo);
    }

    @Override
    public void allCheckCart(Integer isChecked) {
        BoundHashOperations<String,String,CartInfo> ops = redisTemplate.boundHashOps(getCartKey(SecurityContextHolder.getUserId()).toString());
        List<CartInfo> CartInfos= ops.values();
        CartInfos.forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            ops.put(cartInfo.getSkuId().toString(),cartInfo);
        });

    }

    @Override
    public void clearCart() {
       redisTemplate.delete(getCartKey(SecurityContextHolder.getUserId()));
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> ops = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfos = ops.values().stream().filter(cartInfo -> cartInfo.getIsChecked() == 1).collect(Collectors.toList());


        return cartInfos;
    }

    @Override
    public void deleteCartCheckedList(Long userId) {
        BoundHashOperations <String,String,CartInfo>ops = redisTemplate.boundHashOps(getCartKey(userId));
        ops.values().stream().filter(cartInfo -> cartInfo.getIsChecked() == 1).forEach(cartInfo -> {
            ops.delete(cartInfo.getSkuId().toString());
        });
    }

    public String getCartKey(Long userId) {
        return "user:cart" + userId;
    }
}
