package com.bm.jndi;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

public class JndiLookupTest extends TestCase {
	
	public void testAddAndLookup() throws NamingException{
		InitialContext ctx=new InitialContext();
		String testObjBinding = "JBNDi_Test_Bind";
		ctx.bind("TestObjekt", testObjBinding);
		assertEquals(ctx.lookup("TestObjekt"), testObjBinding);
		assertEquals(ctx.lookup("TestObjekt"), testObjBinding);
	}

}
