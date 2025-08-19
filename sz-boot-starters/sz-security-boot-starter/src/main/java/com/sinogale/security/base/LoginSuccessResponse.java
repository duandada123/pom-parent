package com.sinogale.security.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LoginSuccessResponse
 * @Author duanchao
 * @Date 2021/7/21 14:35
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginSuccessResponse {
    private String token;
    private String username;


}
