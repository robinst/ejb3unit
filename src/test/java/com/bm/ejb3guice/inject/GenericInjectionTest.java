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

import java.util.Arrays;
import java.util.List;

import com.bm.ejb3guice.inject.BinderImpl;
import com.bm.ejb3guice.inject.CreationException;
import com.bm.ejb3guice.inject.Injector;
import com.bm.ejb3guice.inject.TypeLiteral;

import junit.framework.TestCase;

/**
 * @author crazybob@google.com (Bob Lee)
 */
public class GenericInjectionTest extends TestCase {

  public void testGenericInjection() throws CreationException {
    List<String> names = Arrays.asList("foo", "bar", "bob");
    BinderImpl builder = new BinderImpl();
    builder.bind(new TypeLiteral<List<String>>() {}).toInstance(names);
    Injector injector = builder.createInjector();
    Foo foo = injector.getInstance(Foo.class);
    assertEquals(names, foo.names);
  }

  static class Foo {
    @Inject List<String> names;
  }
}