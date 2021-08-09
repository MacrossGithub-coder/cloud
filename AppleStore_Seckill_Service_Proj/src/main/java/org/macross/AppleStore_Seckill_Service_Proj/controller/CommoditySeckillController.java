package org.macross.AppleStore_Seckill_Service_Proj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.macross.AppleStore_Common_Config.model.request.CommonRequest;
import org.macross.AppleStore_Common_Config.utils.JsonData;
import org.macross.AppleStore_Seckill_Service_Proj.annotation.DistributeLimitAnno;
import org.macross.AppleStore_Seckill_Service_Proj.enums.SeckillResultEnum;
import org.macross.AppleStore_Seckill_Service_Proj.lua.DistributedLock;
import org.macross.AppleStore_Seckill_Service_Proj.service.CommoditySeckillService;
import org.macross.AppleStore_Seckill_Service_Proj.service.SeckillPathService;
import org.macross.AppleStore_Seckill_Service_Proj.service.SeckillResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/pri/seckill")
@Slf4j
public class CommoditySeckillController {

    @Autowired
    DistributedLock distributedLock;

    @Autowired
    CommoditySeckillService commoditySeckillService;

    @Autowired
    SeckillResultService seckillResultService;

    @Autowired
    SeckillPathService seckillPathService;

    private static String LUA_DISTRIBUTE_LOCK_PRE = "DistributeLock-";

    /**
     * 获取秒杀Path，用于后续Seckill进行验证，接口做限流处理
     *
     * @param commonRequest
     * @param request
     * @return
     */
    @RequestMapping(value = "get_path", method = RequestMethod.POST)
    @DistributeLimitAnno(limitKey = "SeckillPath-limit", limit = 10000)
    public JsonData getSeckillPath(@RequestBody CommonRequest commonRequest, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        log.info("Receive Request get_path, param:[userId = {},commodityId = {}]", userId, commonRequest.getCommodity_id());
        //验证验证码
        //TODO
        String path = seckillPathService.createSeckillPath(commonRequest.getCommodity_id(), userId);
        return path != null ? JsonData.buildSuccess(path) : JsonData.buildError("获取Path失败");
    }


    /**
     * -3 : 秒杀Path有误
     * -1 ：库存不足秒杀失败
     * -2 : 该用户存在重复下单行为
     *  0 ：排队中，继续轮询
     * 接口做限流处理
     */
    @RequestMapping(value = "/{path}/commodity_seckill", method = RequestMethod.POST)
    @DistributeLimitAnno(limitKey = "Seckill-limit", limit = 10000)
    public JsonData commoditySeckill(@RequestBody CommonRequest commonRequest, HttpServletRequest request, @PathVariable("path") String path) throws JsonProcessingException {
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        log.info("Receive Request commoditySeckill, param:[userId = {},commodityId = {}]", userId, commonRequest.getCommodity_id());
        boolean lock;
        lock = distributedLock.distributedLock(LUA_DISTRIBUTE_LOCK_PRE + "SeckillService-" + userId.toString() + "-"
                        + commonRequest.getCommodity_id(),
                        UUID.randomUUID().toString(),
                        "5");
        if (!lock) {
            return JsonData.buildError(-1, "5s内请勿重复请求该接口");
        }
        //Redis:{key:"path:userId:commodityId,Value:str}
        String key = "path:" + userId + ":" + commonRequest.getCommodity_id();
        boolean valid = seckillPathService.confirmPathValid(key, path);
        if (!valid) return JsonData.buildError(-3, "秒杀Path有误！");
        return commoditySeckillService.doCommoditySeckill(commonRequest.getCommodity_id(), userId);
    }


    /**
     * -1 ：商品库存不足
     * -4 ：排队中，继续轮询
     * -2 : 用户余额不足
     * -3 : 秒杀过程中出现异常，从Redis缓存中取出相关信息，页面返回活动火爆
     * 0 :秒杀成功，返回订单ID
     * @param commonRequest
     * @param request
     * @return
     */
    @RequestMapping(value = "get_seckill_result", method = RequestMethod.POST)
    public JsonData getSeckillResult(@RequestBody CommonRequest commonRequest, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("user_id"));
        log.info("Receive Request getSeckillResult param:[userId = {},commodityId = {},outTradeNo = {}]", userId, commonRequest.getCommodity_id(), commonRequest.getOut_trade_no());
        int result = seckillResultService.getSeckillResult(commonRequest.getCommodity_id(), userId, commonRequest.getOut_trade_no());
        //异常情况
        if (result <= 0) {
            SeckillResultEnum seckillResultEnum = SeckillResultEnum.getSeckillResultEnum(result);
            return seckillResultEnum!=null? JsonData.buildError(seckillResultEnum.getCode(),seckillResultEnum.getMsg())
                    :JsonData.buildError("系统未知异常");
        }
        return JsonData.buildSuccess(0,"商品秒杀成功，订单号为：" + result);
    }

    @RequestMapping(value = "reset_database",method = RequestMethod.GET)
    public JsonData resetDatabase() throws Exception {
        boolean result = seckillResultService.resetDatabase();
        return result ? JsonData.buildSuccess("Success"):JsonData.buildError("Error");
    }
}
