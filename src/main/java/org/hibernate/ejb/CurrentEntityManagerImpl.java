//$Id: CurrentEntityManagerImpl.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.PersistenceContextType;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.util.JTAHelper;

/**
 * @author Gavin King
 * @author Emmanuel Bernard
 * @deprecated no longer used since getEntityManager is no longer here
 */
public class CurrentEntityManagerImpl extends AbstractEntityManagerImpl {

	private SessionFactory sessionFactory;

	public CurrentEntityManagerImpl(SessionFactory sessionFactory, PersistenceUnitTransactionType transactionType) {
		super( PersistenceContextType.TRANSACTION, transactionType );
		this.sessionFactory = sessionFactory;
		postInit();
	}

	public Session getSession() {
		/**
		 * Handle non transactional mode by requesting a temporary session to the session factory
		 * This session, will aggressively use the AFTER_STATEMENT connection release mode to be
		 * sure the conenctions are released. Be aware that the session will not be closed explicitly.
		 */

		Session s;
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sessionFactory;
		if ( !JTAHelper.isTransactionInProgress( sfi ) ) {
			s = sfi.openTemporarySession();
			( (SessionImplementor) s ).setAutoClear( true );
		}
		else {
			s = sessionFactory.getCurrentSession();
		}
		return s;
	}

	public void close() {
		throw new UnsupportedOperationException( "cannot close the JTA-bound EntityManager" );
	}

	public boolean isOpen() {
		//TODO Hum contradictory comments, I'm getting mad
		//no need to force enlistment in the tx, a current session is always enlisted
		//adjustFlushMode(); //don't adjust, can't be done on closed EM
		getSession().isOpen(); //to force enlistment in tx
		return true;
	}

}
