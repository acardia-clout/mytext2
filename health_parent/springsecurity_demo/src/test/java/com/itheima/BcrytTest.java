package com.itheima;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * bcryt：encode(加密) matches(匹配密码是否正确 true false)
 */
public class BcrytTest {
    @Test
    public void testBcryt(){
        //将明文加密得到秘钥
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String hashValue = encoder.encode("admin123");
//        System.out.println("::::::::::::::::"+hashValue);
        //将用户输入的明文 跟 秘钥对比
        //$2a$10$JL2pw1OYKj4lDtUTQUry9umEhsgs9Ro17lBjDsgjofg0ydmxc5g8y
        //$2a$10$LUNs1dRR.05/w7FwJF2zr.lhJIvgHfKD7OjqNFESwqrwwUVUcIdEu
        boolean matches = encoder.matches("admin123", "$2a$10$JL2pw1OYKj4lDtUTQUry9umEhsgs9Ro17lBjDsgjofg0ydmxc5g8y");
        boolean matches2 = encoder.matches("admin123", "$2a$10$LUNs1dRR.05/w7FwJF2zr.lhJIvgHfKD7OjqNFESwqrwwUVUcIdEu");
        System.out.println(matches+"--------------"+matches2);

    }
}
