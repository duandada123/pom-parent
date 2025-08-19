package com.sinogale.security.base;

import com.sinogale.common.constants.SZPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName PasswordLoginRequest
 * @Author duanchao
 * @Date 2021/7/21 14:36
 **/

@AllArgsConstructor
@Data
public class PasswordLoginRequest {
    private String username;
    private String password;
    private boolean forceLogin;
    private SZPlatform platform;

    public PasswordLoginRequest() {
        this.platform = SZPlatform.OMSSYSTEM;
    }

}
