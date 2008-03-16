package com.bm.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import junit.framework.TestCase;

import com.bm.ejb3data.bo.IMySessionBean;
import com.bm.ejb3data.bo.MySessionBean;
import com.bm.ejb3data.bo.StockWKNBo;
import com.bm.ejb3guice.inject.Inject;
import com.bm.utils.injectinternal.InternalInjector;

/**
 * Tests the automatic binding of classes defined in ejb3unit properties.
 * @author Daniel
 *
 */
public class Ejb3UnitJndiBinderTest extends TestCase {
	
	@Inject
	private EntityManager em;
	
	/**
	 * Testmethod.
	 * @throws NamingException in error case
	 */
	public void testBinding() throws NamingException{
		InternalInjector.createInternalInjector(StockWKNBo.class).injectMembers(this);
		Ejb3UnitJndiBinder binder=new Ejb3UnitJndiBinder(em);
		binder.bind();
		
		final InitialContext ctx=new InitialContext();
		MySessionBean fromJndi=(MySessionBean)ctx.lookup("ejb/MySessionBean");
		assertNotNull(fromJndi);
		assertNotNull(fromJndi.getDs());
		
		//lokup interface
		IMySessionBean fromJndiInter=(IMySessionBean)ctx.lookup("ejb/MySessionBean");
		assertNotNull(fromJndiInter);
		assertNotNull(fromJndiInter.getDs());
	}

}
