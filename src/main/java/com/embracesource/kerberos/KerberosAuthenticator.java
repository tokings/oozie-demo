package com.embracesource.kerberos;

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Kerberos认证
 * @author	tokings.tang@embracesource.com
 * @date	2018年3月9日 下午5:25:35
 * @copyright	http://www.embracesource.com
 */
public class KerberosAuthenticator {

	/**
	 * 认证
	 * @param authenticationType	认证类型
	 * @param krb5FilePath	认证服务器配置文件路径
	 * @param principal	认证主体
	 * @param keytab	认证票据文件路径
	 * @return	UserGroupInformation 认证后的用户组信息，如果不需要认证则直接返回null
	 * @throws Exception
	 */
	public static UserGroupInformation authenticate(String authenticationType, String krb5FilePath, String principal,
			String keytab) throws Exception {

		if (authenticationType != null && "kerberos".equalsIgnoreCase(authenticationType.trim())) {
			System.out.println("开始设置kerberos身份验证.");
		} else {
			System.out.println("未设置kerberos身份验证.");
			return null;
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			// 默认：这里不设置的话，win默认会到 C盘下读取krb5.init
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		} else {
			// linux 会默认到 /etc/krb5.conf
			// 中读取krb5.conf,本文笔者已将该文件放到/etc/目录下，因而这里便不用再设置了
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		}

		// 使用Hadoop安全登录
		Configuration conf = new Configuration();
		conf.set("hadoop.security.authentication", authenticationType);
		UserGroupInformation.setConfiguration(conf);
		UserGroupInformation.loginUserFromKeytab(principal, keytab);
		System.out.println(
				"Kerberos身份认证完成, krb5FilePath：" + krb5FilePath + ", principal:" + principal + ", keytab:" + keytab);

		return UserGroupInformation.getLoginUser();
	}
	
	/**
	 * 通过认证用户组代理执行操作
	 * @param executor
	 * @return
	 * @throws Exception
	 */
	public static <T> T doAs(Executor<T> executor) throws Exception {
		
		UserGroupInformation ugi = UserGroupInformation.getLoginUser();
		
		if(ugi != null) {
			return ugi.doAs(new PrivilegedExceptionAction<T>() {
				@Override
				public T run() throws Exception {
					return executor.exec();
				}
			});
		}else {
			throw new Exception("not aunthenicated! ignore this exec.");
		}
	}
}
