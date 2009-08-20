package com.bm.utils.substitues;

import java.util.Properties;

import javax.mail.Session;

import com.bm.ejb3guice.inject.Provider;

/**
 * A simple provider for a mail session that will create the default mail session.
 * This provider was created to fix issue 2802736.
 * 
 * @author tnfink
 *
 */
public class MailSessionProvider implements Provider<Session> {

	public Session get() {
		return Session.getDefaultInstance(new Properties());
	}

}
