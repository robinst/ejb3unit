/**
 * 
 */
package com.bm.inject;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Session;

@Stateless
public class MailSessionTestServiceBean implements MailSessionTestService {
	@SuppressWarnings("unused")
	@Resource
	private Session mailSession;
}