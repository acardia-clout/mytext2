package com.itheima.dao;

import com.itheima.pojo.Permission;
import java.util.Set;

/**
 * 权限接口
 */
public interface PermissionDao {
    /**
     * 根据角色id查询权限列表
     * @param roleId
     * @return
     */
    Set<Permission> findPermissionsByRoleId(Integer roleId);
}
