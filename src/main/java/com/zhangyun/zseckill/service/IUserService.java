package com.zhangyun.zseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.vo.LoginVo;
import com.zhangyun.zseckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能表示：实现登录的service层接口
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-26
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     *
     * @param userTicket
     **/
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);

    /**
     * 参数：
     * userTicket，因为我们先通过userTicket去获取用户，然后才能够更显他的密码
     * */
    RespBean updatePassword(String userTicket,String password,HttpServletRequest request,HttpServletResponse response);
}



























