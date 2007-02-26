package com.bm.ejb3metadata.annotations.metadata;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionManagementType.CONTAINER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.ApplicationException;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagementType;

import com.bm.ejb3metadata.annotations.ClassType;
import com.bm.ejb3metadata.annotations.InterceptorType;
import com.bm.ejb3metadata.annotations.JClassInterceptor;
import com.bm.ejb3metadata.annotations.JField;
import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.exceptions.InterceptorsValidationException;
import com.bm.ejb3metadata.annotations.impl.JAnnotationResource;
import com.bm.ejb3metadata.annotations.impl.JCommonBean;
import com.bm.ejb3metadata.annotations.impl.JEjbEJB;
import com.bm.ejb3metadata.annotations.impl.JInterceptors;
import com.bm.ejb3metadata.annotations.impl.JLocal;
import com.bm.ejb3metadata.annotations.impl.JMessageDriven;
import com.bm.ejb3metadata.annotations.impl.JRemote;
import com.bm.ejb3metadata.annotations.impl.JService;
import com.bm.ejb3metadata.annotations.impl.JStateful;
import com.bm.ejb3metadata.annotations.impl.JStateless;
import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceContext;
import com.bm.ejb3metadata.annotations.impl.JavaxPersistenceUnit;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IEJBInterceptors;
import com.bm.ejb3metadata.annotations.metadata.interfaces.ITransactionAttribute;


/**
 * This class represents the annotation metadata of a Bean.<br>
 * From this class, we can access to all methods of a bean with its associated information.
 * @author Daniel Wiese
 */
public class ClassAnnotationMetadata extends CommonAnnotationMetadata implements ITransactionAttribute, IEJBInterceptors {

   private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
		.getLogger(ClassAnnotationMetadata.class);
	
    /**
     * List of method annotations metadata.
     */
    private Map<JMethod, MethodAnnotationMetadata> methodsAnnotationMetadata = null;

    /**
     * List of field annotations metadata.
     */
    private Map<JField, FieldAnnotationMetadata> fieldsAnnotationMetadata = null;

    /**
     * Parent meta data.
     */
    private EjbJarAnnotationMetadata ejbJarAnnotationMetadata = null;

    /**
     * List of local interfaces.
     */
    private JLocal jLocal = null;

    /**
     * List of remote interfaces.
     */
    private JRemote jRemote = null;

    /**
     * CommonBean description.
     */
    private JCommonBean jCommonBean = null;

    /**
     * Message Driven attribute.
     */
    private JMessageDriven jMessageDriven = null;

    /**
     * Stateless attribute.
     */
    private JStateless jStateless = null;

    /**
     * Stateful attribute.
     */
    private JStateful jStateful = null;
    
    /**
     * Servie attribute (singelton).
     */
    private JService jService = null;


    /**
     * Local Home.
     */
    private String localHome = null;

    /**
     * Remote Home.
     */
    private String remoteHome = null;

    /**
     * List of annotation interceptors.
     */
    private JInterceptors annotationInterceptors = null;

    /**
     * EasyBeans global interceptors.<br>
     * These interceptors correspond to a list of interceptors
     * that need to be present first on all methods.
     */
    private List<JClassInterceptor> globalEasyBeansInterceptors = null;

    /**
     * User interceptors. These interceptors correspond to a list of Interceptor that user has specified in its bean class.
     * It is the interceptors defined in interceptor classes, not the bean class itself.
     * Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt;
     */
    private Map<InterceptorType, List<JClassInterceptor>> externalUserInterceptors = null;

    /**
     * User interceptors. These interceptors correspond to a list of Interceptor that user has specified in the bean class.
     * It is not defined in separated classes.
     * Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt;
     */
    private Map<InterceptorType, List<JClassInterceptor>> internalUserInterceptors = null;


    /**
     * Transaction management type (default = container).
     */
    private TransactionManagementType transactionManagementType = CONTAINER;

    /**
     * Transaction attribute type (default = required).
     */
    private TransactionAttributeType transactionAttributeType = REQUIRED;

    /**
     * Application exception annotation.
     */
    private ApplicationException applicationException = null;

    /**
     * Superclass name.
     */
    private String superName = null;

