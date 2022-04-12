package com.zhangyun.zseckill.controller;

import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IGoodsService;
import com.zhangyun.zseckill.service.IUserService;
import com.zhangyun.zseckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUserService iUserService;
    //这里一定要注入bean，否则使用bean时会报空指针异常！
    //注入提供查询商品服务的bean
    @Autowired
    private IGoodsService goodsService;
    //用redis做页面缓存，所以要引入redis
    @Autowired
    private RedisTemplate redisTemplate;
    //用于手动渲染。不详细展开了
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转到商品列表页
     *
     * produce表示这个页面是缓存起来的；且最好在produce中设置一下编码格式，省的返回的时候编码格式有问题导致乱码。
     * */
    @RequestMapping(value="/toList",produces = "text/html;charset=utf-8")
    //使用@ResponseBody后,return的值就不会被解析成静态页面的文件名，而是会return一个对象。
    @ResponseBody
    public String toList(Model model, User user,HttpServletRequest request,HttpServletResponse response){
        //通过valueOperations去处理
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //key可以自己定义。可以直接把拿到的强转成Stirng类型返回，因为我们自己知道我们存的就是String类型，因为存的html页面显然是String类型
        String html = (String) valueOperations.get("goodsList");
        //对从redis中拿到的html做判断
        if(!StringUtils.isEmpty(html)){//如果不为空直接返回html
            return html;
        }

        //把用户信息传入到前端
        model.addAttribute("user",user);
        //把商品信息传入前端
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        /*
         * 如果redis之前没有存储目标页面，就要手动渲染，并且存入redis，并且返回。thymeleaf有对应的模板引擎叫做thymeleafViewResolver 可以用于手动渲染
         * process()用于渲染,参数为(模板名称,webcontext)
         * 构建webcontext也需要入参，req和resp的问题不大，可以直接从前端获取
         * webcontext也需要传入到底网页的内容是什么，所以把model传入，因为model中有user和goodsList的信息，可以用于构建html；所以webContext要在model.addAttribute后处理
         * */
        WebContext webContext=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goodsList",webContext);
        //对渲染结果做判断,渲染成功了则存到redis中，并返回。要设置kv的存在时间，避免让用户看到很久之前的页面，推荐设置为1min
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,1, TimeUnit.MINUTES);//goodsList是自定义的redis key，对应的value是html。
        }
        return html;
    }
    /**
     * 跳转到商品列表页
     * */
    @RequestMapping("/toList2")
    //session获取用户信息和cookie；model做页面跳转时，把商品信息传给前端；传入userTicket即为cookieValue,可以通过@CookieValue拿到，@CookieValue括号中指定的是cookieName
    //public String toList(HttpSession session, Model model,@CookieValue("userTicket") String userTicket){
    //
    //使用WebMvcConfigurer实现拦截器后，我们的tolist函数不需要这么多参数了
    //public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String userTicket){
    //使用WebMvcConfigurer实现拦截器后，不需要req resp等参数，直接从UserArgumentResolver.resolveArgument接收user即可
    public String toList2(Model model, User user){
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
    @RequestMapping(value="/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    //使用@PathVariable指定url路径中参数作为本形参的输入。
    public String toDetail(Model model, User user,@PathVariable Long goodsId,HttpServletRequest request,HttpServletResponse response){
        //引入redis操作
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsDetails:"+goodsId);
        if(!StringUtils.isEmpty(html)){//如果不为空直接返回html
            return html;
        }

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

        //如果html为空，在modeladdAttributes后手动渲染
        WebContext webContext=new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goodsDetail",webContext);
        //对渲染结果做判断,渲染成功了则存到redis中，并返回。要设置kv的存在时间，避免让用户看到很久之前的页面，推荐设置为1min
        if(!StringUtils.isEmpty(html)){
            //set的时候记得给key加上goodsid，这样才能区分url
            valueOperations.set("goodsDetail:"+goodsId,html,1, TimeUnit.MINUTES);//goodsDetail是自定义的redis key，对应的value是html。
        }
        return html;
    }
    /*
    * 跳往商品详情页
    * */
    @RequestMapping("/toDetail2/{goodsId}")
    //使用@PathVariable指定url路径中参数作为本形参的输入。
    public String toDetail2(Model model, User user,@PathVariable Long goodsId){
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
