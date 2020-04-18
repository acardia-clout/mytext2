package com.itheima.dao;

import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map; /**
 * 新增检查组接口
 */
public interface CheckGroupDao {
    /**
     * 新增检查组
     * @param checkGroup
     */
    void add(CheckGroup checkGroup);

    /**
     * 往检查组和检查项中间表插入数据
     * @param map
     */
    void setCheckGroupAndCheckItem(Map<String, Integer> map);

    /**
     * 根据条件查询检查组
     * @param queryString
     * @return
     */
    List<CheckGroup> selectByCondition(String queryString);
    /**
     * 根据检查组id查询检查组对象
     */
    CheckGroup findById(Integer id);

    /**
     *
     * 根据检查组id查询检查项ids
     */
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    /**
     * 更新检查组表
     * @param checkGroup
     */
    void edit(CheckGroup checkGroup);

    /**
     * 删除检查组和检查项关系
     * @param id
     */
    void deleteReShip(Integer id);

    /**
     * 根据检查组id 查询检查组和检查项中间表
     * @param id
     * @return
     */
    int findCheckGroupAndCheckItemByGroupId(Integer id);

    /**
     * 根据检查组id查询套餐和检查组中间表
     * @param id
     * @return
     */
    int findCheckGroupAndSetmealByGroupId(Integer id);

    /**
     * 删除检查组
     * @param id
     */
    void deleteByGroupId(Integer id);

    /**
     * 查询所有检查组
     */
    List<CheckGroup> findAll();
}
