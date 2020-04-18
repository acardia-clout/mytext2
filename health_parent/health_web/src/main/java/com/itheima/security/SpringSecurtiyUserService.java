package com.itheima.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 认证和授权类
 * 工程启动的时候对象放到spring容器中 ，首字母小写
 */
@Component
public class SpringSecurtiyUserService implements UserDetailsService {

    /**
     * 引入用户服务
     */
    @Reference
    private UserService userService;


    /**
     * 根据用户名获取用户对象信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.根据用户名查询用户对象信息
        com.itheima.pojo.User user = userService.findUserByUsername(username);
        //2.如果用户对象为空，return null
        if(user == null){
            return null;
        }
        //3.如果存在，授权
        String password = user.getPassword();
        List<GrantedAuthority> list = new ArrayList<>();
        //String username, String password, Collection<? extends GrantedAuthority> authorities
        //user(当前用户数据+角色数据+权限数据)
        Set<Role> roles = user.getRoles();
        if(roles != null && roles.size()>0){
            for (Role role : roles) {
                //将角色关键字授予给当前用户
                list.add(new SimpleGrantedAuthority(role.getKeyword()));
                Set<Permission> permissions = role.getPermissions();
                if(permissions!=null && permissions.size()>0){
                    for (Permission permission : permissions) {
                        //将权限表的权限关键字授予当前用户
                        list.add(new SimpleGrantedAuthority(permission.getKeyword()));
                    }
                }
            }
        }
        return new User(username,password,list);
    }
}
