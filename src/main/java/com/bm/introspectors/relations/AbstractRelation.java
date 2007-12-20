package com.bm.introspectors.relations;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import com.bm.introspectors.Property;
import com.bm.utils.Ejb3Utils;

/**
 * Abstract class for representation of relations.
 * 
 * @author Daniel Wiese
 */
public abstract class AbstractRelation implements EntityReleationInfo {

	private final Class sourceClass;

	private final Class targetClass;

	private final Property sourceProperty;

	private final Property targetProperty;

	private final FetchType fetchType;
    
    private final boolean isUnidirectional;

	private final CascadeType[] cascadeType;

	/**
	 * Default constructor.
	 * 
	 * @param sourceClass -
	 *            the type of the source entity bean
	 * @param targetClass -
	 *            the type of the target entity bean
	 * @param sourceProperty -
	 *            the property of the source entity bean
	 * @param targetProperty -
	 *            the property of the target entity bean
	 * @param fetchType -
	 *            fetch type
	 * @param cascadeType -
	 *            cascade type
	 * 
	 */
	public AbstractRelation(Class sourceClass, Class targetClass,
			Property sourceProperty, Property targetProperty,
			FetchType fetchType, CascadeType[] cascadeType) {
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.targetProperty = targetProperty;
		this.sourceProperty = sourceProperty;
		this.fetchType = fetchType;
		this.cascadeType = cascadeType;
        this.isUnidirectional = (targetProperty==null);
	}

	/**
	 * Returns the cascadeType.
	 * @return Returns the cascadeType.
	 */
	public CascadeType[] getCascadeType() {
		return cascadeType;
	}

	/**
	 * Returns the fetchType.
	 * @return Returns the fetchType.
	 */
	public FetchType getFetchType() {
		return fetchType;
	}

	/**
	 * Returns the sourceClass.
	 * 
	 * @return Returns the sourceClass.
	 */
	public Class getSourceClass() {
		return this.sourceClass;
	}

	/**
	 * Returns the sourceProperty.
	 * 
	 * @return Returns the sourceProperty.
	 */
	public Property getSourceProperty() {
		return this.sourceProperty;
	}

	/**
	 * Returns the targetClass.
	 * 
	 * @return Returns the targetClass.
	 */
	public Class getTargetClass() {
		return this.targetClass;
	}

	/**
	 * Returns the targetProperty.
	 * 
	 * @return Returns the targetProperty.
	 */
	public Property getTargetProperty() {
		return this.targetProperty;
	}
    
	/**
     * If the preperty is unidirectional.
     * @return the isUnidirectional
     */
    public boolean isUnidirectional() {
        return isUnidirectional;
    }

    /**
	 *{@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[" + this.getReleationType() + "] ")
				.append("Source class: ").append(
						Ejb3Utils.getShortClassName(this.sourceClass)).append(
						"\n");
		sb.append("Source attrib: ").append(this.sourceProperty.getName())
				.append("\n");
		sb.append("Target class: ").append(
				Ejb3Utils.getShortClassName(this.targetClass)).append("\n");
		sb.append("Target attrib: ").append(this.targetProperty.getName());
		return sb.toString();
	}
    
    /**
     * True wenn the delete operatio is cascading.
     * @return when the delete operation is cascading
     */
    public boolean isCascadeOnDelete() {
        boolean back=false;
        if (this.cascadeType!=null) {
            for (CascadeType current: this.cascadeType) {
                if ((current.equals(CascadeType.ALL))|| (current.equals(CascadeType.REMOVE))){
                    back=true;
                    break;
                }
            }
        }
        
        return back;
    }

    /**
     * Returns the primary key property (or properties, in case of a composite key) for the 
     * class that is target of the relation
     * @return	primary key property / properties.
     */
	public Set<Property> getTargetKeyProperty() {
		return null;
	}

}
