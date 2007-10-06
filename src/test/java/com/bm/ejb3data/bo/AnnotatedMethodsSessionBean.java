package com.bm.ejb3data.bo;

import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Example stateless session baen with a reference to a datasource.
 * 
 * @author Daniel Wiese
 * @since 08.11.2005
 */
@Stateless
public class AnnotatedMethodsSessionBean implements IMyMethodAnnotaedSessionBean {

	private static final long serialVersionUID = 1L;

	private DataSource ds;

	private EntityManager manager;

	private IMySessionBean mySessionBean;
	
	private SessionContext sc;

	private TimerService timerService;

	private boolean timerWasCalled;

	/**
	 * Operation which do somethig internal.
	 */
	public void executeOperation() {
		DataSource otherDS = this.mySessionBean.getDs();
		try {
			otherDS.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public DataSource getDs() {
		return ds;
	}
	
	@Resource(name = "java:/MSSqlDS")
	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public EntityManager getManager() {
		return manager;
	}

	@PersistenceContext
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}

	public IMySessionBean getMySessionBean() {
		return mySessionBean;
	}

	@EJB
	public void setMySessionBean(IMySessionBean mySessionBean) {
		this.mySessionBean = mySessionBean;
	}

	public SessionContext getSc() {
		return sc;
	}

	@Resource
	public void setSc(SessionContext sc) {
		this.sc = sc;
	}

	public TimerService getTimer() {
		return timerService;
	}

	@Resource
	public void setTimer(TimerService timer) {
		this.timerService = timer;
	}	
	
	
	public void createTimer(long intervalDuration) {
	    @SuppressWarnings("unused")
		Timer timer = this.timerService.createTimer(intervalDuration,
	        "Created new timer");
	    
	  }

	  @Timeout
	  public void timeout(Timer timer) {
	    timerWasCalled = true;
	  }

	 /**
	  * True if the timer was called
	  * @return true if called
	  */
	public boolean isTimerWasCalled() {
		return timerWasCalled;
	}

	

}
