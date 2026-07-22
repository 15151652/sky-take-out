package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.SetmealDishMapper;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDTO
     * @return
     */
    @Transactional // 事务
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal); //套餐
        List<SetmealDish> setmealDishlist = setmealDTO.getSetmealDishes(); // 套餐菜品关系

        setmealMapper.insert(setmeal);

        long setmealId = setmeal.getId(); //返回自动生成的套餐菜品表主键id

        if (setmealDishlist != null && setmealDishlist.size() > 0) {
            setmealDishlist.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //向套餐菜品关系表插入n条数据
            setmealDishMapper.insertBatch(setmealDishlist);
        }
    }
    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        // select * from setmeal limit 0,10
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();

        return new PageResult(total, records);
    }
    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.update(setmeal);
    }
    @Override
    @Transactional
    public void deleteBtach(List<Long> ids) {
        //判断当前套餐是否在启售中，启售中不能删除
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //删除套餐表里的套餐
        setmealMapper.deleteByIds(ids);

        //删除套餐菜品表里的菜品
        setmealDishMapper.deleteByDishIds(ids);

    }
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //修改套餐
        setmealMapper.update(setmeal);

        List<Long> ids = new ArrayList<>(); //存放套餐id的集合，这样可以不用新定义一个根据单个id查询的方法
        ids.add(setmealDTO.getId());

        //删除原有的菜品
        setmealDishMapper.deleteByDishIds(ids);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        //插入套餐对应的菜品

        if (setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealDTO.getId());
            });
        }

        //批量插入菜品
        setmealDishMapper.insertBatch(setmealDishes);
    }
}