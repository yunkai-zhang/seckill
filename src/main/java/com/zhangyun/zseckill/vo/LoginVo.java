package com.zhangyun.zseckill.vo;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class LoginVo {

    @NotNull
//    @IsMobile
    private String mobile;

    @NotNull
//    @Length(min = 32)
    private String password;
}
