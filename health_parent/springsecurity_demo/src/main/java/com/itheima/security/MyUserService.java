package com.itheima.security;

import com.itheima.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 自定义认证授权类
 * @RestController @Service @Repository  @Component
 */
@Component
public class MyUserService implements UserDetailsService{

    //public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//    private static BCryptPasswordEncoder encoder;

    @Bean
    public static BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 通过静态代码块模式数据库
     */
    public static Map<String, User> map = new HashMap<String, User>();

    static {
        com.itheima.pojo.User user1 = new com.itheima.pojo.User();
        user1.setUsername("admin");
        user1.setPassword(encoder().encode("admin")); //模拟数据的密码已经加密后的

        com.itheima.pojo.User user2 = new com.itheima.pojo.User();
        user2.setUsername("zhangsan");
        user2.setPassword(encoder().encode("123"));

        map.put(user1.getUsername(), user1);
        map.put(user2.getUsername(), user2);
    }

    /**
     * 根据用户名加载用户对象
     *
     * 之前：登录功能通过用户名和密码到用户表验证
     * 现在：SpringSecurity密码是由框架来验证的
     * @param username 登录界面输入的用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username:::::::::::::::::::"+username);
        //1.根据用户名到数据库中查询用户信息(造数据)
        User user = map.get(username);
        //2.判断用户名是否存在,不存在返回null
        if(user == null){
            return null;//用户根本不存在
        }
        //3.如果存在，授权（等框架验证密码成功后，再授予）
        //获取密码
        ///String password = "{noop}"+user.getPassword();
        String password = user.getPassword();
        List<GrantedAuthority> list = new ArrayList<>();
        //参数： 权限关键字
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        list.add(new SimpleGrantedAuthority("CHEKITEM_ADD"));
        list.add(new SimpleGrantedAuthority("CHEKITEM_EDIT"));

        //String username: 用户名
        // String password：密码（根据用户名查询用户表 数据库中获取的）
        // Collection<? extends GrantedAuthority> authorities：授权
        return new org.springframework.security.core.userdetails.User(username,password,list);
    }
}
