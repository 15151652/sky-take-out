package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);


        log.info("新增菜品{}", dishDTO);
        //向菜品表插入1条数据

        dishMapper.insert(dish);
        //获取菜品id
        Long dishId = dish.getId();
        //向菜品口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
       if (flavors != null && !flavors.isEmpty()) {
            //设置菜品id
            flavors.forEach(df -> df.setDishId(dishId));

        dishFlavorMapper.insertBatch(flavors);
       }


    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

@Transactional
    public void delete(List<Long> ids) {
        //判断菜品是否能够删除--是否有订单引用
        for (Long id : ids) {
            //获取菜单id
            Dish dish = dishMapper.getById(id);
            //判断菜品是否被订单引用
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }



        //判断菜品是否能够删除--是否套餐关联
        List<Long> setmealIds = setmealDishMapper.getDishIdsBySetmealId(ids);
        if (setmealIds!=null&&setmealIds.size()>0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

//        //删除菜品数据
        dishMapper.deleteBatchIds(ids);

        //删除菜品口味数据
        dishFlavorMapper.deleteBatchByDishIds(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据菜品id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据菜品id查询菜品口味数据
        List<DishFlavor> flavors =  dishFlavorMapper.selectByDishId(id);


 DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);

        return dishVO;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        //根据菜品id更新菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        dishFlavorMapper.deleteBatch(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(df -> df.setDishId(dishDTO.getId()));

            dishFlavorMapper.insertBatch(flavors);
        }

    }
    /**
     * 根据分类id查询菜品信息
     *
     * @param categoryId
     */

    public List<Dish> getByCategoryID(Long categoryId) {
        return dishMapper.getByCategoryID(categoryId);
    }
    //菜品停售/admin/dish/status/{status}
    @Override
    @Transactional
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        dishMapper.updateById(dish);
    }
}
