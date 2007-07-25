package com.bm.utils.substitues;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

public class FakedTimer implements Timer{

	/**
	 * {@inheritDoc}
	 */
	public void cancel() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		
	}
	/**
	 * {@inheritDoc}
	 */
	public TimerHandle getHandle() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public Serializable getInfo() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public Date getNextTimeout() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	public long getTimeRemaining() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return 0;
	}

}
