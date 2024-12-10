package com.spzx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spzx.product.domain.ProductSku;
import com.spzx.product.domain.SkuQuery;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    //查询畅销商品
    List<ProductSku> selectTopSale();

    //远程调用使用，商品列表
    List<ProductSku> selectProductSkuList(SkuQuery skuQuery);

    int checkAndLock(@Param("skuId")Long skuId, @Param("skuNum")Integer skuNum);

    int unlock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    int minus(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
