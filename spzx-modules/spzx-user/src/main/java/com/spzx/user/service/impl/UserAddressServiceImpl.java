package com.spzx.user.service.impl;

import java.util.List;
import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.user.service.IRegionService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spzx.user.mapper.UserAddressMapper;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;

/**
 * 用户地址Service业务层处理
 *
 * @author atguigu
 * @date 2024-12-05
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService
{
    @Resource
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private IRegionService regionService;

    /**
     * 查询用户地址列表
     *
     * @return 用户地址
     */
    @Override
    public List<UserAddress> selectUserAddressList()
    {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
    }

    /**
     * 新增用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    @Override
    public int insertUserAddress(UserAddress userAddress)
    {
        userAddress.setUserId(SecurityContextHolder.getUserId());
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddress);
        userAddress.setCreateTime(DateUtils.getNowDate());

        //如果是默认地址，其他地址更新为非默认地址
        if(userAddress.getIsDefault().intValue() == 1) {
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0L);
            userAddressMapper.update(updateUserAddress, new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userAddress.getUserId()));
        }
        return userAddressMapper.insert(userAddress);
    }

    /**
     * 修改用户地址
     *
     * @param userAddress 用户地址
     * @return 结果
     */
    @Override
    public int updateUserAddress(UserAddress userAddress)
    {
        String provinceName = regionService.getNameByCode(userAddress.getProvinceCode());
        String cityName = regionService.getNameByCode(userAddress.getCityCode());
        String districtName = regionService.getNameByCode(userAddress.getDistrictCode());
        String fullAddress = provinceName + cityName + districtName + userAddress.getAddress();
        userAddress.setFullAddress(fullAddress);
        userAddress.setUpdateTime(DateUtils.getNowDate());
        //如果是默认地址，其他地址更新为非默认地址
        if(userAddress.getIsDefault().intValue() == 1) {
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0L);
            userAddressMapper.update(updateUserAddress, new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userAddress.getUserId()));
        }
        return userAddressMapper.updateById(userAddress);
    }

}
