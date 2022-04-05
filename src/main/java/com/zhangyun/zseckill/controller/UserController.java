package com.zhangyun.zseckill.controller;


import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-26
 */
@Controller
@RequestMapping("/user")
public class UserController {

    //查看当前登录的用户的信息
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info (User user){
        return RespBean.success(user);
    }

}
