package com.bm.ejb3data.bo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Dropdown.java is responsible for testing unidirectional relationsships.
 */
@Entity
public class Dropdown {
    @Id
    private int id;

    @Column(nullable = false, length = 128)
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "dropdownId", nullable = false)
    private List<Choice> choices;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    protected Dropdown() {
        // empty.
    }

    public Dropdown(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Choice getDefaultChoice() {
        for (Choice choice : choices) {
            if (choice.isDefault()) {
                return choice;
            }
        }

        return null;
    }

    public void setDefaultChoice(int defaultChoiceId) {
        for (Choice choice : choices) {
            if (choice.getId() == defaultChoiceId) {
                choice.setDefault(true);
            } else if (choice.isDefault()) {
                choice.setDefault(false);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Dropdown) {
            final Dropdown otherC = (Dropdown) other;
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
