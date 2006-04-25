package org.hibernate.annotationfactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Creates live annotations (actually <code>AnnotationProxies</code>) from <code>AnnotationDescriptors</code>.
 * 
 * @see org.hibernate.annotationfactory.AnnotationProxy
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
public class AnnotationFactory {

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T create(AnnotationDescriptor descriptor) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T> proxyClass = (Class<T>) Proxy.getProxyClass(classLoader, descriptor.type());
        InvocationHandler handler = new AnnotationProxy(descriptor);
        try {
            return getProxyInstance(proxyClass, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static <T extends Annotation> T getProxyInstance(Class<T> proxyClass, InvocationHandler handler) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<T> constructor = proxyClass.getConstructor(new Class[] {InvocationHandler.class});
        return constructor.newInstance(new Object[] {handler});
    }
}
