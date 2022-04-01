package com.zhangyun.zseckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.zseckill.pojo.Goods;
import com.zhangyun.zseckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-31
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 返回商品列表
     *
     * @param
     * @return java.util.List<com.zhangyun.zseckill.vo.GoodsVo>
     * @operation add
     **/
    List<GoodsVo> findGoodsVo();

    /*
    * 获取商品详情
    * */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
