package com.bm.datagen.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import com.bm.cfg.Ejb3UnitCfg;
import com.bm.creators.EntityBeanCreator;
import com.bm.datagen.Generator;
import com.bm.datagen.annotations.ForInstance;
import com.bm.datagen.annotations.ForProperty;
import com.bm.datagen.annotations.PrepareGenerator;
import com.bm.datagen.annotations.UsedIntrospector;
import com.bm.introspectors.EntityBeanIntrospector;
import com.bm.introspectors.Introspector;
import com.bm.introspectors.PersistentPropertyInfo;
import com.bm.introspectors.Property;
import com.bm.introspectors.releations.EntityReleationInfo;
import com.bm.introspectors.releations.ManyToOneReleation;
import com.bm.introspectors.releations.RelationType;
import com.bm.utils.Ejb3Utils;

/**
 * Generates <code>1</code> entity bean (using own configurator settings) to enable ben creation for beans
 * wich are using 1:1 or N:1 relations.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the entity bean to creat
 * 
 */
public class SingleBeanGenerator<T> implements Generator<T>, EntityRelation<T> {

    private static Logger log = Logger.getLogger(SingleBeanGenerator.class);

    private static EntityManagerFactory emf;

    private final EntityBeanIntrospector<T> intro;

    private final EntityBeanCreator<T> creator;

    private final Class<T> beanType;

    private T createdBean;

    @ForInstance
    private T entityInstance;

    @ForProperty
    private Property forProperty;

    @UsedIntrospector
    private Introspector<Object> introspector;

    /**
     * Contructor using additional generator (for this generator).
     * 
     * @param beanToCreate -
     *            the bean to create
     * @param additionalGenerators -
     *            if the beand creation sould have some special rules (creators)
     */
    public SingleBeanGenerator(Class<T> beanToCreate, Generator[] additionalGenerators) {
        this.beanType = beanToCreate;
        // register additional generators
        final List<Generator> currentGenList = new ArrayList<Generator>();
        for (Generator aktGen : additionalGenerators) {
            currentGenList.add(aktGen);
        }

        this.intro = new EntityBeanIntrospector<T>(beanToCreate);
        this.creator = new EntityBeanCreator<T>(intro, beanToCreate, currentGenList);
    }

    /**
     * Default constructor.
     * 
     * @param beanToCreate -
     *            the bean to create
     */
    public SingleBeanGenerator(Class<T> beanToCreate) {
        this.beanType = beanToCreate;
        this.intro = new EntityBeanIntrospector<T>(beanToCreate);
        this.creator = new EntityBeanCreator<T>(intro, beanToCreate);
    }

    /**
     * Gets called before every jUnitTest.
     */
    @PrepareGenerator
    public void preCreate() {
        // init the entity manager lazy
        // we can ensure that the configuration is being initialized erlier
        if (emf == null) {
            this.initEntityManagerFactory();
        }
        createdBean = this.creator.createBeanInstance();
        this.persist(createdBean);
    }

    /**
     * Returns the next entity bean of type T.
     * 
     * @return next entity bean of type T.
     * @see com.bm.datagen.Generator#getValue()
     */
    @SuppressWarnings("unchecked")
    public T getValue() {
        final PersistentPropertyInfo pi = introspector.getPresistentFieldInfo(forProperty);
        if (pi.isReleation()) {
            final EntityReleationInfo er = pi.getEntityReleationInfo();
            // FIXME cosidder one2one relation
            // add the Bean to the collection of
            // this.entityInstance (who is requesting the value)
            if (er.getReleationType() == RelationType.ManyToOne) {

                ManyToOneReleation m2o = (ManyToOneReleation) er;
                if (!m2o.isUnidirectional()) {
                    try {
                        Collection toAdd = null;
                        // check if the collection is not null
                        if (m2o.getTargetProperty().getField(this.createdBean) == null) {
                            toAdd = Ejb3Utils.getRightCollectionType(m2o.getTargetProperty());
                        } else {
                            toAdd = (Collection) m2o.getTargetProperty().getField(this.createdBean);
                        }
                        // add collection with this bean to the other side
                        toAdd.add(this.entityInstance);
                        m2o.getTargetProperty().setField(this.createdBean, toAdd);
                    } catch (IllegalAccessException e) {
                        log.error("OneToMany rel.: Can�t set the property: "
                                + m2o.getTargetProperty().getName());
                        throw new RuntimeException("OneToMany rel.: Can�t set the property: "
                                + m2o.getTargetProperty().getName());
                    }
                }
            }
        }
        return this.createdBean;
    }

    /**
     * Returns the releated bean: The Type of the bean wich is generated by this generator.
     * 
     * @return the releated bean: The Type of the bean wich is generated by this generator.
     * @see com.bm.datagen.relation.EntityRelation#getUsedBeans()
     */
    public Class<T> getUsedBeans() {
        // only one in this case
        return this.beanType;
    }

    private void persist(T toPersist) {
        final EntityManager manager = emf.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        try {
            log.debug("Pre-Test-Persist: Need to persist ONE Entiy-Bean of type" + this.beanType);
            tx.begin();
            manager.persist(toPersist);
            // the transaction must be committed
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
        } finally {
            manager.close();
        }

    }

    private void initEntityManagerFactory() {
        emf = Ejb3UnitCfg.getConfiguration().getEntityManagerFactory();
    }
}
