package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 移动端登录控制层
 */
@RestController
@RequestMapping("/login")
public class LoginMobileController {

//    @Autowired
//    private JedisPool jedisPool;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private MemberService memberService;

    /**
     * 手机号码快速登录
     * map:手机号码+验证码
     */
    @RequestMapping("/check")
    public Result login(HttpServletResponse response,@RequestBody Map map){
        //1.接收用户输入的信息（Map） {"validateCode":"1111111","telephone":"13111222111"}
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");
        //2.验证码校验
        String redisCode =  (String)redisTemplate.opsForValue().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        //String redisCode = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if(StringUtils.isEmpty(validateCode)||StringUtils.isEmpty(redisCode) || !validateCode.equals(redisCode)){
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //3.如果用户不存在，则自动注册
        Member member = memberService.findByTelephone(telephone);
        if(member == null){
            member = new Member();
            member.setPhoneNumber(telephone);//手机号码
            member.setRegTime(new Date());//注册时间
            memberService.add(member);
        }
        //4.用户信息保存cookie中，返回前端页面
        Cookie cookie = new Cookie("login_member_telephone",telephone);
        cookie.setPath("/");//所有的页面都能获取cookie数据
        cookie.setMaxAge(30*60*60*24);//设置cookie过期时间 1个月
        response.addCookie(cookie);
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }
}
