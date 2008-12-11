package com.bm.datagen.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



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
import com.bm.introspectors.relations.EntityReleationInfo;
import com.bm.introspectors.relations.OneToManyRelation;
import com.bm.introspectors.relations.RelationType;
import com.bm.utils.Ejb3Utils;

/**
 * Generates <code>N</code> entity beans (using own configurator settings) to enable bean creation for beans
 * which are using 1:M or N:M relations.
 * 
 * @author Daniel Wiese
 * @param <T> -
 *            the type of the generator
 * 
 */
public class BeanCollectionGenerator<T> implements Generator<Collection<T>>, EntityRelation<T> {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BeanCollectionGenerator.class);

    private final EntityBeanIntrospector<T> intro;

    private EntityBeanCreator<T> creator;

    private final int beansToCreate;

    private final Class<T> beanType;

    @ForInstance
    private T entityInstance;

    @ForProperty
    private Property forProperty;

    @UsedIntrospector
    private Introspector<Object> introspector;
    
    @PersistenceContext
	private EntityManager manager;


	private final List<Generator<?>> currentGenList;

    /**
     * Constructor using additional generator (for this generator).
     * 
     * @param beanToCreate -
     *            the bean to create
     * @param additionalGenerators -
     *            if the bean creation should have some special rules (creators)
     * @param beansToCreate -
     *            beans to create
     */
    public BeanCollectionGenerator(Class<T> beanToCreate, Generator<?>[] additionalGenerators, int beansToCreate) {
		this.beanType = beanToCreate;
        // register additional generators
       currentGenList = new ArrayList<Generator<?>>();
        for (Generator<?> aktGen : additionalGenerators) {
            currentGenList.add(aktGen);
        }

        this.intro = EntityBeanIntrospector.getEntityBeanIntrospector(beanToCreate);
        this.beansToCreate = beansToCreate;
    }

    /**
     * Default constructor.
     * 
     * @param beanToCreate -
     *            the bean to create
     * @param beansToCreate -
     *            bean to create
     */
    public BeanCollectionGenerator(Class<T> beanToCreate, int beansToCreate) {
        this.intro = EntityBeanIntrospector.getEntityBeanIntrospector(beanToCreate);
        this.beanType = beanToCreate;
        this.beansToCreate = beansToCreate;
        this.currentGenList=new ArrayList<Generator<?>>();
    }
    
    /**
	 * Gets called before every jUnitTest.
	 */
	@PrepareGenerator
	public void preCreate() {
		this.creator = new EntityBeanCreator<T>(manager, intro, beanType, currentGenList);
		
	}

    /**
     * Returns the next entity bean of type T.
     * 
     * @return the vaue
     * @see com.bm.datagen.Generator#getValue()
     */
    @SuppressWarnings("unchecked")
	public Collection<T> getValue() {
        final Collection<T> back = Ejb3Utils.getRightCollectionType(forProperty);
        final PersistentPropertyInfo pi = introspector.getPresistentFieldInfo(forProperty);
        for (int i = 0; i < this.beansToCreate; i++) {
            T created = this.creator.createBeanInstance();
            if (pi.isReleation()) {
                final EntityReleationInfo er = pi.getEntityReleationInfo();
                if (er.getReleationType() == RelationType.OneToMany) {

                    OneToManyRelation o2m = (OneToManyRelation) er;
                    if (!o2m.isUnidirectional()) {
                        try {
                            o2m.getTargetProperty().setField(created, this.entityInstance);
                        } catch (IllegalAccessException e) {
                            log.error("OneToMany rel.: Can�t set the property: "
                                    + o2m.getTargetProperty().getName());
                            throw new RuntimeException("OneToMany rel.: Can�t set the property: "
                                    + o2m.getTargetProperty().getName());
                        }
                    }
                }

            }
            back.add(created);
        }
        return back;
    }

    /**
     * Returns the releated bean: The Type of the bean which is generated by this generator.
     * 
     * @return the used entity beans
     * @see com.bm.datagen.relation.EntityRelation#getUsedBeans()
     */
    public Class<T> getUsedBeans() {
        // only one in this case
        return this.beanType;
    }
}
