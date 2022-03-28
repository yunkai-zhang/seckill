package com.zhangyun.zseckill.vo;

import com.zhangyun.zseckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    /*
    * @IsMobile是自定义注解
    *
    * 这个注解实现的功能和ValidatorUtil.java的功能一样，都是校验手机号的合法性；不过用注解就省的自己写工具类了；
    * */
    @IsMobile
    @NotNull
    private String mobile;
    @NotNull//password不能为空
    @Length(min=32)//password长度不得少于32
    private String password;
}
