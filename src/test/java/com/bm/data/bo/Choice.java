package com.bm.data.bo;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Choice.java is responsible for testing unidirectional relationsships.
 */

@Entity
public class Choice // implements Translatable
{
    @Id
    private int id;

    private boolean isDefault;

    public int getId() {
        return id;
    }

    public boolean isDefault() {
        return isDefault;
    }

    void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    protected Choice() {
        // empty.
    }

    /**
     * Constructor.
     */
    public Choice(int id, String value) {
        this.id = id;
        // TODO this.values = new HashMap<Locale.Key,Value>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Choice) {
            final Choice otherC = (Choice) other;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.id, otherC.id);
            return builder.isEquals();
        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.id);
        return builder.toHashCode();
    }

}
