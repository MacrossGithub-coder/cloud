package org.macross.AppleStore_Seckill_Service_Proj.enums;

/**
 *-1 ：商品库存不足
 *-4 ：排队中，继续轮询
 *-2 : 用户余额不足
 *-3 : 秒杀过程中出现异常，从Redis缓存中取出相关信息，页面返回活动火爆
 * 0 : 秒杀成功，返回订单ID
 * @Author: jiancheng.zhang
 * @Date: 2021/2/25 11:50
 */
public enum SeckillResultEnum {


    OUT_OF_STOCK(-1,"商品库存不足"),

    INSUFFICIENT_BALANCE(-2,"用户余额不足"),

    EXCEPTION(-3,"当前秒杀活动过于火爆，请稍后重试"),

    IN_QUEUE(-4,"订单尚未处理完"),

    SECKILL_SUCCESS(0,"秒杀成功，返回订单ID");

    private Integer code;
    private String msg;

    SeckillResultEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static SeckillResultEnum getSeckillResultEnum(Integer code){
        for (SeckillResultEnum e :SeckillResultEnum.values()){
            if (e.getCode().equals(code)){
                return e;
            }
        }
        return null;
    }
}
