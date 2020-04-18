package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/**
 * 套餐接口服务
 */
public interface SetmealService {
    /**
     * 新增套餐数据
     */
    void add(Setmeal setmeal, Integer[] checkgroupIds);

    /**
     * 套餐分页查询
     */
    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);
    /**
     * 删除套餐数据
     */
    void deleteById(Integer id);

    /**
     * 根据套餐id查询套餐对象
     */
    Setmeal findById(Integer id);

    /**
     * 根据套餐id查询所有检查组ids
     */
    List<Integer> findCheckGroupIdsBySetmealId(Integer id);

    /**
     * 编辑套餐数据
     */
    void edit(Setmeal setmeal, Integer[] checkgroupIds);

    /**
     * 查询所有套餐列表
     */
    List<Setmeal> getSetmeal();

    /**
     * 获取套餐名称和套餐对应的预约数量
     * @return
     */
    List<Map<String,Object>> getSetmealCount();
}
