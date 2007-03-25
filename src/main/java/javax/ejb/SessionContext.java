package javax.ejb;

import javax.xml.rpc.handler.MessageContext;

/**
 * The SessionContext interface provides access to the runtime session context 
 * that the container provides for a session enterprise Bean instance. The 
 * container passes the SessionContext interface to an instance after the 
 * instance has been created. The session context remains associated with 
 * the instance for the lifetime of the instance.
 */
public interface SessionContext extends EJBContext
{
    /**
     * Obtain a reference to the EJB local object that is  
     * associated with the instance.
     *
     * <p> An instance of a session enterprise Bean can call this method
     * at anytime between the ejbCreate() and ejbRemove() methods, including
     * from within the ejbCreate() and ejbRemove() methods.
     *
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB local object currently associated with the instance.
     *
     * @exception IllegalStateException Thrown if the instance invokes this
     *    method while the instance is in a state that does not allow the
     *    instance to invoke this method, or if the instance does not have
     *    a local interface.
     */
    EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    /**
     * Obtain a reference to the EJB object that is currently associated with 
     * the instance.
     *
     * <p> An instance of a session enterprise Bean can call this method
     * at anytime between the ejbCreate() and ejbRemove() methods, including
     * from within the ejbCreate() and ejbRemove() methods.
     *
     * <p> An instance can use this method, for example, when it wants to
     * pass a reference to itself in a method argument or result.
     *
     * @return The EJB object currently associated with the instance.
     *
     * @exception IllegalStateException Thrown if the instance invokes this
     *    method while the instance is in a state that does not allow the
     *    instance to invoke this method, or if the instance does not have
     *    a remote interface.
     */
    EJBObject getEJBObject() throws IllegalStateException;

    /**
     * Obtain a reference to the JAX-RPC MessageContext. 
     *
     * <p> An instance of a stateless session bean can call this method
     * from any business method invoked through its web service
     * endpoint interface.
     *
     * @return The MessageContext for this web service invocation.
     *
     * @exception IllegalStateException Thrown if this method is invoked
     *    while the instance is in a state that does not allow access
     *    to this method.
     */
    MessageContext getMessageContext() throws IllegalStateException;

    /**
     * Obtain an object that can be used to invoke the current bean through
     * the given business interface.
     *
     * @param businessInterface One of the local business interfaces 
     *        or remote business interfaces for this session bean.
     *
     * @return The business object corresponding to the given business 
     *         interface.
     *
     * @exception IllegalStateException Thrown if this method is invoked 
     *         with an invalid business interface for the current bean.
     */
    <T> T getBusinessObject(Class<T> businessInterface) throws IllegalStateException;

    /**
     * Obtain the business interface through which the current business
     * method invocation was made. 
     *
     * @exception IllegalStateException Thrown if this method is called
     *       and the bean has not been invoked through a business interface.
     */
    Class getInvokedBusinessInterface() throws IllegalStateException;


}