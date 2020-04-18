package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项接口服务实现类
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;

    /**
     * 查询所有检查项（不分页）
     *
     * @return
     */
    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    /**
     * 新增检查项
     */
    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    /**
     * 检查项分页查询
     */
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //分页插件使用
        //设置分页参数
        PageHelper.startPage(currentPage, pageSize);//当前页码  每页显示多少条记录
        //需要被分页的语句查询（一定要写到第一行代码后面）
        List<CheckItem> checkItemList = checkItemDao.selectByCondition(queryString);//分页逻辑交给PageHelper插件搞定了  select * from table where 用户输入的条件 （limit ）
        //封装分页PageInfo对象
        PageInfo<CheckItem> pageInfo = new PageInfo<>(checkItemList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据检查项id删除检查项
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {
        // 1.查询检查项和检查组是否存在关系
        int count = checkItemDao.findCountByCheckItemId(id);
        // 2.如果有关系则不允许删除
        if (count > 0) {
            throw new RuntimeException(MessageConstant.ERROR_CHECKITEM_CHECKGROUP);
        }
        // 3.如果没有关系可以删除
        checkItemDao.delete(id);
    }

    /**
     * 根据检查项id查询检查项对象
     *
     * @param id
     * @return
     */
    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    /**
     * 编辑检查项
     */
    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }


}
