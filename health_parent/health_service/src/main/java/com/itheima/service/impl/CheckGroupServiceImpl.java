package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckGroupService;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查组服务接口实现类
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    /**
     * 新检查组
     * @param checkGroup
     * @param checkItemIds
     */
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkItemIds) {
        //新增检查组表（单表插入）
        checkGroupDao.add(checkGroup);
        Integer groupId = checkGroup.getId();///检查组id
        //往中间表插入数据(单独抽取一个方法 --- 后续编辑还会用此方法)
        setCheckGroupAndCheckItem(groupId,checkItemIds);
    }
    /**
     * 检查组分页查询
     */
    @Override
    public PageResult findPage(Integer currentPage, Integer pageSize, String queryString) {
        //设置分页参数
        PageHelper.startPage(currentPage, pageSize);//当前页码  每页显示多少条记录
        //需要被分页的语句查询（一定要写到第一行代码后面）
        List<CheckGroup> checkGroupList = checkGroupDao.selectByCondition(queryString);//分页逻辑交给PageHelper插件搞定了  select * from table where 用户输入的条件 （limit ）
        //封装分页PageInfo对象
        PageInfo<CheckGroup> pageInfo = new PageInfo<>(checkGroupList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
    /**
     * 根据检查组id查询检查组对象
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }
    /**
     *
     * 根据检查组id查询检查项ids
     */
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    /**
     * 检查组编辑
     */
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkItemIds) {
        //1.	更新检查组表 update
        checkGroupDao.edit(checkGroup);
        //2.	删除检查组和检查项关系
        checkGroupDao.deleteReShip(checkGroup.getId());
        //3.	重新建立检查组和检查项关系（检查组id +  检查项ids 页面勾选）
        setCheckGroupAndCheckItem(checkGroup.getId(),checkItemIds);
    }

    /**
     * 删除检查组
     */
    @Override
    public void deleteByGroupId(Integer id) {
        // 根据检查组id 查询检查组和检查项中间表
        int c1 = checkGroupDao.findCheckGroupAndCheckItemByGroupId(id);
        // Count>0  提示不能删除
        if(c1>0){
            throw  new RuntimeException(MessageConstant.ERROR_CHECKITEM_CHECKGROUP);
        }
        // Count =0  根据检查组id查询套餐和检查组中间表
        int c2 = checkGroupDao.findCheckGroupAndSetmealByGroupId(id);
        // Count>0 提示不能删除检查组
        if(c2 > 0){
            throw  new RuntimeException(MessageConstant.ERROR_CHECKGROUP_SETMEAL);
        }
        //删除检查组
        checkGroupDao.deleteByGroupId(id);

    }
    /**
     * 查询所有检查组
     */
    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    /***
     * 往检查组和检查项中间表插入数据
     * @param groupId
     * @param checkItemIds
     */
    public void setCheckGroupAndCheckItem(Integer groupId,Integer[] checkItemIds){
        if(checkItemIds !=null && checkItemIds.length>0 ){
            for (Integer checkItemId : checkItemIds) {
                //使用map来封装中间表的数据
                Map<String,Integer> map = new HashMap();
                map.put("checkGroupId",groupId);
                map.put("checkItemId",checkItemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
