package org.macross.AppleStore_Seckill_Service_Proj.rabbitmq;

public class SeckillMessage {

    private Integer userId;

    private Integer commodityId;

    private String outTradeNo;

    public SeckillMessage(){

    }
    public SeckillMessage(Integer userId,Integer commodityId ,String outTradeNo){
        this.userId = userId;
        this.commodityId = commodityId;
        this.outTradeNo = outTradeNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String toString() {
        return "SeckillMessage{" +
                "userId=" + userId +
                ", commodityId=" + commodityId +
                ", outTradeNo='" + outTradeNo + '\'' +
                '}';
    }
}
