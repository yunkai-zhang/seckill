package com.zhangyun.zseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangyun.zseckill.pojo.Goods;
import com.zhangyun.zseckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangyun
 * @since 2022-03-31
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 返回商品列表
     * @param
     * @return java.util.List<com.zhangyun.zseckill.vo.GoodsVo>
     **/
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     * */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
