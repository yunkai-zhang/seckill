package com.zhangyun.zseckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.zseckill.exception.GlobalException;
import com.zhangyun.zseckill.mapper.UserMapper;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IUserService;
import com.zhangyun.zseckill.utils.CookieUtil;
import com.zhangyun.zseckill.utils.MD5Util;
import com.zhangyun.zseckill.utils.UUIDUtil;
import com.zhangyun.zseckill.vo.LoginVo;
import com.zhangyun.zseckill.vo.RespBean;
import com.zhangyun.zseckill.vo.RespBeanEnum;
//import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    //注入userMapper用于数据库操作
    /*
    * idea中userMapper爆红并提示“Could not autowire. No beans of 'UserMapper' type found”的话，没事，
    * 因为项目的启动类加了注解@MapperScan("com.zhangyun.zseckill.mapper")，会扫描到mapper作为bean的
    * 这个红色波浪线可以忽略
    * */
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /*
    * 登录
    * */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password=loginVo.getPassword();

        /*
        * 虽然前端已经做了初步校验，但是后端还要再做一遍，因为后端是最后防线;
        * */
        // 判断用户名密码是否为空；老师用的spring框架的，我也跟着吧
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        //判断手机号合法性
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        /*
        * 合法性都没问题的话，就进行数据库查询了
        * */
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if(null==user){
            //之前返回错误的对象
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            //现在throw自定义的GlobalException
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确：即对收到的前端一次加密的密码，在本服务端再次加密，查看二次加密后的结果是否与数据库存储的二次加密效果相同
        if (!MD5Util.fromPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            //之前返回错误的对象
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            //现在throw自定义的GlobalException
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String userTicket = UUIDUtil.uuid();
        //将用户信息存入redis；opsForValue专门用于操作String类型
        redisTemplate.opsForValue().set("user:"+userTicket,user);

        //将用户信息+用户cookie存到session中，就得用到request和response（存cookie用到response）
        //request.getSession().setAttribute(userTicket,user);之前把用户信息放在session中，现在我们把用户信息放到redis中：
        /*
        * 通过CookieUtil工具类，把cookievalue为uuid且cookiename为userTicket的cookie存入resp中（cookie中其他未设置的属性使用CookieUtil中的默认值）
        *
        * 使用redis存储用户信息时，cookie用法不变
        * */
        CookieUtil.setCookie(request, response, "userTicket", userTicket);
        //登录校验成功+在session中设置用户和cookie完成，于是可以给前端发送成功指令
        return RespBean.success();

    }

    /**
     * 根据tickiet（即cookievalue）从远程redis拿到用户的数据
     * */
    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        /*
        * 做一个优化，如果用户不为空，我把cookie重新设置一下，这主要是以防万一的考虑。
        * */
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }
}
