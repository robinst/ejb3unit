package com.bm.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtilsBean;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.empty.EmptyCollection;
import com.bm.datagen.random.primitive.PrimitiveRandomBooleanGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomDateGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomDoubleGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomIntegerGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomLongGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomShortGenerator;
import com.bm.datagen.random.primitive.PrimitiveRandomStringGenerator;

/**
 * This class exectues a simple test of getter setters. All seeters with
 * primitive or basic type will be executed.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class SimpleGetterSetterTest extends Assert {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(SimpleGetterSetterTest.class);

	private static final List<Generator> DEFAULT_GENERATORS = new ArrayList<Generator>();

	private final Class toTestClass;

	private final PropertyUtilsBean propUtils = new PropertyUtilsBean();

	private final PropertyDescriptor[] properties;

	private final Object toTestObj;

	static {
		DEFAULT_GENERATORS.add(new PrimitiveRandomDateGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomBooleanGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomIntegerGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomLongGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomShortGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomStringGenerator());
		DEFAULT_GENERATORS.add(new PrimitiveRandomDoubleGenerator());
		DEFAULT_GENERATORS.add(new EmptyCollection());
	}

	/**
	 * Constructor.
	 * 
	 * @param toTest -
	 *            the bean to test.
	 */
	public SimpleGetterSetterTest(Object toTest) {
		this.toTestClass = toTest.getClass();
		properties = this.propUtils.getPropertyDescriptors(this.toTestClass);
		this.toTestObj = toTest;

	}

	/**
	 * Test all primitive getters/setters of the bean instance.
	 * 
	 * @author Daniel Wiese
	 * @since 17.04.2006
	 */
	public void testGetterSetter() {
		for (PropertyDescriptor current : properties) {
			if (current.getWriteMethod() != null
					&& current.getReadMethod() != null) {
				Class[] params = current.getWriteMethod().getParameterTypes();
				if (params.length == 1 && params[0] != null) {
					Class toCreate = Ejb3Utils.getNonPrimitiveType(params[0]);
					Object[] value = new Object[1];
					value[0] = this.getValue(toCreate);
					if (value[0] != null) {
						try {
							log.debug("Testing property (get-/set "
									+ current.getName() + "()) ...");
							// call the setter method
							current.getWriteMethod().invoke(this.toTestObj,
									value);

							// call the getter method
							Object result = current.getReadMethod().invoke(
									this.toTestObj, (Object[]) null);
							assertNotNull(
									"The result of getter-property ("
											+ current.getName()
											+ ") is null, but set.. was called (Expected: "
											+ value[0] + ")", result);
							assertEquals("The result of getter-property ("
									+ current.getName() + ") is (" + result
									+ ") but not (" + value[0] + ")", value[0],
									result);
						} catch (IllegalArgumentException e) {
							log.error("WrongArgument-Can't invoke the setter of property ("
									+ current.getName() + ")", e);
							fail("WrongArgument-Can't invoke the setter of property ("
									+ current.getName() + ")");
							throw new IllegalArgumentException(e);
						} catch (IllegalAccessException e) {
							log.error("WrongArgument-Can't invoke the setter of property ("
									+ current.getName() + ")", e);
							fail("WrongArgument-Can't invoke the setter of property ("
									+ current.getName() + ")");
							throw new IllegalArgumentException(e);
						} catch (InvocationTargetException e) {
							log.error("Can't invoke the setter of property ("
									+ current.getName() + ")", e);
							fail("Can't invoke the setter of property ("
									+ current.getName() + ")");
							throw new IllegalArgumentException(e);
						}

					}
				}
			}

		}
	}

	private Object getValue(Class type) {
		Object back = null;
		for (Generator current : DEFAULT_GENERATORS) {
			final GeneratorType gType = Ejb3Utils
					.getGeneratorTypeAnnotation(current);
			if (type.equals(Ejb3Utils.getNonPrimitiveType(gType.className()))) {
				back = current.getValue();
				break;
			}
		}

		return back;
	}

}
