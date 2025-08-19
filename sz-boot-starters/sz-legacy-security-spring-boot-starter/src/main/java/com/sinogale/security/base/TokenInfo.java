package com.sinogale.security.base;

import com.sinogale.common.constants.SZPlatform;
import com.sinogale.common.constants.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TokenInfo
 * @Author duanchao
 * @Date 2021/7/21 12:11
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenInfo {

    private Long userId;

    private UserType userType;

    private String username;

    private SZPlatform platform;


}
