package com.spzx.channel.service.impl;


import com.alibaba.fastjson2.JSON;
import com.spzx.channel.config.ThreadPoolConfig;
import com.spzx.channel.domain.ItemVo;
import com.spzx.channel.service.IItemService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service

public class IItemServiceImpl implements IItemService {
    @Autowired
 private RemoteProductService remoteProductService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public ItemVo item(Long skuId) {
        ItemVo itemVo = new ItemVo();
        CompletableFuture<ProductSku> productSkuCompletableFuture = CompletableFuture.supplyAsync(new Supplier<ProductSku>() {
            @Override
            public ProductSku get() {
                R<ProductSku> R1 = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
                ProductSku productSku = R1.getData();
//                Long productId = productSku.getProductId();
                itemVo.setProductSku(productSku);
                return productSku;
            }
        }, threadPoolExecutor);
        CompletableFuture<Product> productCompletableFuture = productSkuCompletableFuture.thenApplyAsync(new Function<ProductSku, Product>() {
            @Override
            public Product apply(ProductSku productSku) {
                R<Product> R2 = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
                Product product = R2.getData();
                itemVo.setProduct(product);
                return product;
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> SkuPriceCompletableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                R<SkuPrice> R3 = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
                SkuPrice skuPrice = R3.getData();
                itemVo.setSkuPrice(skuPrice);
            }
        }, threadPoolExecutor);
        CompletableFuture<Void> sliderUrlsCompletableFuture = productCompletableFuture.thenAcceptAsync(new Consumer<Product>() {
            @Override
            public void accept(Product product) {
                String sliderUrls = product.getSliderUrls();
                List<String> list = Arrays.asList(sliderUrls.split(","));
                itemVo.setSliderUrlList(list);

                itemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));



            }
        }, threadPoolExecutor);

        CompletableFuture<Void> SkuStockVoCompletableFuture = productCompletableFuture.thenAcceptAsync(new Consumer<Product>() {
            @Override
            public void accept(Product product) {
                R<SkuStockVo> R5 = remoteProductService.getSkuStock(product.getId(), SecurityConstants.INNER);
                SkuStockVo skuStock = R5.getData();
                itemVo.setSkuStockVo(skuStock);


            }
        }, threadPoolExecutor);

        CompletableFuture<Void> SpecValueCompletableFuture = productCompletableFuture.thenAcceptAsync(new Consumer<Product>() {
            @Override
            public void accept(Product product) {
                Long id = product.getId();
//                System.out.println("id = " + id);
                R<Map<String, Long>> mapR = remoteProductService.getSkuSpecValue(id, SecurityConstants.INNER);
                Map<String, Long> map = mapR.getData();
//                System.out.println("map = " + map);
                itemVo.setSkuSpecValueMap(map);
            }
        }, threadPoolExecutor);

        CompletableFuture<ProductDetails> productDetailsCompletableFuture = productCompletableFuture.thenApplyAsync(new Function<Product, ProductDetails>() {
            @Override
            public ProductDetails apply(Product product) {
                Long id = product.getId();
                R<ProductDetails> R4 = remoteProductService.getProductDetails(id, SecurityConstants.INNER);
                ProductDetails productDetails = R4.getData();
                return productDetails;
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> DetailsimagesUrlthenAcceptAsync = productDetailsCompletableFuture.thenAcceptAsync(new Consumer<ProductDetails>() {
            @Override
            public void accept(ProductDetails productDetails) {
                String imageUrls = productDetails.getImageUrls();
                List<String> list1 = Arrays.asList(imageUrls.split(","));
                itemVo.setDetailsimagesUrlList(list1);

            }
        }, threadPoolExecutor);

        CompletableFuture<Void> imageUrlsCompletableFuture = productDetailsCompletableFuture.thenAcceptAsync(new Consumer<ProductDetails>() {
            @Override
            public void accept(ProductDetails productDetails)    {
                String imageUrls = productDetails.getImageUrls();
                List<String> list1 = Arrays.asList(imageUrls.split(","));
                itemVo.setDetailsimagesUrlList(list1);
            }
        }, threadPoolExecutor);
CompletableFuture.allOf(productCompletableFuture, SkuPriceCompletableFuture, sliderUrlsCompletableFuture, SkuStockVoCompletableFuture, SpecValueCompletableFuture, DetailsimagesUrlthenAcceptAsync, imageUrlsCompletableFuture, productDetailsCompletableFuture, productSkuCompletableFuture).join();


        return itemVo;
    }
//    @Override
//    public ItemVo item(Long skuId) {
//        ItemVo itemVo = new ItemVo();
//        R<ProductSku> R1 = remoteProductService.getProductSku(skuId, SecurityConstants.INNER);
//        ProductSku productSku = R1.getData();
//        Long productId = productSku.getProductId();
//        itemVo.setProductSku(productSku);
//
//        R<Product> R2 = remoteProductService.getProduct(productSku.getProductId(), SecurityConstants.INNER);
//        Product product = R2.getData();
//        itemVo.setProduct(product);
//
//        R<SkuPrice> R3 = remoteProductService.getSkuPrice(skuId, SecurityConstants.INNER);
//        SkuPrice skuPrice = R3.getData();
//        itemVo.setSkuPrice(skuPrice);
//
//        String sliderUrls = product.getSliderUrls();
//        List<String> list = Arrays.asList(sliderUrls.split(","));
//        itemVo.setSliderUrlList(list);
//
//        R<ProductDetails> R4 = remoteProductService.getProductDetails(productId, SecurityConstants.INNER);
//        ProductDetails productDetails = R4.getData();
//        String imageUrls = productDetails.getImageUrls();
//        List<String> list1 = Arrays.asList(imageUrls.split(","));
//        itemVo.setDetailsimagesUrlList(list1);
//        itemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));
//
//        R<SkuStockVo> R5 = remoteProductService.getSkuStock(productId, SecurityConstants.INNER);
//        SkuStockVo skuStock = R5.getData();
//        itemVo.setSkuStockVo(skuStock);
//        R<Map<String, Long>> R6 = remoteProductService.getSkuSpecValue(productId, SecurityConstants.INNER);
//        Map<String, Long> map = R6.getData();
//
//        itemVo.setSkuSpecValueMap(map);
//        return itemVo;
//    }
}
