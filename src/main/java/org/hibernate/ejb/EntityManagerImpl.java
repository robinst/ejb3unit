//$Id: EntityManagerImpl.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.PersistenceContextType;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.transaction.Synchronization;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Gavin King
 */
public class EntityManagerImpl extends AbstractEntityManagerImpl {

	private static Log log = LogFactory.getLog( EntityManagerImpl.class );
	protected Session session;
	protected SessionFactory sessionFactory;
	protected boolean open;
	protected boolean discardOnClose;

	public EntityManagerImpl(
			SessionFactory sessionFactory, PersistenceContextType pcType,
			PersistenceUnitTransactionType transactionType,
			boolean discardOnClose
	) {
		super( pcType, transactionType );
		this.sessionFactory = sessionFactory;
		this.open = true;
		this.discardOnClose = discardOnClose;
		postInit();
	}

	public Session getSession() {

		if ( !open ) throw new IllegalStateException( "EntityManager is closed" );

		if ( session == null ) {
			session = sessionFactory.openSession();
			if ( persistenceContextType == PersistenceContextType.TRANSACTION ) {
				( (SessionImplementor) session ).setAutoClear( true );
			}
		}

		return session;

	}

	public void close() {

		if ( !open ) throw new IllegalStateException( "EntityManager is closed" );
		if ( !discardOnClose && isTransactionInProgress() ) {
			//delay the closing till the end of the enlisted transaction
			getSession().getTransaction().registerSynchronization( new Synchronization() {
					public void beforeCompletion() {
						//nothing to do
					}

					public void afterCompletion(int i) {
						//TODO should I check for isOpen() ?
						if (session != null) {
							log.debug( "Closing session after transaction completion");
							session.close();
						}
						//TODO session == null should not happen
					}
				}
			);
		}
		else {
			//close right now
			if ( session != null ) session.close();
		}
		open = false;
	}

	public boolean isOpen() {
		//adjustFlushMode(); //don't adjust, can't be done on closed EM
		try {
			if (open) getSession().isOpen(); //to force enlistment in tx
			return open;
		}
		catch (HibernateException he) {
			throwPersistenceException(he);
			return false;
		}
	}

}
