package com.embracesource.oozie;

import org.apache.oozie.client.OozieClient;

/**
 * Oozie客户端访问端点
 * @author	tokings.tang@embracesource.com
 * @date	2018年1月24日 上午11:47:30
 * @copyright	http://www.embracesource.com
 */
interface OozieEndpoint {
	
	/**
	 * 初始化客户端
	 * @throws OozieException
	 */
	void init() throws OozieException;
	
	/**
	 * 是否初始化客户端
	 * @return
	 */
	boolean isInit();
	
	/**
	 * 获取客户端
	 * @return
	 * @throws OozieException
	 */
	OozieClient getClient() throws OozieException;
	
}
