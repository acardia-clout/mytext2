package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 检查项接口服务
 */
public interface CheckItemService {
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
     */
    PageResult findPage(Integer currentPage, Integer pageSize, String queryString);

    /**
     * 根据检查项id删除检查项
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
