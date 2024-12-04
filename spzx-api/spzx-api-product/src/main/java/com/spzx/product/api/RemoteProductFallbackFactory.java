package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.*;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@Component
public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService>
{

    @Override
    public RemoteProductService create(Throwable throwable) {
        return new RemoteProductService() {
            @Override
            public R<List<ProductSku>> getTopSale(String source) {
                return R.fail("查询畅销商品出错....");
            }

            @Override
            public R<TableDataInfo> skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery, String source) {
                return  R.fail("获取商品skulist失败:" + throwable.getMessage());
            }

            @Override
            public R<ProductSku> getProductSku(Long skuId, String source) {
                return R.fail("获取商品sku失败:" + throwable.getMessage());
            }

            @Override
            public R<Product> getProduct(Long id, String source) {
                return R.fail("获取商品信息失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuPrice> getSkuPrice(Long skuId, String source) {
                return R.fail("获取商品sku价格失败:" + throwable.getMessage());
            }

            @Override
            public R<ProductDetails> getProductDetails(Long id, String source) {
                return R.fail("获取商品详情失败:" + throwable.getMessage());
            }

            @Override
            public R<Map<String, Long>> getSkuSpecValue(Long id, String source) {
                return R.fail("获取商品sku规格失败:" + throwable.getMessage());
            }

            @Override
            public R<SkuStockVo> getSkuStock(Long skuId, String source) {
                return R.fail("获取商品sku库存失败:" + throwable.getMessage());
            }
            public R<List<SkuPrice>> getSkuPriceList( List<Long> skuIds,String source){
                return R.fail("获取价格列表失败:" + throwable.getMessage());

            };
        };

    }

}
