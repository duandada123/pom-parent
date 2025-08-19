package com.sinogale.security.util;

import com.sinogale.security.base.BaseJwtUser;
import com.sinogale.security.base.TokenInfo;

/**
 * @ClassName AuthHelper
 * @Author duanchao
 * @Date 2021/7/21 13:37
 **/

public class AuthHelper {
    public static String getLoginRedisKey(BaseJwtUser user) {
        StringBuilder builder = new StringBuilder();
        builder.append("token:")
                .append(user.getPlatform().getCode())
                .append(":")
                .append(user.getUserType().getCode())
                .append(":")
                .append(user.getUserId());
        return builder.toString();
    }

    public static String getLoginSucRedisKey(TokenInfo tokenInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("token:")
                .append(tokenInfo.getPlatform().getCode())
                .append(":")
                .append(tokenInfo.getUserType().getCode())
                .append(":")
                .append(tokenInfo.getUserId());
        return builder.toString();
    }
}
