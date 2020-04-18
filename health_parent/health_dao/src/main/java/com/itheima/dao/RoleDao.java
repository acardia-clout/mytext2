package com.itheima.dao;

import com.itheima.pojo.Role;

import java.util.Set;

/**
 * 角色接口
 */
public interface RoleDao {
    /**
     * 根据用户id查询角色列表
     * @param userId
     * @return
     */
    Set<Role> findRolesByUserId(Integer userId);
}
