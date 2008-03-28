package com.bm.junit4;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.SingleBeanGenerator;
import com.bm.ejb3data.bo.Etablissement;
import com.bm.ejb3data.bo.Service;
import com.bm.ejb3data.bo.Societe;
import com.bm.testsuite.junit4.BaseEntityJunit4Fixture;

public class ServiceJUnit4Test extends BaseEntityJunit4Fixture<Service> {
	private static final Generator<?>[] SPECIAL_GENERATORS = {
			new EtablissementCreator(), new SocieteCreator() };

	/**
	 * 
	 * Constructor.
	 */
	public ServiceJUnit4Test() {
		super(Service.class, SPECIAL_GENERATORS);
	}

	@GeneratorType(className = Etablissement.class, field = "etablissement")
	private static final class EtablissementCreator extends
			SingleBeanGenerator<Etablissement> {
		private EtablissementCreator() {
			super(Etablissement.class);
		}
	}

	@GeneratorType(className = Societe.class)
	private static final class SocieteCreator extends
			SingleBeanGenerator<Societe> {
		private SocieteCreator() {
			super(Societe.class);
		}
	}

}
