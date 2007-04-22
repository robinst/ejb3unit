package com.bm.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import com.bm.data.bo.IMySessionBean;
import com.bm.data.bo.MySessionBean;

/**
 * Tests the automatic binding of classes defined in ejb3unit properties.
 * @author Daniel
 *
 */
public class Ejb3UnitJndiBinderTest extends TestCase {
	
	/**
	 * Testmethod.
	 * @throws NamingException in error case
	 */
	public void testBinding() throws NamingException{
		Ejb3UnitJndiBinder binder=new Ejb3UnitJndiBinder();
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
