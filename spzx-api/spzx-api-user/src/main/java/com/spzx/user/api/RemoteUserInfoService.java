package com.spzx.user.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.user.domain.UserAddress;
import com.spzx.user.domain.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "remoteUserInfoService",
value = "spzx-user",fallbackFactory = RemoteUserInfoFallbackFactory.class)
public interface RemoteUserInfoService {

    @PostMapping("/userInfo/register")
    public R register(@RequestBody UserInfo userInfo,@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/userInfo/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
    @GetMapping("/userAddress/getById/{addressId}")
    public R<UserAddress> getById(@PathVariable("addressId") Long addressId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
    @GetMapping("/Select/{code}")
    public String Select(@PathVariable("code") String code, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
