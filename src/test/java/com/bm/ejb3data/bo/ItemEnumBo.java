/*
 * Created on Sep 4, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman
 */
@Entity
@Table(name = "testenumentity")
public class ItemEnumBo implements Serializable {

    public static final long serialVersionUID = 1;
    @Id
    @Column(name = "wkn_id", nullable = false)
    private int wkn;
    @Column(name = "enum_field")
    private ItemEnum enumField;
    @Column(name = "enum_field_string")
    @Enumerated(EnumType.STRING)
    private ItemEnum enumFieldString;
    
    @ManyToOne
    @JoinColumn(name = "item_codebook_id")
    private ItemCodebook itemCodebook;
    
    public ItemEnum getEnumField() {
        return enumField;
    }

    public void setEnumField(ItemEnum enumField) {
        this.enumField = enumField;
    }

    public ItemEnum getEnumFieldString() {
        return enumFieldString;
    }

    public void setEnumFieldString(ItemEnum enumFieldString) {
        this.enumFieldString = enumFieldString;
    }

    public int getWkn() {
        return wkn;
    }

    public void setWkn(int wkn) {
        this.wkn = wkn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemEnumBo other = (ItemEnumBo) obj;
        if (this.wkn != other.wkn) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.wkn;
        return hash;
    }
}
