package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.user.domain.Region;
import com.spzx.user.mapper.RegionMapper;
import com.spzx.user.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;


@Service
public class IRegionServiceImpl implements IRegionService {
    @Autowired
    RegionMapper regionMapper;
    @Override
    public String getNameByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        Region region = regionMapper.selectOne(new LambdaQueryWrapper<Region>().eq(Region::getCode,code).select(Region::getName));
        if(null != region) {
            return region.getName();
        }
        return "";
    }

    @Override
    public List<Region> treeSelect(String code) {


        List<Region> regions = regionMapper.selectList(new LambdaQueryWrapper<Region>().eq(Region::getParentCode, code));
//        Region region = regionMapper.selectOne(new LambdaQueryWrapper<Region>().eq(Region::getParentCode, code));
//        String name = region.getName();

        return regions;
    }

}
