package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;

/**
 * 套餐控制层
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference
    private SetmealService setmealService;

    /*@Autowired
    private JedisPool jedisPool;*/

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 套餐图片上传
     * 方式一：MultipartFile imgFile
     * 方式二：@RequestParam("imgFile") MultipartFile imgFile
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        try {
            // 获取图片MultipartFile 1.jpg  
            //获取原始文件名
            String originalFilename = imgFile.getOriginalFilename();//1.jpg
            int lastIndexOf = originalFilename.lastIndexOf("."); 
            //获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf);
            // 生成一个唯一的文件名称 UUID
            String fileName = UUID.randomUUID().toString() + suffix;
            // 最终调用七牛云接口
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            //上传图片完成后 将图片名称记录到redis
            redisTemplate.opsForSet().add(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            //jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
            return new Result(true, MessageConstant.UPLOAD_SUCCESS,fileName);//返回文件名（页面预览图片用户+提交表单图片名称）
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 新增套餐数据
     */
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            //新增套餐数据完成后 将图片名称记录到redis
            redisTemplate.opsForSet().add(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
            //jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    /**
     * 套餐分页查询
     */
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        try {
            PageResult pageResult = setmealService.findPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize(),queryPageBean.getQueryString());
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 删除套餐数据
     */
    @RequestMapping(value = "/delete")
    public Result delete(Integer id){
        try {
            setmealService.deleteById(id);
            return new Result(true, MessageConstant.DELETE_SETMEAL_SUCCESS);
        } catch (RuntimeException e) { //捕获MessageConstant.ERROR_CHECKGROUP_SETMEAL
            e.printStackTrace();
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.DELETE_SETMEAL_FAIL);
        }
    }

    /**
     * 根据套餐id查询套餐对象
     */
    @RequestMapping(value = "/findById")
    public Result findById(Integer id){
        try {
            Setmeal setmeal = setmealService.findById(id);
            return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
        }  catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
        }
    }

    /**
     * 根据套餐id查询所有检查组ids
     */
    @RequestMapping(value = "/findCheckGroupIdsBySetmealId")
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id){
        try {
            List<Integer> list = setmealService.findCheckGroupIdsBySetmealId(id);
            return list;
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 编辑套餐数据
     */
    @RequestMapping(value = "/edit")
    public Result edit(@RequestBody Setmeal setmeal,Integer[] checkgroupIds){
        try {
            setmealService.edit(setmeal,checkgroupIds);
            return new Result(true, MessageConstant.EDIT_SETMEAL_SUCCESS);
        }  catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.EDIT_SETMEAL_FAIL);
        }
    }
}
