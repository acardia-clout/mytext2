package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检查项控制层
 */
@RestController  //@Controller+@ResponseBody
@RequestMapping("/checkitem")
public class CheckItemController {

    //调用服务接口
    @Reference
    private CheckItemService checkItemService;

    /**
     * 查询检查项（不分页）
     */
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<CheckItem> checkItemList = checkItemService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 新增检查项
     */
    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('CHECKITEM_ADD')")//表示用户必须拥有add权限才能调用当前方法
    public Result add(@RequestBody CheckItem checkItem){
        try {
            checkItemService.add(checkItem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_CHECKITEM_FAIL);
        }
    }
    /**
     * 检查项分页查询
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        try {
            PageResult pageResult = checkItemService.findPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize(),queryPageBean.getQueryString());
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 根据检查项id删除检查项
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")
    public Result delete(Integer id){
        try {
            checkItemService.delete(id);
            return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());//e.getMessage() == MessageConstant.ERROR_CHECKITEM_CHECKGROUP
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_CHECKITEM_FAIL);
        }
    }

    /**
     * 根据检查项id查询检查项对象
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            CheckItem checkItem = checkItemService.findById(id);
            if(checkItem !=null){
                return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
    }
    /**
     * 编辑检查项
     */
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckItem checkItem){
        try {
            checkItemService.edit(checkItem);
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_CHECKITEM_FAIL);
        }
    }
}
