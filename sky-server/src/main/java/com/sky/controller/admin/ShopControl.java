package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopControl")
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理")
@Slf4j
public class ShopControl {

    public static final String KEY  ="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @PutMapping("{status}")
    @ApiOperation("设置店铺状态为")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态为：{}",status==1?"正常":"停用");
        //设置店铺状态,redis
        redisTemplate.opsForValue().set("KEY", status);
        return Result.success();
}

//查询店铺状态
    @GetMapping("/status")
    @ApiOperation("查询店铺状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get("KEY");
        return Result.success(status);
    }
}
