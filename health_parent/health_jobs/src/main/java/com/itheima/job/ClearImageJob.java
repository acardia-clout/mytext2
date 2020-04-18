package com.itheima.job;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 定时任务 清理垃圾图片
 */
public class ClearImageJob {
    //注入JedisPool
    /*@Autowired
    private JedisPool jedisPool;*/

    @Autowired
    private RedisTemplate redisTemplate;

    public void deleteImgs(){
        //1.获取两个集合差集
        Set<String> set = redisTemplate.opsForSet().difference(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
       // Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        //2.遍历集合
        for (String imgName : set) {
            System.out.println("删除图片的名称：：：：：：：："+imgName);
            //3.调用七牛云删除图片
            QiniuUtils.deleteFileFromQiniu(imgName);
            //4.删除redis的垃圾图片记录
            redisTemplate.opsForSet().remove(RedisConstant.SETMEAL_PIC_RESOURCES,imgName);
            //jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,imgName);
            System.out.println("删除图片成功：：：：：：：：：：：：");
        }

    }
}
