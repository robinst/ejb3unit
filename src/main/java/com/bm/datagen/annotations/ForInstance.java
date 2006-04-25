package com.bm.datagen.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used in generators
 * if a generator need a reference to the entity bean instance
 * where this generator is used for.
 * @author Daniel Wiese
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForInstance {

}
