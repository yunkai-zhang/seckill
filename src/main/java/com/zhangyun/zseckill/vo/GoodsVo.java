package com.zhangyun.zseckill.vo;

import com.zhangyun.zseckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
//继承goods后，得到goods的所有属性；只需要在GoodVo中添加goods中没有但是goodsvo需要的属性
public class GoodsVo extends Goods {
    /**
     * 秒杀价格
     **/
    private BigDecimal seckillPrice;

    /**
     * 剩余数量
     **/
    private Integer stockCount;

    /**
     * 开始时间
     **/
    private Date startDate;

    /**
     * 结束时间
     **/
    private Date endDate;
}
