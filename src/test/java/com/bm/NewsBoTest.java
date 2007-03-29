package com.bm;

import com.bm.data.bo.NewsBo;
import com.bm.datagen.Generator;
import com.bm.testsuite.BaseEntityFixture;

/**
 * Test case for the Ejb3Unit framework.
 * 
 * @author Daniel Wiese
 * @since 15.10.2005
 */
public class NewsBoTest extends BaseEntityFixture<NewsBo> {

	private static final Generator[] SPECIAL_GENERATORS = { new ConstantIntegerGenerator(
			870737) };

	/**
	 * Constructor.
	 */
	public NewsBoTest() {
		super(NewsBo.class, SPECIAL_GENERATORS);
	}

}
