package com.bm.utils.substitues;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.bm.ejb3guice.annotations.InjectedIn;
import com.bm.ejb3guice.annotations.NotifyOnInject;

/**
 * @author Daniel Wiese
 * @author Marcus Nilsson Date: May 16, 2007 Time: 9:50:53 AM
 */
@NotifyOnInject
public class MockedTimerService implements TimerService {
	private Object caller;

	@Resource
	private EntityManager entityManager;

	private final Collection<TimerMock> timers = new ArrayList<TimerMock>();

	public MockedTimerService() {
	}
	
	@InjectedIn
	public void injectedIn(Object injectectedIn){
		this.caller = injectectedIn;
	}

	public Timer createTimer(long l, Serializable info)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		final Class callerClass = caller.getClass();

		TimerMock t = new TimerMock(l, info) {
			public void timerExpired() {
				Method[] methods = callerClass.getMethods();
				for (Method m : methods) {
					if (m.isAnnotationPresent(Timeout.class)) {
						try {
							EntityTransaction t = entityManager
									.getTransaction();
							t.begin();
							m.invoke(caller, this);
							t.commit();
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}

					timers.remove(this);
				}
			}
		};

		synchronized (timers) {
			timers.add(t);
		}

		t.start();

		return null;
	}

	public Timer createTimer(long l, long l1, Serializable serializable)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		throw new RuntimeException("Not implemented");
	}

	public Timer createTimer(Date date, Serializable serializable)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		throw new RuntimeException("Not implemented");
	}

	public Timer createTimer(Date date, long l, Serializable serializable)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	public Collection getTimers() throws IllegalStateException, EJBException {
		Collection res = new ArrayList();

		synchronized (timers) {
			for (TimerMock t : timers) {
				res.add(t);
			}
		}

		return res;
	}
}