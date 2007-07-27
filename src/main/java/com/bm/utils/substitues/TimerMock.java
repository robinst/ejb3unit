package com.bm.utils.substitues;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

/**
 * Author: Marcus Nilsson Date: May 16, 2007 Time: 9:52:08 AM
 */
public class TimerMock extends Thread implements Timer {
	private long timeout;

	private Serializable info;

	/**
	 * Constructor.
	 * 
	 * @param timeout
	 *            timeout
	 * @param info
	 *            the info object.
	 */
	public TimerMock(long timeout, Serializable info) {
		this.timeout = timeout;
		this.info = info;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		try {
			Thread.sleep(this.timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.timerExpired();
	}

	/**
	 * {@inheritDoc}
	 */
	public void timerExpired() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		this.interrupt();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getTimeRemaining() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return this.timeout;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getNextTimeout() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Serializable getInfo() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return info;
	}

	/**
	 * {@inheritDoc}
	 */
	public TimerHandle getHandle() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return null;
	}
}