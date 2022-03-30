package com.zhangyun.zseckill.config;

import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IUserService;
import com.zhangyun.zseckill.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义用户参数
 *
 * @author: 张云
 * @ClassName: UserArgumentResolver
 */
//不要忘记Component注解，因为本类要被WebConfig中的addArgumentResolvers使用
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    //注入userService，从而借助它可以拿到用户信息
    @Autowired
    private IUserService iUserService;

    /*
    * 本函数相当于是做一层条件的判断（可以看到返回类型是布尔类型），只有符合supportsParameter方法的条件（此方法返回false）之后，
    * 才会执行下面的resolveArgument方法。所以我们在supportsParameter中做一层条件的判断。
    * */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //获取参数parameter的类型
        Class<?> parameterType = parameter.getParameterType();
        //看参数parameter的类型是不是User。如果返回true，说明本方法的入参是User，进而才会走到下面的resolveArgument方法
        return parameterType == User.class;
    }

    /*
    * 本方法主要做原先controller中判断用户是否已登录的那些操作，比如 if(StringUtils.isEmpty(userTicket))和 if(user==null)
    * */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //从webRequest中获取request和response
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        //getCookieValue需要request，但是resolveArgument的参数里没有直接的request；不过可以从webRequest中获取request和response。这一步从request中拿到了cookievalue
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");

        //ticket（即cookievalue）为空则直接返回null
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        //如果ticket不为空，则可以根据ticket从redis中获取用户的数据（即返回一个user对象）
        return iUserService.getUserByCookie(userTicket, request, response);
    }

}
