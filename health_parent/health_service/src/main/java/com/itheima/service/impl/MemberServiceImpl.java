package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员服务实现类
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {


    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        memberDao.add(member);
    }

    /**
     * 根据年月获取累计总会员数量
     * @param months
     * @return
     */
    @Override
    public List<Integer> findMemberCountByMonth(List<String> months) {
        List<Integer> list = new ArrayList<>();
        for (String month : months) {
            //month=2020-01 ===处理作用获取会员数量条件==> 2020-01-31
            month = month + "-31";
            int count = memberDao.findMemberCountBeforeDate(month);
            list.add(count);
        }
        return list;
    }
}
