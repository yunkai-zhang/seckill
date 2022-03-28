package com.zhangyun.zseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.vo.LoginVo;
import com.zhangyun.zseckill.vo.RespBean;

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

    RespBean doLogin(LoginVo loginVo);
}