    /**
     * Interfaces of this clas.
     */
    private String[] interfaces = null;

    /**
     * The type of the class.
     * @see ClassType
     */
    private ClassType classType = null;

    /**
     * Name of the class associated to this metadata.
     */
    private String className = null;

    /**
     * List of &#64;{@link javax.interceptor.AroundInvoke} methods on this
     * class (should be only one per class, validating occurs after).
     */
    private List<MethodAnnotationMetadata> aroundInvokeMethodsMetadata = null;

    /**
     * Object representing &#64;{@link javax.ejb.EJBs} annotation.
     */
    private List<JEjbEJB> jEjbEJBs = null;

    /**
     * Object representing &#64;{@link javax.annotation.Resources} annotation.
     */
    private List<JAnnotationResource> jAnnotationResources = null;

    /**
     * Object representing &#64;{@link javax.persistence.PersistenceContext} annotation.
     */
    private List<JavaxPersistenceContext> javaxPersistencePersistenceContexts = null;

    /**
     * Object representing &#64;{@link javax.persistence.PersistenceUnit} annotation.
     */
    private List<JavaxPersistenceUnit> javaxPersistencePersistenceUnits = null;

    /**
     * Methods used for &#64;{@link javax.annotation.PostConstruct} on this
     * class (only one per class but may be defined in super classes).
     */
    private LinkedList<MethodAnnotationMetadata> postConstructMethodsMetadata = null;

    /**
     * Methods used for &#64;{@link javax.annotation.PreDestroy} on this class
     * (only one per class but may be defined in super classes).
     */
    private LinkedList<MethodAnnotationMetadata> preDestroyMethodsMetadata = null;

    /**
     * Methods used for &#64;{@link javax.ejb.PostActivate} on this class (only
     * one per class but may be defined in super classes).
     */
    private LinkedList<MethodAnnotationMetadata> postActivateMethodsMetadata = null;

    /**
     * Methods used for &#64;{@link javax.ejb.PrePassivate} on this class (only
     * one per class but may be defined in super classes).
     */
    private LinkedList<MethodAnnotationMetadata> prePassivateMethodsMetadata = null;

    /**
     * Is that the class represented by this metadata has already been modified ?
     */
    private boolean modified = false;

    /**
     * Constructor.
     * @param className name of the class associated to these metadatas.
     * @param ejbJarAnnotationMetadata parent metadata object.
     */
    public ClassAnnotationMetadata(final String className, final EjbJarAnnotationMetadata ejbJarAnnotationMetadata) {
        this.className = className;
        this.methodsAnnotationMetadata = new HashMap<JMethod, MethodAnnotationMetadata>();
        this.fieldsAnnotationMetadata = new HashMap<JField, FieldAnnotationMetadata>();
        this.ejbJarAnnotationMetadata = ejbJarAnnotationMetadata;
        this.postConstructMethodsMetadata = new LinkedList<MethodAnnotationMetadata>();
        this.preDestroyMethodsMetadata = new LinkedList<MethodAnnotationMetadata>();
        this.postActivateMethodsMetadata = new LinkedList<MethodAnnotationMetadata>();
        this.prePassivateMethodsMetadata = new LinkedList<MethodAnnotationMetadata>();
    }

    /**
     * @return name of the bean (associated to this metadata).
     */
    public String getClassName() {
        return className;
    }

