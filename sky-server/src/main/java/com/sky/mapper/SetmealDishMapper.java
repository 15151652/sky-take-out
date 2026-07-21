package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    //根据套餐id查询套餐下的菜品
    List<Long> getDishIdsBySetmealId(List<Long> dishIds);
}
