package com.itheima.test;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 删除垃圾图片（第4章会改进）
 */
//@ContextConfiguration(locations = "classpath:spring-redis.xml")
//@RunWith(SpringJUnit4ClassRunner.class)
public class DeleteQiNiuTest {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 删除垃圾图片
     */
    //@Test
    public void deleteImgs(){
        //1.获取两个集合差集
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        //2.遍历集合
        for (String imgName : set) {
            System.out.println("删除图片的名称：：：：：：：："+imgName);
            //3.调用七牛云删除图片
            QiniuUtils.deleteFileFromQiniu(imgName);
            //4.删除redis的垃圾图片记录
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,imgName);
            System.out.println("删除图片成功：：：：：：：：：：：：");
        }

    }
}
