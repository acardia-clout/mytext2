package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;

import java.util.List;

/**
 * 检查组服务接口
 */
public interface CheckGroupService {
    /**
     * 新增检查组
     * @param checkGroup
     * @param checkItemIds
     */
    void add(CheckGroup checkGroup, Integer[] checkItemIds);

    /**
     * 检查组分页查询
     */
    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

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
     * 检查组编辑
     */
    void edit(CheckGroup checkGroup, Integer[] checkItemIds);

    /**
     * 删除检查组
     */
    void deleteByGroupId(Integer id);
    /**
     * 查询所有检查组
     */
    List<CheckGroup> findAll();
}
