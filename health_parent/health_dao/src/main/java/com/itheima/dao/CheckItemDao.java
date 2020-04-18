package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 检查项接口
 */
public interface CheckItemDao {
    /**
     * 查询所有检查项（不分页）
     * @return
     */
    List<CheckItem> findAll();

    /**
     * 新增检查项
     */
    void add(CheckItem checkItem);

    /**
     * 检查项分页查询
     * @param queryString
     * @return
     */
    Page<CheckItem> selectByCondition(String queryString);

    /**
     * 查询检查项和检查组是否存在关系
     * @param id
     * @return
     */
    int findCountByCheckItemId(Integer id);

    /**
     * 如果没有关系可以删除
     * @param id
     */
    void delete(Integer id);

    /**
     * 根据检查项id查询检查项对象
     * @param id
     * @return
     */
    CheckItem findById(Integer id);

    /**
     * 编辑检查项
     */
    void edit(CheckItem checkItem);
}
