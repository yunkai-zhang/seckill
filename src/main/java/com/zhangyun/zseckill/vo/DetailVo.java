package com.zhangyun.zseckill.vo;

import com.zhangyun.zseckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品详情返回对象
 * @ClassName: DetailVo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {


    private User User;

    private GoodsVo goodsVo;

    private int secKillStatus;

    private int remainSeconds;

}
