package com.embracesource.oozie;

import org.apache.oozie.client.OozieClient;

public abstract class AbstractOozieService implements Submitter,Monitor {

	private static OozieClient client = null;
	
	private static boolean init = false;
	
	@Override
	public void init() throws OozieException {

		
		init = true;
	}
	

	@Override
	public boolean isInit() {
		return init;
	}



	@Override
	public OozieClient getClient() throws OozieException {
		if(isInit()) {
			return client;
		}
		throw new OozieException("10001", "oozie client is not inited yet.");
	}

	
}
