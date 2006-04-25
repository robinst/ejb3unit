package com.bm.datagen.relation;


/**
 * This interface represents entity beans
 * which are used by other components -> all
 * used entity beans have to be registered before.
 * @author Daniel Wiese
 * 
 * @param <T> - the type of the bean
 */
public interface EntityRelation<T> {
	
	/** 
	 * Informations if other beans (types) are used.
	 * @return a list of entity bean classes.
	 */
	Class<T> getUsedBeans();

}
