package com.bm.ejb3metadata.annotations.metadata;

import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.TransactionAttributeType;

import com.bm.ejb3metadata.annotations.InterceptorType;
import com.bm.ejb3metadata.annotations.JClassInterceptor;
import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.impl.JInterceptors;
import com.bm.ejb3metadata.annotations.metadata.interfaces.IEJBInterceptors;
import com.bm.ejb3metadata.annotations.metadata.interfaces.ITransactionAttribute;

/**
 * This class represents the annotation metadata of a method.
 * @author Daniel Wiese
 */
public class MethodAnnotationMetadata extends CommonAnnotationMetadata
    implements Cloneable, ITransactionAttribute, IEJBInterceptors {

    /**
     * Logger.
     */
    //private static JLog logger = JLogFactory.getLog(MethodAnnotationMetadata.class);

    /**
     * Method on which we got metadata.
     */
    private JMethod jMethod = null;

    /**
     * Parent metadata.
     */
    private ClassAnnotationMetadata classAnnotationMetadata = null;


    /**
     * Original parent metadata (if method is inherited).
     */
    private ClassAnnotationMetadata originalClassAnnotationMetadata = null;

    /**
     * Type of transaction.
     */
    private TransactionAttributeType transactionAttributeType = null;

    /**
     * &#64;{@link javax.ejb.Remove} annotation.
     */
    private Remove remove = null;

    /**
     * This method is a business method ?
     */
    private boolean businessMethod = false;

    /**
     * PostConstruct method ?
     */
    private boolean postConstruct = false;

    /**
     * PreDestroy method ?
     */
    private boolean preDestroy = false;

    /**
     * PostActivate method ?
     */
    private boolean postActivate = false;

    /**
     * PrePassivate method ?
     */
    private boolean prePassivate = false;

    /**
     * &#64;{@link javax.interceptor.AroundInvoke} method used by interceptors ?
     */
    private boolean aroundInvoke = false;

    /**
     *This method is a method from a super class ?<br>
     * This flag is used by enhancers
     */
    private boolean inherited = false;

    /**
     * &#64;{@link javax.interceptor.ExcludeClassInterceptors} method ?
     */
    private boolean excludeClassInterceptors = false;

    /**
     * &#64;{@link javax.ejb.Timeout} method ?
     */
    private boolean timeout = false;

    /**
     * EasyBeans global interceptors.<br>
     * These interceptors correspond to a list of interceptors
     * that need to be present first on this current method.
     */
    private List<JClassInterceptor> globalEasyBeansInterceptors = null;


    /**
     * EasyBeans method interceptors. These interceptors correspond to a list of Interceptors like security or transaction.
     */
    private List<JClassInterceptor> interceptors = null;

    /**
     * List of annotation interceptors.
     */
    private JInterceptors annotationInterceptors = null;


    /**
     * User interceptors. These interceptors correspond to a list of Interceptor that user has specified in its bean class.
     * Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt;
     */
    private Map<InterceptorType, List<JClassInterceptor>> userInterceptors = null;

    /**
     * Constructor.
     * @param jMethod the method on which we will set/add metadata
     * @param classAnnotationMetadata the parent metadata.
     */
    public MethodAnnotationMetadata(final JMethod jMethod, final ClassAnnotationMetadata classAnnotationMetadata) {
        this.jMethod = jMethod;
        this.classAnnotationMetadata = classAnnotationMetadata;
    }

    /**
     * @return name of the method
     */
    public String getMethodName() {
        return this.jMethod.getName();
    }

    /**
     * @return JMethod object
     */
    public JMethod getJMethod() {
        return this.jMethod;
    }

    /**
     * @return transaction Attribute type.
     * @see javax.ejb.TransactionAttributeType
     */
    public TransactionAttributeType getTransactionAttributeType() {
        return transactionAttributeType;
    }

    /**
     * Sets Transaction Attribute Type.
     * @see javax.ejb.TransactionAttributeType
     * @param transactionAttributeType the type of transaction.
     */
    public void setTransactionAttributeType(final TransactionAttributeType transactionAttributeType) {
        this.transactionAttributeType = transactionAttributeType;
    }

    /**
     * @return true if the method is a business method.
     */
    public boolean isBusinessMethod() {
        return businessMethod;
    }

    /**
     * This method is a business method.
     * @param flag true/false if method is a business method.
     */
    public void setBusinessMethod(final boolean flag) {
        this.businessMethod = flag;
    }

    /**
     * @return true if the method is a lifecycle method.
     */
    public boolean isLifeCycleMethod() {
        return  isPostActivate() || isPostConstruct() || isPreDestroy() || isPrePassivate();
    }

    /**
     * @return remove attributes  &#64;{@link javax.ejb.Remove} attributes
     */
    public Remove getJRemove() {
        return this.remove;
    }

    /**
     * Sets &#64;{@link javax.ejb.Remove} attribute.
     * @param remove contains the attribute with retainIfException.
     */
    public void setRemove(final Remove remove) {
        this.remove = remove;
    }



    /**
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String titleIndent = " ";

        // classname
        sb.append(titleIndent);
        sb.append(this.getClass().getName().substring(this.getClass().getPackage().getName().length() + 1));
        sb.append("[\n");

        // Add super class toString()
        sb.append(super.toString());

        // Method
        concatStringBuilder("jMethod", jMethod, sb);

        // inherited
        concatStringBuilder("inherited", Boolean.valueOf(inherited), sb);

        // transactionAttributeType
        concatStringBuilder("transactionAttributeType", transactionAttributeType, sb);

        // remove
        concatStringBuilder("remove", remove, sb);

        // businessMethod
        concatStringBuilder("businessMethod", Boolean.valueOf(businessMethod), sb);

        // aroundInvoke
        concatStringBuilder("aroundInvoke", Boolean.valueOf(aroundInvoke), sb);

        // postConstruct
        concatStringBuilder("postConstruct", Boolean.valueOf(postConstruct), sb);

        // preDestroy
        concatStringBuilder("preDestroy", Boolean.valueOf(preDestroy), sb);

        // postActivate
        concatStringBuilder("postActivate", Boolean.valueOf(postActivate), sb);

        // prePassivate
        concatStringBuilder("prePassivate", Boolean.valueOf(prePassivate), sb);

        // timeout
        concatStringBuilder("timeout", Boolean.valueOf(timeout), sb);

        // annotation Interceptors
        concatStringBuilder("annotationInterceptors", annotationInterceptors, sb);

        // interceptors
        concatStringBuilder("interceptors", interceptors, sb);

        sb.append(titleIndent);
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * @return true if method has  &#64;{@link javax.ejb.PostActivate}
     */
    public boolean isPostActivate() {
        return postActivate;
    }

    /**
     * Sets true if method has &#64;{@link javax.ejb.PostActivate}.
     * @param postActivate true/false.
     */
    public void setPostActivate(final boolean postActivate) {
        this.postActivate = postActivate;
    }

    /**
     * @return true if method has &#64;{@link javax.annotation.PostConstruct}.
     */
    public boolean isPostConstruct() {
        return postConstruct;
    }

    /**
     * Sets true if method has &#64;{@link javax.annotation.PostConstruct}.
     * @param postConstruct true/false.
     */
    public void setPostConstruct(final boolean postConstruct) {
        this.postConstruct = postConstruct;
    }

    /**
     * @return true if method has &#64;{@link javax.annotation.PreDestroy}.
     */

    public boolean isPreDestroy() {
        return preDestroy;
    }

    /**
     * Sets true if method has &#64;{@link javax.annotation.PreDestroy}.
     * @param preDestroy true/false.
     */
    public void setPreDestroy(final boolean preDestroy) {
        this.preDestroy = preDestroy;
    }

    /**
     * @return true if method has &#64;{@link javax.ejb.PrePassivate}.
     */

    public boolean isPrePassivate() {
        return prePassivate;
    }

    /**
     * Sets true if method has &#64;{@link javax.ejb.PrePassivate}.
     * @param prePassivate true/false.
     */
    public void setPrePassivate(final boolean prePassivate) {
        this.prePassivate = prePassivate;
    }

    /**
     * @return true if method has &#64;{@link javax.ejb.Timeout}.
     */
    public boolean isTimeout() {
        return timeout;
    }

    /**
     * Sets true if method has &#64;{@link javax.ejb.Timeout}.
     * @param timeout true/false.
     */
    public void setTimeout(final boolean timeout) {
        this.timeout = timeout;
    }

    /**
     * @return true if method has &#64;{@link javax.interceptor.AroundInvoke}.
     */
    public boolean isAroundInvoke() {
        return aroundInvoke;
    }

    /**
     * Sets true if method has &#64;{@link javax.interceptor.AroundInvoke}.
     * @param aroundInvoke true/false
     */
    public void setAroundInvoke(final boolean aroundInvoke) {
        this.aroundInvoke = aroundInvoke;
    }

    /**
     * @return true if this method is inherited from a super class
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Sets the inheritance of this method.
     * @param inherited true if method is from a super class
     * @param originalClassAnnotationMetadata the metadata of the original class (not inherited)
     */
    public void setInherited(final boolean inherited, final ClassAnnotationMetadata originalClassAnnotationMetadata) {
        this.inherited = inherited;
        this.originalClassAnnotationMetadata = originalClassAnnotationMetadata;
    }

    /**
     * @return true if this method won't use user interceptors.
     */
    public boolean isExcludedClassInterceptors() {
        return excludeClassInterceptors;
    }

    /**
     * Flag this method as a method which exclude user interceptors.
     * @param excludeClassInterceptors true if this method is a method which exclude user interceptors.
     */
    public void setExcludeClassInterceptors(final boolean excludeClassInterceptors) {
        this.excludeClassInterceptors = excludeClassInterceptors;
    }



    /**
     * @return parent metadata (class)
     */
    public ClassAnnotationMetadata getClassAnnotationMetadata() {
        return classAnnotationMetadata;
    }

    /**
     * @return original parent metadata (class) if inherited.
     */
    public ClassAnnotationMetadata getOriginalClassAnnotationMetadata() {
        return originalClassAnnotationMetadata;
    }

    /**
     * @return list of interceptors that enhancer will use. (ie : security/transaction)
     */
    public List<JClassInterceptor> getInterceptors() {
        return interceptors;
    }

    /**
     * Sets the list of interceptors(tx, security, etc) that enhancers will use.<br>
     * These interceptors are defined per methods.
     * @param interceptors list of interceptors that enhancer will use.
     */
    public void setInterceptors(final List<JClassInterceptor> interceptors) {
        this.interceptors = interceptors;
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
     * @return Map&lt;interceptor type &lt;--&gt; List of methods/class corresponding to the interceptor&gt;
     * of user interceptors that enhancer will use.
     */
    public Map<InterceptorType, List<JClassInterceptor>> getUserEasyBeansInterceptors() {
        return userInterceptors;
    }

    /**
     * Sets the list of user interceptors that enhancers will use.<br>
     * These interceptors are defined in bean class.
     * @param userInterceptors list of interceptors that enhancer will use.
     */
    public void setUserInterceptors(final Map<InterceptorType, List<JClassInterceptor>> userInterceptors) {
        this.userInterceptors = userInterceptors;
    }

    /**
     * @return list of global interceptors that enhancer will use. (ie : Remove interceptor)
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
     * @return a clone object.
     */
    @Override
    public Object clone() {
        MethodAnnotationMetadata newMethodAnnotationMetadata = new MethodAnnotationMetadata(jMethod, classAnnotationMetadata);
        newMethodAnnotationMetadata.setAnnotationsInterceptors(annotationInterceptors);
        newMethodAnnotationMetadata.setAroundInvoke(aroundInvoke);
        newMethodAnnotationMetadata.setBusinessMethod(businessMethod);
        newMethodAnnotationMetadata.setExcludeClassInterceptors(excludeClassInterceptors);
        newMethodAnnotationMetadata.setInherited(inherited, originalClassAnnotationMetadata);
        newMethodAnnotationMetadata.setInterceptors(interceptors);
        newMethodAnnotationMetadata.setJAnnotationResource(getJAnnotationResource());
        newMethodAnnotationMetadata.setJEjbEJB(getJEjbEJB());
        newMethodAnnotationMetadata.setJavaxPersistenceContext(getJavaxPersistenceContext());
        newMethodAnnotationMetadata.setJavaxPersistenceUnit(getJavaxPersistenceUnit());
        newMethodAnnotationMetadata.setPostActivate(postActivate);
        newMethodAnnotationMetadata.setPostConstruct(postConstruct);
        newMethodAnnotationMetadata.setPreDestroy(preDestroy);
        newMethodAnnotationMetadata.setPrePassivate(prePassivate);
        newMethodAnnotationMetadata.setRemove(remove);
        newMethodAnnotationMetadata.setTimeout(timeout);
        newMethodAnnotationMetadata.setTransactionAttributeType(transactionAttributeType);
        newMethodAnnotationMetadata.setUserInterceptors(userInterceptors);
        newMethodAnnotationMetadata.setGlobalEasyBeansInterceptors(globalEasyBeansInterceptors);
        return  newMethodAnnotationMetadata;
    }


    /**
     * Replace the link to the classannotation metadata.
     * @param classAnnotationMetadata new object for the link.
     */
    public void setClassAnnotationMetadata(final ClassAnnotationMetadata classAnnotationMetadata) {
        this.classAnnotationMetadata = classAnnotationMetadata;
    }
}
