package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * 专门负责发送验证码
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateController {

    /*@Autowired
    private JedisPool jedisPool;*/

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 体检预约发送手机验证码
     */
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone){

        try {
            //1.生成验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //2.调用SMSUtils发送验证码
            if(false){
                SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
            }
            //3.将生成的验证码存入redis  key:保证不同业务唯一

            redisTemplate.opsForValue().set(telephone+ RedisMessageConstant.SENDTYPE_ORDER,code.toString(),5, TimeUnit.MINUTES);
            //jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,5*60,code.toString());
            //输出手机号码和验证码
            System.out.println("手机号码：：：：：："+telephone+"：：：：：：验证码：：：：：："+code);
            //4.返回结果
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }



    /**
     * 手机快速登录-发送手机验证码
     */
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){

        try {
            //1.生成验证码
            Integer code = ValidateCodeUtils.generateValidateCode(4);
            //2.调用SMSUtils发送验证码
            if(false){
                SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
            }
            //3.将生成的验证码存入redis  key:保证不同业务唯一
            redisTemplate.opsForValue().set(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,code.toString(),5, TimeUnit.MINUTES);
            //jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,5*60,code.toString());
            //输出手机号码和验证码
            System.out.println("手机号码：：：：：："+telephone+"：：：：：：验证码：：：：：："+code);
            //4.返回结果
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
    }
}
