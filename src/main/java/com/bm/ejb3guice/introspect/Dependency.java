package com.bm.ejb3guice.introspect;

import com.bm.ejb3guice.inject.Key;

/**
 * Javadoc.
 *
 * @author Kevin Bourrillion (kevinb9n@gmail.com)
 */
public interface Dependency<T> {

  Implementation<?> getDependent();

  Key<T> getKey();

  boolean usesProviderInjection();

  InjectionStyle getInjectionStyle();

  // put this enum somewhere else?
  enum InjectionStyle { FIELD, METHOD, CONSTRUCTOR }
}