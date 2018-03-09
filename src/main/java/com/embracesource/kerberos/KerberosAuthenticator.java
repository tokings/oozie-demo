package com.embracesource.kerberos;

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

/**
 * Kerberos��֤
 * @author	tokings.tang@embracesource.com
 * @date	2018��3��9�� ����5:25:35
 * @copyright	http://www.embracesource.com
 */
public class KerberosAuthenticator {

	/**
	 * ��֤
	 * @param authenticationType	��֤����
	 * @param krb5FilePath	��֤�����������ļ�·��
	 * @param principal	��֤����
	 * @param keytab	��֤Ʊ���ļ�·��
	 * @return	UserGroupInformation ��֤����û�����Ϣ���������Ҫ��֤��ֱ�ӷ���null
	 * @throws Exception
	 */
	public static UserGroupInformation authenticate(String authenticationType, String krb5FilePath, String principal,
			String keytab) throws Exception {

		if (authenticationType != null && "kerberos".equalsIgnoreCase(authenticationType.trim())) {
			System.out.println("��ʼ����kerberos�����֤.");
		} else {
			System.out.println("δ����kerberos�����֤.");
			return null;
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			// Ĭ�ϣ����ﲻ���õĻ���winĬ�ϻᵽ C���¶�ȡkrb5.init
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		} else {
			// linux ��Ĭ�ϵ� /etc/krb5.conf
			// �ж�ȡkrb5.conf,���ı����ѽ����ļ��ŵ�/etc/Ŀ¼�£��������㲻����������
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		}

		// ʹ��Hadoop��ȫ��¼
		Configuration conf = new Configuration();
		conf.set("hadoop.security.authentication", authenticationType);
		UserGroupInformation.setConfiguration(conf);
		UserGroupInformation.loginUserFromKeytab(principal, keytab);
		System.out.println(
				"Kerberos�����֤���, krb5FilePath��" + krb5FilePath + ", principal:" + principal + ", keytab:" + keytab);

		return UserGroupInformation.getLoginUser();
	}
	
	/**
	 * ͨ����֤�û������ִ�в���
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
