package com.zhangyun.zseckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhangyun.zseckill.pojo.Order;
import com.zhangyun.zseckill.pojo.SeckillOrder;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IGoodsService;
import com.zhangyun.zseckill.service.impl.OrderServiceImpl;
import com.zhangyun.zseckill.service.impl.SeckillOrderServiceImpl;
import com.zhangyun.zseckill.vo.GoodsVo;
import com.zhangyun.zseckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seckill")
public class SecKillController {
    //service层为controller层提供查询商品数据的服务
    @Autowired
    private IGoodsService goodsService;
    //service层为controller层提供查询秒杀订单数据的服务
    @Autowired
    private SeckillOrderServiceImpl seckillOrderService;
    //
    @Autowired
    private OrderServiceImpl orderService;

    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        //System.out.println("!!!!!!!进入秒杀了");
        //如果用户不存在，跳往登录页面
        if (user == null) {
            //System.out.println("!!!!!!!传入user为空");
            return "login";
        }
        //如果用户存在，传给前端，让前端能知道前端被展示的时候是否是用户已登录的状态
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存。用户可以自己修改前端，所以我们只能根据id自己去表里查真实的内存，而不能依赖前端返回的库存数据
        if (goods.getStockCount() < 1) {
            //前端收到后端传递来的“错误信息”，会做前端自己的处理。
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            System.out.println("SecKillController-doSeckill 库存不足，跳往:seckillFail");
            return "seckillFail";
        }
        /*
        * 判断订单是否重复抢购：抓住userid和goodsid
        *
        * QueryWrapper是mybatisplus的包装类
        * */
        SeckillOrder seckillOrder = seckillOrderService.getOne(new
                QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq(
                "goods_id",
                goodsId));
        if (seckillOrder != null) {//订单表中显示同一人抢了同一款商品（如iphone12），应拒绝本次秒杀请求
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        //如果库存够，且没有出现黄牛行为，则允许秒杀
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);

        //秒杀成功后去前端的订单页
        return "orderDetail";
    }

}
