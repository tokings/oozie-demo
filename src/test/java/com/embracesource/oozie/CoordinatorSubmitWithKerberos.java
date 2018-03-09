package com.embracesource.oozie;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.oozie.client.CoordinatorJob;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

import com.embracesource.kerberos.Executor;
import com.embracesource.kerberos.KerberosAuthenticator;

/**
 * 
 * @author	tokings.tang@embracesource.com
 * @date	2018年3月9日 下午5:16:30
 * @copyright	http://www.embracesource.com
 */
public class CoordinatorSubmitWithKerberos {

	private static OozieClient client = null;
	private static final String OOZIE_URL = "http://master.embracesource.com:11000/oozie";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm+0800");

	private static final String authenticationType = "kerberos";
	private static final String krb5FilePath = "krb5.ini";
	private static final String principal = "oozie/master.embracesource.com@EXAMPLE.COM";
	private static final String keytab = "oozie.service.keytab";
	/**
	 * 已认证的用户组信息
	 */
	private static UserGroupInformation ugi = null;

	public static void main(String[] args) throws Exception {

		init();

//		authentication();
//		// 使用认证用户执行操作
//		ugi.doAs(new PrivilegedExceptionAction<Object>() {
//			public Object run() throws Exception {
//				test();
//				return "SUCCESS";
//			}
//		});
		
		KerberosAuthenticator.authenticate(authenticationType, krb5FilePath, principal, keytab);
		Object ret = KerberosAuthenticator.doAs(new Executor<Object>() {
			@Override
			public Object exec() throws Exception {
				test();
				return "SUCCESS";
			}
		});
		System.out.println("ret:" + ret);
	}

	private static void init() throws Exception {
		client = new OozieClient(OOZIE_URL);
	}

	private static void authentication() {

		if (authenticationType != null && "kerberos".equalsIgnoreCase(authenticationType.trim())) {
			System.out.println("开始设置kerberos身份验证.");
		} else {
			System.out.println("未设置kerberos身份验证.");
			return;
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			// 默认：这里不设置的话，win默认会到 C盘下读取krb5.init
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		} else {
			// linux 会默认到 /etc/krb5.conf
			// 中读取krb5.conf,本文笔者已将该文件放到/etc/目录下，因而这里便不用再设置了
			System.setProperty("java.security.krb5.conf", krb5FilePath);
		}

		try {
			// 使用Hadoop安全登录
			Configuration conf = new Configuration();
			conf.set("hadoop.security.authentication", authenticationType);
			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation.loginUserFromKeytab(principal, keytab);
			System.out.println(
					"Kerberos身份认证完成, krb5FilePath：" + krb5FilePath + ", principal:" + principal + ", keytab:" + keytab);
			
			ugi = UserGroupInformation.getLoginUser();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void test() throws Exception {

		Calendar c = Calendar.getInstance();

		Properties props = client.createConfiguration();
		props.setProperty("nameNode", "hdfs://master.embracesource.com:8020");
		props.setProperty("jobTracker", "master.embracesource.com:8050");
		props.setProperty("queueName", "default");
		props.setProperty("examplesRoot", "examples");
		props.setProperty(OozieClient.USER_NAME, "root");
		props.setProperty("start", sdf.format(c.getTime()));
		c.add(Calendar.MINUTE, 5);
		props.setProperty("end", sdf.format(c.getTime()));
		props.setProperty("cron", "* * * * ?");
		props.setProperty("workflowAppUri", "${nameNode}/user/${user.name}/${examplesRoot}/apps/cron");
		props.setProperty(OozieClient.COORDINATOR_APP_PATH, "${nameNode}/user/${user.name}/${examplesRoot}/apps/cron");

		String jobId = client.run(props);
		System.out.println("job submit finished. jobId:" + jobId);

		CoordinatorJob job = null;

		while (true) {
			job = getJobInfo(jobId);
			if (job.getStatus() != CoordinatorJob.Status.KILLED && job.getStatus() != CoordinatorJob.Status.FAILED
					&& job.getStatus() != CoordinatorJob.Status.SUCCEEDED) {
				System.out.println("Workflow job running ...");
				System.out.println(job);
				job.getActions().forEach(action -> {
					System.out.println(action);
				});
				Thread.sleep(60 * 1000);
			} else {
				break;
			}
		}

		System.out.println("Workflow job completed ...");
		System.out.println("Workflow job final status:" + getJobInfo(jobId).getStatus());

	}

	private static CoordinatorJob getJobInfo(String jobId) throws OozieClientException {
		return client.getCoordJobInfo(jobId);
	}

}
