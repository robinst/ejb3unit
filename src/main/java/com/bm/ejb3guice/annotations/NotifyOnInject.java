package com.bm.ejb3guice.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a class which wants to have informationes about, where it was injected.
 * 
 * @author wiesda00
 * 
 */
@Target( { TYPE })
@Retention(RUNTIME)
public @interface NotifyOnInject {

}
