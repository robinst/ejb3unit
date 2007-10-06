package com.bm.ejb3data.bo;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
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
public class AnnotatedFieldsSessionBean implements IMyOtherSessionBean {

	private static final long serialVersionUID = 1L;

	@Resource(name = "java:/MSSqlDS")
	private DataSource ds;

	@PersistenceContext
	private EntityManager manager;

	@EJB
	private IMySessionBean mySessionBean;
	
	@Resource
	private TimerService timerService;
	
	private IMySessionBean mySessionBeanJndi;
	
	@Resource
	private SessionContext sc;

	private boolean timerWasCalled = false;
	
	@PostConstruct
	void setup() {
		this.mySessionBeanJndi= (IMySessionBean)sc.lookup("ejb/MySessionBean");
	}

	/**
	 * Returns the DataSource.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see IMyOtherSessionBean#getDs()
	 * @return the DataSource
	 */
	public DataSource getDs() {
		return this.ds;
	}

	/**
	 * Returns the entity manager.
	 * 
	 * @author Daniel Wiese
	 * @since 08.11.2005
	 * @see IMyOtherSessionBean#getEm()
	 * @return the entity manager.
	 */
	public EntityManager getEm() {
		return this.manager;
	}

	/**
	 * Reference to another session bean via dependency injection.
	 * 
	 * @see com.bm.ejb3data.bo.IMyOtherSessionBean#getSessionBean()
	 * @return the injected session bean.
	 */
	public IMySessionBean getSessionBean() {
		return this.mySessionBean;
	}
	
	/**
	 * Reference to another session bean via jndi lookup.
	 * 
	 * @see com.bm.ejb3data.bo.IMyOtherSessionBean#getSessionBean()
	 * @return the injected session bean.
	 */
	public IMySessionBean getSessionBeanOverJndi() {
		return this.mySessionBeanJndi;
	}

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

	/**
	 * Returns the timer.
	 * @return Returns the timer.
	 */
	public TimerService getTimer() {
		return timerService;
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
