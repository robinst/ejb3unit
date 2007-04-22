package com.bm.cfg;

public class JndiProperty extends NestedProperty {

	/**
	 * Constructor.
	 */
	public JndiProperty() {
		super("isSessionBean", "jndiName", "className");
	}

	/**
	 * Gets the className.
	 * 
	 * @return the className
	 */
	public String getClassName() {
		return this.getString("className");
	}

	/**
	 * Gets the isSessionBean.
	 * 
	 * @return the isSessionBean
	 */
	public boolean isSessionBean() {
		return this.getBoolean("isSessionBean");
	}

	/**
	 * Gets the jndiName.
	 * 
	 * @return the jndiName
	 */
	public String getJndiName() {
		return this.getString("jndiName");
	}

}
