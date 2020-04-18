package com.itheima;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 */
public class MD5Utils {
	/**
	 * 使用md5的算法进行加密
	 */
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(
					plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	public static void main(String[] args) {
		System.out.println(md5("123456@#@$@#$FSDRW#$@R2"));
		//（加密）通过明文加密后的密文 每一次都是一样的
		//0192023a7bbd73250516f069df18b500
		//0192023a7bbd73250516f069df18b500

		//“解密”并不是真的解密
		// 用户页面输入admin123 跟 数据库中 0192023a7bbd73250516f069df18b500 比较？
		//admin123 =用相同的加密算法加密=》0192023a7bbd73250516f069df18b500
		//将加密后的秘钥 和 数据库的秘钥进行对比

		//密码加密后是不能解密的（如果能解密那就不安全了，加密是不可逆）
	}

}