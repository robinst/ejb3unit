package com.bm.utils.substitues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 * Implementation which is not doing anything.
 * @author Daniel Wiese
 * @since Jul 24, 2007
 */
public class FakedTimerService implements TimerService {

	/**
	 * {@inheritDoc}
	 */
	public Timer createTimer(long duration, Serializable info)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		return new FakedTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	public Timer createTimer(long initialDuration, long intervalDuration,
			Serializable info) throws IllegalArgumentException,
			IllegalStateException, EJBException {
		return new FakedTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	public Timer createTimer(Date expiration, Serializable info)
			throws IllegalArgumentException, IllegalStateException,
			EJBException {
		return new FakedTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	public Timer createTimer(Date initialExpiration, long intervalDuration,
			Serializable info) throws IllegalArgumentException,
			IllegalStateException, EJBException {
		return new FakedTimer();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getTimers() throws IllegalStateException, EJBException {
		return new ArrayList<Timer>();
	}

}
