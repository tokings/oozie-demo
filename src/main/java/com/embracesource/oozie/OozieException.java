package com.embracesource.oozie;

import org.apache.oozie.client.OozieClientException;

public class OozieException extends OozieClientException {

	/**  */
	private static final long serialVersionUID = 1L;

	public OozieException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
		// TODO Auto-generated constructor stub
	}

	public OozieException(String errorCode, String message) {
		super(errorCode, message);
		// TODO Auto-generated constructor stub
	}

	public OozieException(String errorCode, Throwable cause) {
		super(errorCode, cause);
		// TODO Auto-generated constructor stub
	}


	
}
