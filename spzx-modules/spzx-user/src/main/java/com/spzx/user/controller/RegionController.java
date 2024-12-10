package com.spzx.user.controller;

import java.util.List;
import java.util.Arrays;

import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.domain.Region;
import com.spzx.user.service.IRegionService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 地区信息Controller
 *
 * @author atguigu
 * @date 2024-12-05
 */
@Tag(name = "地区信息接口管理")
@RestController
@RequestMapping("/region")
public class RegionController extends BaseController {
    @Resource
    private IRegionService regionService;

    /**
     * 查询地区信息列表
     */
    @Operation(summary = "查询地区信息列表")
    @GetMapping("/treeSelect/{code}")
    @RequiresLogin
    public List<Region> treeSelect(@PathVariable("code") String code) {
        List<Region> regions = regionService.treeSelect(code);

        return regions;
    }

    @GetMapping("/Select/{code}")
    @Operation(summary = "获取名称")
    @InnerAuth
    public String Select(@PathVariable("code") String code) {


        return regionService.getNameByCode(code);


    }
}
