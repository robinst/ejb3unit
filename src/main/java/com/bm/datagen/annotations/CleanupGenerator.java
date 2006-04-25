package com.bm.datagen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a method is anotated with this annotation the methos get called in the
 * tearDown method of the JUnit test.
 * 
 * @author Daniel Wiese
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CleanupGenerator {

}
