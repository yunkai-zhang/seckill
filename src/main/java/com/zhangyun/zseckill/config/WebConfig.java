package com.zhangyun.zseckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * MVC配置类
 * */
@Configuration
//@EnableWebMvc表示让MVC全面接管，仅使用个别MVC功能，还是注释掉@EnableWebMvc吧
//@EnableWebMvc
//因为本类是MVC的配置类，所以本类要实现WebMvcConfigurer接口
public class WebConfig implements WebMvcConfigurer {
    //注入编写好的UserArgumentResolver类的bean对象，供给resolvers
    @Autowired
    private UserArgumentResolver userArgumentResolver;

    /*
    * WebMvcConfigurer接口中有很多方法，我们现在要做的是自定义参数；想自定义参数就得用addArgumentResolvers，咱重写该方法
    *
    * HandlerMethodArgumentResolver是我们用到的自定义参数的解析器
    * */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //resolvers是一个存储HandlerMethodArgumentResolver的数组，所以我们要往resolvers中add实现了HandlerMethodArgumentResolver接口的对象
        resolvers.add(userArgumentResolver);
    }
}
