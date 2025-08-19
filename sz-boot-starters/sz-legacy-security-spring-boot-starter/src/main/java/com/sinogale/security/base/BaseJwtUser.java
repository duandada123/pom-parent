package com.sinogale.security.base;

import com.sinogale.common.constants.UserType;
import com.sinogale.common.constants.SZPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName BaseJwtUser
 * @Author duanchao
 * @Date 2021/7/21 13:32
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseJwtUser implements SessionInfo {

    private Long userId;

    private UserType userType;

    private String username;

    private SZPlatform platform;

    private Collection<? extends GrantedAuthority> authorities;

}
