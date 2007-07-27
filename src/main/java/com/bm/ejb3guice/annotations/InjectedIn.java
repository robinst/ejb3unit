package com.bm.ejb3guice.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *Marker for a method whcih will be invoked on the injected object
 *if this object was somewhere injected (in annotated with notifyOnInject)
 * @author wiesda00
 *
 */
@Target({ METHOD})
@Retention(RUNTIME)
@Documented
public @interface InjectedIn {

}
