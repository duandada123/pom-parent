package com.sinogale.security.util;

import com.sinogale.security.base.BaseJwtUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * @ClassName SpringSecurityUtils
 * @Author duanchao
 * @Date 2021/7/21 14:42
 **/

public class SpringSecurityUtils {
    public SpringSecurityUtils(){
        
    }
    
    public static String getCurrentUserName(){
        String temp = "";

        try {
            BaseJwtUser jwtUser = getJwtUser();
            if (Objects.nonNull(jwtUser)) {
                return jwtUser.getUsername();
            }

            temp = "-";
        } catch (Exception var2) {
            temp = "";
        }

        return temp;
    }

    private static BaseJwtUser getJwtUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getClass().isAssignableFrom(AnonymousAuthenticationToken.class)) {
            return null;
        } else {
            BaseJwtUser info = (BaseJwtUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return info;
        }
    }
}
