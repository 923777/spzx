package com.spzx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.*;

import java.util.List;
import java.util.Map;

public interface ProductService extends IService<Product> {

    //查询商品列表
    List<Product> selectProductList(Product product);

    int insertProduct(Product product);

    //获取商品详细信息
    Product selectProductById(Long id);

    int updateProduct(Product product);

    int deleteProductByIds(Long[] ids);

    //查询畅销商品
    List<ProductSku> getTopSale();

    //远程调用使用，商品列表
    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);


    ProductSku getProductSku(Long skuId);

    Product getProduct(Long id);

    SkuPrice getSkuPrice(Long skuId);

    ProductDetails getProductDetails(Long id);

    SkuStockVo getSkuStock(Long skuId);

    Map<String, Long> getSkuSpecValue(Long id);

    List<SkuPrice> getSkuPriceList(List<Long> skuIds);



    String checkAndLock(List<SkuLockVo> skuLockVos, String orderNo);

    void unlock(String orderNo);

    void minus(String orderNo);
}
