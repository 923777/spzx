package com.spzx.cart.controller;

import com.spzx.cart.api.domain.CartInfo;
import com.spzx.cart.service.ICartService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车接口")
@RestController
@RequestMapping
public class CartController extends BaseController {
    @Autowired
    private ICartService cartService;
    @GetMapping("/addToCart/{skuId}/{skuNum}")
    @RequiresLogin
    @Operation(summary = "添加购物车")
    public void addCart(@PathVariable ("skuId") Long skuId, @PathVariable("skuNum") Long skuNum) {

        cartService.addCart(skuId, skuNum);
    }
    @GetMapping("/cartList")
    @Operation(summary = "购物车列表")
    @RequiresLogin
    public AjaxResult cartList() {

        return AjaxResult.success(cartService.cartList());

    }
    @DeleteMapping("/deleteCart/{skuId}")
    @RequiresLogin
    @Operation(summary = "删除购物车")
    public AjaxResult deleteCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable("skuId") Long skuId) {
        cartService.deleteCart(skuId);
        return success();
    }
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    @RequiresLogin
    @Operation(summary = "更改选中状态")
    public AjaxResult checkCart(@Parameter(name = "skuId", description = "商品skuId", required = true) @PathVariable("skuId") Long skuId
                                , @Parameter(description = "是否选中", required = true) @PathVariable("isChecked") Integer isChecked){
        cartService.checkCart(skuId, isChecked);
        return AjaxResult.success();
    }
    @GetMapping("/allCheckCart/{isChecked}")
    @RequiresLogin
    @Operation(summary = "更新购物车商品全部选中状态")
    public AjaxResult allCheckCart(
             @Parameter(description = "是否选中", required = true) @PathVariable("isChecked") Integer isChecked){
        cartService.allCheckCart( isChecked);
        return AjaxResult.success();
    }
    @GetMapping("/clearCart")
    @RequiresLogin
    @Operation(summary = "清空购物车")
    public AjaxResult clearCart(){
        cartService.clearCart();
        return AjaxResult.success();
    }
    @GetMapping("/getCartCheckedList/{userId}")
    @InnerAuth
    @Operation(summary = "查询中的购物车")
    public R<List<CartInfo>> getCartCheckedList(@PathVariable("userId") Long userId){
        List<CartInfo> cartInfos= cartService.getCartCheckedList(userId);

        return R.ok(cartInfos);

    };
    @GetMapping("/deleteCartCheckedList/{userId}")
    @InnerAuth
    @Operation(summary = "删除已勾选的购物车")
    R deleteCartCheckedList(@PathVariable("userId") Long userId){
        cartService.deleteCartCheckedList(userId);
        return R.ok();
    };

}
