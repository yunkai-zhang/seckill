package com.zhangyun.zseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.zseckill.pojo.Order;
import com.zhangyun.zseckill.pojo.User;
import com.zhangyun.zseckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-31
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * */
    Order seckill(User user, GoodsVo goods);
}
