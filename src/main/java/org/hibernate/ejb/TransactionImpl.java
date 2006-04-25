//$Id: TransactionImpl.java,v 1.1 2006/04/17 12:11:08 daniel_wiese Exp $
package org.hibernate.ejb;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;

/**
 * @author Gavin King
 * @author Emmanuel Bernard
 */
public class TransactionImpl implements EntityTransaction {

	private AbstractEntityManagerImpl entityManager;
	private Transaction tx;
	private boolean rollbackOnly;

	public TransactionImpl(AbstractEntityManagerImpl entityManager) {
		this.entityManager = entityManager;
	}

	private Session getSession() {
		return entityManager.getSession();
	}

	public void begin() {
		try {
			rollbackOnly = false;
			if ( tx != null && tx.isActive() ) {
				throw new IllegalStateException( "Transaction already active" );
			}
			//entityManager.adjustFlushMode();
			tx = getSession().beginTransaction();
		}
		catch (HibernateException he) {
			entityManager.throwPersistenceException(he);
		}
	}

	public void commit() {
		if ( tx == null || !tx.isActive() ) {
			throw new IllegalStateException( "Transaction not active" );
		}
		if (rollbackOnly) {
			tx.rollback();
			throw new RollbackException("Transaction marked as rollbackOnly");
		}
		try {
			tx.commit();
		}
		catch (Exception e) {
			try {
				//as per the spec we should rollback if commit fails
				tx.rollback();
			}
			catch (Exception re) {
				//swallow
			}
			throw new RollbackException( "Error while commiting the transaction", e);
		}
		finally {
			rollbackOnly = false;
		}
		//if closed and we commit, the mode should have been adjusted already
		//if ( entityManager.isOpen() ) entityManager.adjustFlushMode();
	}

	public void rollback() {
		if ( tx == null || !tx.isActive() ) {
			throw new IllegalStateException( "Transaction not active" );
		}
		try {
			tx.rollback();
		}
		catch (Exception e) {
			throw new PersistenceException("unexpected error when rollbacking", e);
		}
		finally {
			rollbackOnly = false;
		}
		//if ( entityManager.isOpen() ) entityManager.adjustFlushMode();
	}

	public void setRollbackOnly() {
		if ( ! isActive() ) throw new IllegalStateException( "Transaction not active" );
		this.rollbackOnly = true;
	}

	public boolean getRollbackOnly() {
		if ( ! isActive() ) throw new IllegalStateException( "Transaction not active" );
		return rollbackOnly;
	}

	public boolean isActive() {
		try {
			return tx != null && tx.isActive();
		}
		catch (RuntimeException e) {
			throw new PersistenceException("unexpected error when checking transaction status", e);
		}
	}

}
