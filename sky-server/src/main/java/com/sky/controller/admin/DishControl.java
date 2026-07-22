package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//菜品管理
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishControl {

    @Autowired
    private DishService dishService;

    @PostMapping("/save")
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除菜品{}", ids);
        dishService.delete(ids);
        return Result.success("删除菜品成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("根据菜品id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据菜品id查询菜品{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("更新菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success("更新菜品成功");
    }
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategory(Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list = dishService.getByCategoryID(categoryId);
        return Result.success(list);
    }
    //菜品停售/admin/dish/status/{status}
    @PostMapping("/status/{status}")
    @ApiOperation("菜品停售")
    public Result startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("菜品停售：{},{}", status, id);
        dishService.startOrStop(status, id);
        return Result.success();
    }
}

