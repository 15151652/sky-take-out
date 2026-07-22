package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags= "套餐相关接口") //描述类的作用
public class SetmealController {
    @Autowired
    private SetmealService setmealServices;

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}",setmealDTO);
        setmealServices.save(setmealDTO);
        return Result.success();
    }
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询，参数为：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealServices.pageQuery(setmealPageQueryDTO);
        log.info("套餐分页查询，结果为：{}", pageResult);
        return Result.success(pageResult);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("启售、停售套餐")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启售、停售套餐：{},{}",status,id);

        setmealServices.startOrStop(status,id);
        return Result.success();
    }
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("根据id批量删除套餐：{}",ids);

        setmealServices.deleteBtach(ids);

        return Result.success();
    }
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐：{}",setmealDTO);

        setmealServices.update(setmealDTO);

        return Result.success();
    }
}