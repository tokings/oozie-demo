package com.embracesource.oozie;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.oozie.client.CoordinatorJob;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;

public class CoordinatorSubmit {

	private static OozieClient client = null;
	private static final String OOZIE_URL = "http://master:11000/oozie";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm+0800");

	public static void main(String[] args) throws Exception {

		init();

		test();
	}

	private static void test() throws Exception {

		Calendar c = Calendar.getInstance();

		Properties conf = client.createConfiguration();
		conf.setProperty("nameNode", "hdfs://master:8020");
		conf.setProperty("jobTracker", "master:8050");
		conf.setProperty("queueName", "default");
		conf.setProperty("examplesRoot", "examples");
		conf.setProperty(OozieClient.USER_NAME, "root");
		conf.setProperty("start", sdf.format(c.getTime()));
		c.add(Calendar.MINUTE, 5);
		conf.setProperty("end", sdf.format(c.getTime()));
		conf.setProperty("cron", "* * * * ?");
		conf.setProperty("workflowAppUri", "${nameNode}/user/${user.name}/${examplesRoot}/apps/cron");
		conf.setProperty(OozieClient.COORDINATOR_APP_PATH, "${nameNode}/user/${user.name}/${examplesRoot}/apps/cron");

		String jobId = client.run(conf);
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

	private static void init() {
		client = new OozieClient(OOZIE_URL);
	}

}
