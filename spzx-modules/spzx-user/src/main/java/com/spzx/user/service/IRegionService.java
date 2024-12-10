package com.spzx.user.service;

import com.spzx.user.domain.Region;

import java.util.List;

public interface IRegionService {
    String getNameByCode(String code);

   List<Region> treeSelect(String code);
}