    /**
     * Add method annotation metadata for a given Bean.
     * @param methodAnnotationMetadata metadata of a method.
     */
    public void addMethodAnnotationMetadata(final MethodAnnotationMetadata methodAnnotationMetadata) {
        JMethod key = methodAnnotationMetadata.getJMethod();
        // already exists ?
        if (methodsAnnotationMetadata.containsKey(key)) {
            String msg = "BeanAnnotationMetadata.addMethodAnnotationMetadata.alreadyPresent";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        methodsAnnotationMetadata.put(key, methodAnnotationMetadata);
    }

    /**
     * @param jMethod key of the map of methods annotations.
     * @return method annotation metadata of a given method.
     */
    public MethodAnnotationMetadata getMethodAnnotationMetadata(final JMethod jMethod) {
        return methodsAnnotationMetadata.get(jMethod);
    }

    /**
     * Get collections of methods annotation metadata.
     * @return collections of methods annotation metadata.
     */
    public Collection<MethodAnnotationMetadata> getMethodAnnotationMetadataCollection() {
        return methodsAnnotationMetadata.values();
    }


    /**
     * Add field annotation metadata for a given Bean.
     * @param fieldAnnotationMetadata metadata of a field.
     */
    public void addFieldAnnotationMetadata(final FieldAnnotationMetadata fieldAnnotationMetadata) {
        JField key = fieldAnnotationMetadata.getJField();
        // already exists ?
        if (fieldsAnnotationMetadata.containsKey(key)) {
            String msg = "BeanAnnotationMetadata.addFieldAnnotationMetadata.alreadyPresent";
            logger.debug(msg);
            throw new IllegalStateException(msg);
        }
        fieldsAnnotationMetadata.put(key, fieldAnnotationMetadata);
    }

    /**
     * key of the map of fields annotations.
     * @param jField key of the map of fields annotations.
     * @return field annotation metadata of a given method.
     */
    public FieldAnnotationMetadata getFieldAnnotationMetadata(final JField jField) {
        return fieldsAnnotationMetadata.get(jField);
    }

    /**
     * Get collections of fields annotation metadata.
     * @return collections of fields annotation metadata.
     */
    public Collection<FieldAnnotationMetadata> getFieldAnnotationMetadataCollection() {
        return fieldsAnnotationMetadata.values();
    }


    /**
     * Sets the local interfaces of this class.
     * @param jLocal list of interfaces.
     */
    public void setLocalInterfaces(final JLocal jLocal) {
        this.jLocal = jLocal;
    }

    /**
     * Sets the remote interfaces of this class.
     * @param jRemote list of interfaces.
     */
    public void setRemoteInterfaces(final JRemote jRemote) {
        this.jRemote = jRemote;
    }


    /**
     * @return the local interfaces of this class.
     */
    public JLocal getLocalInterfaces() {
       return jLocal;
    }

    /**
     * @return the remote interfaces of this class.
     */
    public JRemote getRemoteInterfaces() {
        return jRemote;
    }

      /**
     * @return true if the class is a stateless class
     */
    public boolean isStateless() {
        return (classType != null && classType == ClassType.STATELESS);
    }

    /**
     * @return true if the class is a stateful class
     */
    public boolean isStateful() {
        return (classType != null && classType == ClassType.STATEFUL);
    }

    /**
     * @return true if the class is a session bean class
     */
    public boolean isSession() {
        return (classType != null && (classType == ClassType.STATELESS || classType == ClassType.STATEFUL));
    }

    /**
     * @return true if the class is an MDB class
     */
    public boolean isMdb() {
        return (classType != null && classType == ClassType.MDB);
    }
    
    /**
     * @return true if the class is an MDB class
     */
    public boolean isService() {
        return (classType != null && classType == ClassType.SERVICE);
    }

    /**
     * Sets the type of this class.
     * @param cType a type from enum class ClassType.
     * @see com.bm.ejb3metadata.annotations.ClassType
     */
    public void setClassType(final ClassType cType) {
        this.classType = cType;
    }

    /**
     * @return Message driven attribute.
     */
    public JMessageDriven getJMessageDriven() {
        return jMessageDriven;
    }

    /**
     * Sets the message driven bean object.
     * @param messageDriven attributes of message driven bean.
     */
    public void setJMessageDriven(final JMessageDriven messageDriven) {
        jMessageDriven = messageDriven;
    }


    /**
     * @return string representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // classname
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        sb.append("[\n");

        // Add super class toString()
        sb.append(super.toString());

        // Class name
        concatStringBuilder("className", className, sb);

        // superclass name
        concatStringBuilder("superName", superName, sb);

        // interfaces
        concatStringBuilder("interfaces", interfaces, sb);

        // classType
        concatStringBuilder("classType", classType, sb);

        // jLocal
        concatStringBuilder("jLocal", jLocal, sb);

        // aroundInvokeMethodMetadatas
        concatStringBuilder("aroundInvokeMethodsMetadata", aroundInvokeMethodsMetadata, sb);

        // jRemote
        concatStringBuilder("jRemote", jRemote, sb);

        // jMessageDriven
        concatStringBuilder("jMessageDriven", jMessageDriven, sb);

        // remoteHome
        concatStringBuilder("remoteHome", remoteHome, sb);

        // localHome
        concatStringBuilder("localHome", localHome, sb);

        // transactionManagementType
        concatStringBuilder("transactionManagementType", transactionManagementType, sb);

        // transactionAttributeType
        concatStringBuilder("transactionAttributeType", transactionAttributeType, sb);

        // annotation Interceptors
        concatStringBuilder("annotationInterceptors", annotationInterceptors, sb);

        // jEjbEJBs
        concatStringBuilder("jAnnotationEJBs", jEjbEJBs, sb);

        // jAnnotationResources
        concatStringBuilder("jAnnotationResources", jAnnotationResources, sb);

        // javaxPersistencePersistenceContexts
        concatStringBuilder("javaxPersistencePersistenceContexts", javaxPersistencePersistenceContexts, sb);

        // javaxPersistencePersistenceUnits
        concatStringBuilder("javaxPersistencePersistenceUnits", javaxPersistencePersistenceUnits, sb);


        // Methods
        for (MethodAnnotationMetadata methodAnnotationMetadata : getMethodAnnotationMetadataCollection()) {
            concatStringBuilder("methods", methodAnnotationMetadata, sb);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * @return the &#64;{@link javax.ejb.RemoteHome} class name.
     */
    public String getRemoteHome() {
        return remoteHome;
    }

    /**
     * Sets the &#64;{@link javax.ejb.RemoteHome} class name.
     * @param remoteHome the class name.
     */
    public void setRemoteHome(final String remoteHome) {
        this.remoteHome = remoteHome;
    }

    /**
     * @return the &#64;{@link javax.ejb.LocalHome} class name.
     */
    public String getLocalHome() {
        return localHome;
    }

    /**
     * Sets the &#64;{@link javax.ejb.LocalHome} class name.
     * @param localHome the class name.
     */
    public void setLocalHome(final String localHome) {
        this.localHome = localHome;
    }

    /**
     * @return transaction management type from @see TransactionManagementType.
     */
    public TransactionManagementType getTransactionManagementType() {
        return transactionManagementType;
    }

    /**
     * Sets transaction management type.
     * @see javax.ejb.TransactionManagementType
     * @param transactionManagementType value.
     *      (BEAN, CONTAINER)
     */
    public void setTransactionManagementType(final TransactionManagementType transactionManagementType) {
        this.transactionManagementType = transactionManagementType;
    }

    /**
     * @return transaction Attribute type.
     * @see javax.ejb.TransactionAttributeType
     */
    public TransactionAttributeType getTransactionAttributeType() {
        return transactionAttributeType;
    }

    /**
     * Set Transaction Attribute Type.
     * @see javax.ejb.TransactionAttributeType
     * @param transactionAttributeType the type of transaction.
     */
    public void setTransactionAttributeType(final TransactionAttributeType transactionAttributeType) {
        this.transactionAttributeType = transactionAttributeType;
    }

    /**
     * @return object representing list of &#64;{@link javax.interceptor.Interceptors}.
     */
    public JInterceptors getAnnotationInterceptors() {
        return annotationInterceptors;
    }

    /**
     * Sets the object representing the &#64;{@link javax.interceptor.Interceptors} annotation.
     * @param annotationInterceptors list of classes
     */
    public void setAnnotationsInterceptors(final JInterceptors annotationInterceptors) {
        this.annotationInterceptors = annotationInterceptors;
    }

    /**
     * @return the &#64;{@link javax.ejb.ApplicationException} annotation.
     */
    public ApplicationException getApplicationException() {
        return applicationException;
    }

    /**
     * Sets the object representing the &#64;{@link javax.ejb.ApplicationException} annotation.
     * @param applicationException object representation
     */
    public void setApplicationException(final ApplicationException applicationException) {
        this.applicationException = applicationException;
    }

    /**
     * @return true if the classs is a Bean
     */
    public boolean isBean() {
        return isStateless() || isStateful() || isMdb() || isService();
    }

    /**
     * @return array of interfaces name.
     */
    public String[] getInterfaces() {
        return interfaces;
    }

    /**
     * Sets the interfaces of this class.
     * @param interfaces name of interfaces.
     */
    public void setInterfaces(final String[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * @return the super class name.
     */
    public String getSuperName() {
        return superName;
    }

    /**
     * Sets the super class name.
     * @param superName name of the super class.
     */
    public void setSuperName(final String superName) {
        this.superName = superName;
    }

    /**
     * @return parent metadata object.
     */
    public EjbJarAnnotationMetadata getEjbJarAnnotationMetadata() {
        return ejbJarAnnotationMetadata;
    }

    /**
     * @return Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt; (interceptor classes)
     * of user interceptors that enhancer will use.
     */
    public Map<InterceptorType, List<JClassInterceptor>> getExternalUserEasyBeansInterceptors() {
        return externalUserInterceptors;
    }

    /**
     * Sets the list of user interceptors that enhancers will use.<br>
     * These interceptors are defined outside the bean class (interceptor classes).
     * @param externalUserInterceptors list of interceptors that enhancer will use.
     */
    public void setExternalUserInterceptors(final Map<InterceptorType, List<JClassInterceptor>> externalUserInterceptors) {
        this.externalUserInterceptors = externalUserInterceptors;
    }


    /**
     * @return Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt; (bean classes)
     * of user interceptors that enhancer will use.
     */
    public Map<InterceptorType, List<JClassInterceptor>> getInternalUserEasyBeansInterceptors() {
        return internalUserInterceptors;
    }

    /**
     * Sets the list of user interceptors that enhancers will use.<br>
     * These interceptors are defined in bean class.
     * @param internalUserInterceptors list of interceptors that enhancer will use.
     */
    public void setInternalUserInterceptors(final Map<InterceptorType, List<JClassInterceptor>> internalUserInterceptors) {
        this.internalUserInterceptors = internalUserInterceptors;
    }

    /**
     * @return list of global interceptors that enhancer will use. (ie : ENC)
     */
    public List<JClassInterceptor> getGlobalEasyBeansInterceptors() {
        return globalEasyBeansInterceptors;
    }

    /**
     * Sets the list of global interceptors that enhancers will use.
     * @param globalEasyBeansInterceptors list of interceptors that enhancer will use.
     */
    public void setGlobalEasyBeansInterceptors(final List<JClassInterceptor> globalEasyBeansInterceptors) {
        this.globalEasyBeansInterceptors = globalEasyBeansInterceptors;
    }

    /**
     * @return the method metadata with annotation &#64;{@link javax.interceptor.AroundInvoke}.
     */
    public boolean isAroundInvokeMethodMetadata() {
        return (aroundInvokeMethodsMetadata != null);
    }


    /**
     * @return the list of methods metadata with annotation &#64;{@link javax.interceptor.AroundInvoke}.
     */
    public List<MethodAnnotationMetadata> getAroundInvokeMethodMetadatas() {
        return aroundInvokeMethodsMetadata;
    }

    /**
     * Add a &#64;{@link javax.interceptor.AroundInvoke} method of this class.
     * @param aroundInvokeMethodMetadata the method.
     */
    public void addAroundInvokeMethodMetadata(final MethodAnnotationMetadata aroundInvokeMethodMetadata) {
        if (aroundInvokeMethodsMetadata == null) {
            this.aroundInvokeMethodsMetadata = new ArrayList<MethodAnnotationMetadata>();
        }
        aroundInvokeMethodsMetadata.add(aroundInvokeMethodMetadata);
    }

    /**
     * @return the methods metadata with annotation &#64;{@link javax.annotation.PostConstruct}.
     */
    public LinkedList<MethodAnnotationMetadata> getPostConstructMethodsMetadata() {
        return postConstructMethodsMetadata;
    }

    /**
     * Adds a &#64;{@link javax.annotation.PostConstruct} method of this class.
     * @param postConstructMethodMetadata the method.
     */
    public void addPostConstructMethodMetadata(final MethodAnnotationMetadata postConstructMethodMetadata) {
      checkLifeCycleDuplicate(postConstructMethodMetadata, InterceptorType.POST_CONSTRUCT, getPostConstructMethodsMetadata());
      this.postConstructMethodsMetadata.addFirst(postConstructMethodMetadata);
    }

    /**
     * Checks that only method at one level of a class is present.
     * @param postConstructMethodMetadata method to check
     * @param itcType the type of interceptor (used for the error)
     * @param existingList current list of methods
     */
    private void checkLifeCycleDuplicate(final MethodAnnotationMetadata postConstructMethodMetadata,
            final InterceptorType itcType, final List<MethodAnnotationMetadata> existingList) {
        // First case : not inherited
        ClassAnnotationMetadata wantToAddClassMetadata = postConstructMethodMetadata.getClassAnnotationMetadata();
        if (postConstructMethodMetadata.isInherited()) {
            wantToAddClassMetadata = postConstructMethodMetadata.getOriginalClassAnnotationMetadata();
        }
        for (MethodAnnotationMetadata method : existingList) {
            ClassAnnotationMetadata compareMetaData;
            if (method.isInherited()) {
                compareMetaData = method.getOriginalClassAnnotationMetadata();
            } else {
                compareMetaData = method.getClassAnnotationMetadata();
            }
            if (compareMetaData.equals(wantToAddClassMetadata)) {
                throw new InterceptorsValidationException("Class " + getClassName()
                        + " has already a " + itcType + " method which is "
                        + method.getMethodName() + ", cannot set new method "
                        + postConstructMethodMetadata.getMethodName());
            }
        }
    }


    /**
     * @return the methods metadata with annotation &#64;{@link javax.annotation.PreDestroy}.
     */
    public LinkedList<MethodAnnotationMetadata> getPreDestroyMethodsMetadata() {
        return preDestroyMethodsMetadata;
    }

    /**
     * Adds a &#64;{@link javax.annotation.PreDestroy} method of this class.
     * @param preDestroyMethodMetadata the method.
     */
    public void addPreDestroyMethodMetadata(final MethodAnnotationMetadata preDestroyMethodMetadata) {
        checkLifeCycleDuplicate(preDestroyMethodMetadata, InterceptorType.PRE_DESTROY, getPreDestroyMethodsMetadata());
        this.preDestroyMethodsMetadata.addFirst(preDestroyMethodMetadata);
    }


    /**
     * @return the methods metadata with annotation &#64;{@link javax.ejb.PostActivate}.
     */
    public LinkedList<MethodAnnotationMetadata> getPostActivateMethodsMetadata() {
        return postActivateMethodsMetadata;
    }

    /**
     * Adds a &#64;{@link javax.ejb.PostActivate} method of this class.
     * @param postActivateMethodMetadata the method.
     */
    public void addPostActivateMethodMetadata(final MethodAnnotationMetadata postActivateMethodMetadata) {
        checkLifeCycleDuplicate(postActivateMethodMetadata, InterceptorType.POST_ACTIVATE, getPostActivateMethodsMetadata());
        this.postActivateMethodsMetadata.addFirst(postActivateMethodMetadata);
    }


    /**
     * @return the method metadata with annotation &#64;{@link javax.ejb.PrePassivate}.
     */
    public LinkedList<MethodAnnotationMetadata> getPrePassivateMethodsMetadata() {
        return prePassivateMethodsMetadata;
    }

    /**
     * Adds a &#64;{@link javax.ejb.PrePassivate} method of this class.
     * @param prePassivateMethodMetadata the method.
     */
    public void addPrePassivateMethodMetadata(final MethodAnnotationMetadata prePassivateMethodMetadata) {
        checkLifeCycleDuplicate(prePassivateMethodMetadata, InterceptorType.PRE_PASSIVATE, getPrePassivateMethodsMetadata());
        this.prePassivateMethodsMetadata.addFirst(prePassivateMethodMetadata);
    }


    /**
     * Is that this class is an interceptor class ?
     * @return true if it the case, else false.
     */
    public boolean isInterceptor() {
        return (aroundInvokeMethodsMetadata != null && aroundInvokeMethodsMetadata.size() > 0)
        || (postConstructMethodsMetadata != null && postConstructMethodsMetadata.size() > 0)
        || (preDestroyMethodsMetadata != null && preDestroyMethodsMetadata.size() > 0)
        || (prePassivateMethodsMetadata != null && prePassivateMethodsMetadata.size() > 0)
        || (postActivateMethodsMetadata != null && postActivateMethodsMetadata.size() > 0);
    }


    /**
     * @return jEjbEJBs list representing &#64;{@link javax.ejb.EJBs} annotation.
     */
    public List<JEjbEJB> getJEjbEJBs() {
        return jEjbEJBs;
    }

    /**
     * Set JEjbEJBs object.
     * @param jEjbEJBs list representing javax.ejb.EJBs annotation.
     */
    public void setJEjbEJBs(final List<JEjbEJB> jEjbEJBs) {
        this.jEjbEJBs = jEjbEJBs;
    }


    /**
     * @return JAnnotationResources list representing &#64;{@link javax.annotation.Resources} annotation.
     */
    public List<JAnnotationResource> getJAnnotationResources() {
        return jAnnotationResources;
    }

    /**
     * Sets JAnnotationResources object.
     * @param jAnnotationResources list representing javax.annotation.Resources annotation.
     */
    public void setJAnnotationResources(final List<JAnnotationResource> jAnnotationResources) {
        this.jAnnotationResources = jAnnotationResources;
    }

    /**
     * @return javaxPersistencePersistenceContexts list representing &#64;{@link javax.persistence.PersistenceContexts}
     *         annotation.
     */
    public List<JavaxPersistenceContext> getJavaxPersistencePersistenceContexts() {
        return javaxPersistencePersistenceContexts;
    }

    /**
     * Sets JavaxPersistencePersistenceContexts object.
     * @param javaxPersistencePersistenceContexts list representing &#64;{@link javax.persistence.PersistenceContexts} annotation.
     */
    public void setJavaxPersistencePersistenceContexts(final List<JavaxPersistenceContext> javaxPersistencePersistenceContexts) {
        this.javaxPersistencePersistenceContexts = javaxPersistencePersistenceContexts;
    }

    /**
     * @return javaxPersistencePersistenceUnits list representing &#64;{@link javax.persistence.PersistenceUnits} annotation.
     */
    public List<JavaxPersistenceUnit> getJavaxPersistencePersistenceUnits() {
        return javaxPersistencePersistenceUnits;
    }

    /**
     * Sets setJavaxPersistencePersistenceUnits object.
     * @param javaxPersistencePersistenceUnits list representing &#64;{@link javax.persistence.PersistenceUnits} annotation.
     */
    public void setJavaxPersistencePersistenceUnits(final List<JavaxPersistenceUnit> javaxPersistencePersistenceUnits) {
        this.javaxPersistencePersistenceUnits = javaxPersistencePersistenceUnits;
    }

    /**
     * @return the attributes for a Stateless/Stateful/MDB
     */
    public JCommonBean getJCommonBean() {
        return jCommonBean;
    }

    /**
     * Sets the attributes for a Stateless/Stateful/MDB.
     * @param commonBean the attributes
     */
    public void setJCommonBean(final JCommonBean commonBean) {
        jCommonBean = commonBean;
    }

    /**
     * @return the attributes for a Stateful
     */
    public JStateful getJStateful() {
        return jStateful;
    }

    /**
     * Sets the attributes for a Stateful.
     * @param jStateful the attributes
     */
    public void setJStateful(final JStateful jStateful) {
        this.jStateful = jStateful;
    }

    /**
     * @return the attributes for a Stateless
     */
    public JStateless getJStateless() {
        return jStateless;
    }
    

    /**
     * Sets the attributes for a Stateless.
     * @param jStateless the attributes
     */
    public void setJStateless(final JStateless jStateless) {
        this.jStateless = jStateless;
    }
    
    
    /**
	 * @return the jService
	 */
	public JService getJService() {
		return jService;
	}

	/**
	 * Sets the attributes for a Stateless.
	 * @param service the jService to set
	 */
	public void setJService(final JService service) {
		jService = service;
	}

	/**
     * @return true if the class has been modified.
     */
    public boolean wasModified() {
        return modified;
    }

    /**
     * Defines that this class has been modified.
     */
    public void setModified() {
        this.modified = true;
    }


}
