package com.zhangyun.zseckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.zseckill.mapper.OrderMapper;
import com.zhangyun.zseckill.pojo.Order;
import com.zhangyun.zseckill.pojo.SeckillGoods;
import com.zhangyun.zseckill.pojo.SeckillOrder;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.service.IOrderService;
import com.zhangyun.zseckill.service.ISeckillGoodsService;
import com.zhangyun.zseckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-31
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    //注入操作秒杀商品表的服务，用于减少库存
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    //注入操作订单表的mapper bean，用于修改order表
    @Autowired
    private OrderMapper orderMapper;
    //注入操作秒杀订单表的service层服务
    @Autowired
    private SeckillOrderServiceImpl seckillOrderService;


    /**
     * 秒杀
     * */
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        /*
        * 秒杀首先要减少库存，减少的是秒杀商品表中的库存
        *
        * 先获取某id的秒杀商品的bean，修改bean中记录的库存后，把bean传入updateById方法来更新秒杀商品表的库存；这么做的好处是免了自己写mapper.xml也能做crud了。
        * ISeckillGoodsService继承了IService，所以seckillGoodsService才能用getone方法。
        * */
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //更新秒杀商品表中的库存。
        seckillGoodsService.updateById(seckillGoods);
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        /*
        * 除了生成订单之外，还要生成秒杀订单。之所以要先生成订单，是因为秒杀订单中有一个字段“订单id”是和订单做关联的
        * */
        //生成秒杀订单。id字段是自增的不用管，其他的几个字段要填一下。
        SeckillOrder tSeckillOrder = new SeckillOrder();
        tSeckillOrder.setUserId(user.getId());
        tSeckillOrder.setOrderId(order.getId());
        tSeckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(tSeckillOrder);

        //后端的订单相关处理完毕，把订单信息返回给前端展示
        return order;
    }
}
