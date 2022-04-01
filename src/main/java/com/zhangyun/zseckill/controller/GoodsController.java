package com.zhangyun.zseckill.controller;

import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IGoodsService;
import com.zhangyun.zseckill.service.IUserService;
import com.zhangyun.zseckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

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


    /*
    * 跳往商品详情页
    * */
    @RequestMapping("/toDetail/{goodsId}")
    //使用@PathVariable指定url路径中参数作为本形参的输入。
    public String toDetail(Model model, User user,@PathVariable Long goodsId){
        //把用户信息传入到前端
        model.addAttribute("user",user);
        //获取到goods中定义的秒杀时间段信息，并和当前时间对比来判断
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            //秒杀还未开始；seckillStatus是初始值0
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //秒杀已经结束
            secKillStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        //把商品信息传入前端
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus", secKillStatus);

        //由controller指定跳往的前端页面，跳转的时候model携带了要给前端的参数
        return "goodsDetail";
    }
}
