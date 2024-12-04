package com.spzx.product.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.domain.*;
import com.spzx.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品Controller
 */
@Tag(name = "商品接口管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Resource
    private ProductService productService;

    @Operation(summary = "获取销量好的sku")
    @GetMapping("getTopSale")
    public R<List<ProductSku>> getTopSale() {
        return R.ok(productService.getTopSale());
    }

    //添加
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Product product) {
        return toAjax(productService.insertProduct(product));
    }

    /**
     * 查询商品列表
     */
    @Operation(summary = "查询商品列表")
    @GetMapping("/list")
    public TableDataInfo list(Product product) {
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }

    /**
     * 获取商品详细信息
     */
    @Operation(summary = "获取商品详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productService.selectProductById(id));
    }

    /**
     * 修改商品
     *
     * @param product
     * @return
     */
    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult edit(@RequestBody Product product) {
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 删除商品
     *
     * @param ids
     * @return
     */
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productService.deleteProductByIds(ids));
    }

    //远程调用使用，商品列表
    @GetMapping("/skuList/{pageNum}/{pageSize}")
    public R<TableDataInfo> skuList(@PathVariable Integer pageNum,
                                    @PathVariable Integer pageSize,
                                    @ModelAttribute SkuQuery skuQuery) {
        //设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        //调用service方法根据条件查询
        List<ProductSku> list = productService.selectProductSkuList(skuQuery);

        TableDataInfo dataTable = getDataTable(list);
        return R.ok(dataTable);
    }

    @Operation(summary = "修改商品狀態")
    @GetMapping("/updateAuditStatus/{id}/{status}")
    public R updateAuditStatus(@PathVariable("id") long id, @PathVariable("status") long status) {
        boolean update = productService.update(new LambdaUpdateWrapper<Product>().eq(Product::getId, id).set(Product::getAuditStatus, status));
        return R.ok(update);
    }

    @Operation(summary = "获取商品sku信息")
    @InnerAuth
    @GetMapping(value = "/getProductSku/{skuId}")
    public R<ProductSku> getProductSku(@PathVariable("skuId") Long skuId) {
        return R.ok(productService.getProductSku(skuId));
    }

    @Operation(summary = "获取商品信息")
    @InnerAuth
    @GetMapping(value = "/getProduct/{id}")
    public R<Product> getProduct(@PathVariable("id") Long id) {
        return R.ok(productService.getProduct(id));
    }

    @Operation(summary = "获取商品sku最新价格信息")
    @InnerAuth
    @GetMapping(value = "/getSkuPrice/{skuId}")
    public R<SkuPrice> getSkuPrice(@PathVariable("skuId") Long skuId) {
        return R.ok(productService.getSkuPrice(skuId));
    }

    @Operation(summary = "获取商品详细信息")
    @InnerAuth
    @GetMapping(value = "/getProductDetails/{id}")
    public R<ProductDetails> getProductDetails(@PathVariable("id") Long id) {
        return R.ok(productService.getProductDetails(id));
    }

    @Operation(summary = "获取商品sku规则详细信息")
    @InnerAuth
    @GetMapping(value = "/getSkuSpecValue/{id}")
    public R<Map<String, Long>> getSkuSpecValue(@PathVariable("id") Long id) {
        Map<String, Long> map=    productService.getSkuSpecValue(id);
        return R.ok(map);
    }

    @Operation(summary = "获取商品sku库存信息")
    @InnerAuth
    @GetMapping(value = "/getSkuStock/{skuId}")
    public R<SkuStockVo> getSkuStock(@PathVariable("skuId") Long skuId) {
        return R.ok(productService.getSkuStock(skuId));
    }
    @PostMapping("/getSkuPriceList")
    @InnerAuth
    @Operation(summary = "获取商品sku价格信息")
    public R<List<SkuPrice>> getSkuPriceList(@RequestBody List<Long> skuIds){
        List<SkuPrice> list = productService.getSkuPriceList(skuIds);
        return R.ok(list);

    };



}
