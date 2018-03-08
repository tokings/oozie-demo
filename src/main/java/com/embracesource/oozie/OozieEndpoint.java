package com.embracesource.oozie;

import org.apache.oozie.client.OozieClient;

/**
 * Oozie�ͻ��˷��ʶ˵�
 * @author	tokings.tang@embracesource.com
 * @date	2018��1��24�� ����11:47:30
 * @copyright	http://www.embracesource.com
 */
interface OozieEndpoint {
	
	/**
	 * ��ʼ���ͻ���
	 * @throws OozieException
	 */
	void init() throws OozieException;
	
	/**
	 * �Ƿ��ʼ���ͻ���
	 * @return
	 */
	boolean isInit();
	
	/**
	 * ��ȡ�ͻ���
	 * @return
	 * @throws OozieException
	 */
	OozieClient getClient() throws OozieException;
	
}
