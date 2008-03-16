/**
 * 
 */
package com.bm;

import com.bm.datagen.Generator;
import com.bm.datagen.annotations.GeneratorType;
import com.bm.datagen.relation.SingleBeanGenerator;
import com.bm.ejb3data.bo.Etablissement;
import com.bm.ejb3data.bo.Service;
import com.bm.ejb3data.bo.Societe;
import com.bm.testsuite.BaseEntityFixture;

/**
 * Importet test case from a bug report.
 * @author Dnaiel Wiese
 * @author LIEMANS
 * @generated "UML to Java V5.0
 *            (com.ibm.xtools.transform.uml2.java5.internal.UML2JavaTransform)"
 */
public class ServiceTest extends BaseEntityFixture<Service> {
	private static final Generator<?>[] SPECIAL_GENERATORS = {
			new EtablissementCreator(), new SocieteCreator() };

	/**
	 * 
	 * Constructor.
	 */
	public ServiceTest() {
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