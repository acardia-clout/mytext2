package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;

/**
 * 会员服务接口
 */
public interface MemberService {
    /**
     * 根据手机号码查询会员信息
     * @param telephone
     * @return
     */
    Member findByTelephone(String telephone);

    /**
     * 自动注册会员信息
     * @param member
     */
    void add(Member member);

    /**
     * 根据年月获取累计总会员数量
     * @param months
     * @return
     */
    List<Integer> findMemberCountByMonth(List<String> months);
}
