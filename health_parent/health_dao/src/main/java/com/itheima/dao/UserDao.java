package com.itheima.dao;

import com.itheima.pojo.User;

/**
 * 用户持久层接口
 */
public interface UserDao {
    /**
     * 根据用户名查询用户对象信息
     * @param username
     * @return
     */
    User findUserByUsername(String username);
}
