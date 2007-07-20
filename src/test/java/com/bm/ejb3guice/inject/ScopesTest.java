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

package com.bm.ejb3guice.inject;

import com.bm.ejb3guice.inject.BinderImpl;
import com.bm.ejb3guice.inject.BindingBuilderImpl;
import com.bm.ejb3guice.inject.CreationException;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.Scopes;

import junit.framework.TestCase;

/**
 * @author crazybob@google.com (Bob Lee)
 */
public class ScopesTest extends TestCase {

  public void testSingletonAnnotation() throws CreationException {
    BinderImpl binder = new BinderImpl();
    BindingBuilderImpl<SampleSingleton> bindingBuilder
        = binder.bind(SampleSingleton.class);
    Injector injector = binder.createInjector();
    assertSame(
        injector.getInstance(SampleSingleton.class),
        injector.getInstance(SampleSingleton.class));
  }

  @Singleton
  static class SampleSingleton {}

  public void testOverriddingAnnotation()
      throws CreationException {
    BinderImpl binder = new BinderImpl();
    binder.bind(SampleSingleton.class).in(Scopes.NO_SCOPE);
    Injector injector = binder.createInjector();
    assertNotSame(
        injector.getInstance(SampleSingleton.class),
        injector.getInstance(SampleSingleton.class));
  }
}