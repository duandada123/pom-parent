package com.sinogale.security.base.platform;

import com.sinogale.security.base.BaseJwtUser;

/**
 * @ClassName UserContextAware
 * @Author duanchao
 * @Date 2021/7/21 13:31
 **/

public interface UserContextAware {
    BaseJwtUser processToken(String paramString);
}
