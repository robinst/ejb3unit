/**
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.binder;

import com.bm.ejb3guice.inject.Key;
import com.bm.ejb3guice.inject.Provider;
import com.bm.ejb3guice.inject.TypeLiteral;

/**
 * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder {

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   */
  ScopedBindingBuilder to(Class<? extends T> implementation);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   */
  ScopedBindingBuilder to(TypeLiteral<? extends T> implementation);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   */
  ScopedBindingBuilder to(Key<? extends T> targetKey);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   *
   * @see com.bm.ejb3guice.inject.Injector#injectMembers
   */
  void toInstance(T instance);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   *
   * @see com.bm.ejb3guice.inject.Injector#injectMembers
   */
  ScopedBindingBuilder toProvider(Provider<? extends T> provider);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   */
  ScopedBindingBuilder toProvider(
      Class<? extends Provider<? extends T>> providerType);

  /**
   * See the EDSL examples at {@link com.bm.ejb3guice.inject.Binder}.
   */
  ScopedBindingBuilder toProvider(
      Key<? extends Provider<? extends T>> providerKey);
}
