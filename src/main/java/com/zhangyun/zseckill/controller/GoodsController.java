package com.zhangyun.zseckill.controller;

import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IGoodsService;
import com.zhangyun.zseckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService iUserService;
    //这里一定要注入bean，否则使用bean时会报空指针异常！
    @Autowired
    //注入提供查询商品服务的bean
    private IGoodsService goodsService;

    /**
     * 跳转到商品列表页
     * */
    @RequestMapping("/toList")
    //session获取用户信息和cookie；model做页面跳转时，把商品信息传给前端；传入userTicket即为cookieValue,可以通过@CookieValue拿到，@CookieValue括号中指定的是cookieName
    //public String toList(HttpSession session, Model model,@CookieValue("userTicket") String userTicket){
    //
    //使用WebMvcConfigurer实现拦截器后，我们的tolist函数不需要这么多参数了
    //public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String userTicket){
    //使用WebMvcConfigurer实现拦截器后，不需要req resp等参数，直接从UserArgumentResolver.resolveArgument接收user即可
    public String toList(Model model, User user){
        /*
        * 刚刚登录的时候，把相应的用户信息存储起来了，这里就可以获取用户信息
        * */
        //如果ticket为空就登录（防止用户直接访问toList尝试来到商品页）
//        if(StringUtils.isEmpty(userTicket)){
//            return "login";
//        }
        /*
        * session中通过kv存储了userTicket（即cookieValue）和User，这里通过userTicket拿到user；
        * 我推测：getAttribute拿到的object本身就是一个User，所以才能强转为User
        *
        * session中没有用户的值，即用户未登录，跳往登录页面
        * */
        //User user = (User)session.getAttribute(userTicket);SpringSession实现分布式Session的时候根据票据从session中获取用户信息；
        // 现在我们要通过redis获取用户信息
//        User user = iUserService.getUserByCookie(userTicket, request, response);
//
//        if(user==null){
//            return "login";
//        }

        //把用户信息传入到前端
        model.addAttribute("user",user);
        //把商品信息传入前端
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }
}
