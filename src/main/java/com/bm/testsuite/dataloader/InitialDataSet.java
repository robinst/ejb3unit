package com.bm.testsuite.dataloader;

import javax.persistence.EntityManager;

/**
 * This interface must be implemented if the database should contain initial
 * data. These rows willinsertetinto the DB before every test and deleted after
 * the test.
 * 
 * @author Daniel Wiese
 * 
 */
public interface InitialDataSet {

	/**
	 * Creates the data.
	 */
	void create();

	/**
	 * Deletes the data.
	 * 
	 * @param em -
	 *            the enntity manager.
	 */
	void cleanup(EntityManager em);

}
