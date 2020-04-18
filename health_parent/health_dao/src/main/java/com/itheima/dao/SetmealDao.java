package com.itheima.dao;

import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;
/**
 * 套餐dao接口
 */
public interface SetmealDao {

    /**
     * 新增套餐表
     * @param setmeal
     */
    void add(Setmeal setmeal);

    /**
     * 往检查组和套餐中间表插入数据
     * @param map
     */
    void setCheckGroupAndSetmeal(Map<String, Integer> map);

    /**
     * 套餐分页查询
     */
    List<Setmeal> selectByCondition(String queryString);

    /**
     * 根据套餐id查询 套餐检查组中间表
     * @param id
     * @return
     */
    int findSetmealAndCheckGroupBySetmealId(Integer id);

    /**
     * 直接删除套餐数据
     * @param id
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
     * 根据套餐id删除套餐和检查组关系
     * @param id
     */
    void deleteReShip(Integer id);

    /**
     * 更新套餐数据
     * @param setmeal
     */
    void edit(Setmeal setmeal);
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
