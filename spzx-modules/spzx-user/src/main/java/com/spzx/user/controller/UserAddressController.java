package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.product.domain.ProductSku;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 用户地址Controller
 *
 * @author atguigu
 * @date 2024-12-05
 */
@Tag(name = "用户地址接口管理")
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController
{
    @Resource
    private IUserAddressService userAddressService;

    /**
     * 查询用户地址列表
     */
    @Operation(summary = "查询用户地址列表")
    @RequiresLogin
    @GetMapping("/list")
    public AjaxResult list()
    {
        List<UserAddress> list = userAddressService.selectUserAddressList();
        return success(list);
    }

    /**
     * 新增用户地址
     */
    @Operation(summary = "新增用户地址")
    @RequiresLogin
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.insertUserAddress(userAddress));
    }

    /**
     * 修改用户地址
     */
    @Operation(summary = "修改用户地址")
    @RequiresLogin
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.updateUserAddress(userAddress));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @RequiresLogin
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(userAddressService.removeById(id));
    }
    @Operation(summary = "获取地址详情")
    @InnerAuth
    @GetMapping(value = "/getById/{addressId}")
    public R<UserAddress> getById(@PathVariable("addressId") Long addressId) {

        return R.ok(userAddressService.getById(addressId));
    }

}
