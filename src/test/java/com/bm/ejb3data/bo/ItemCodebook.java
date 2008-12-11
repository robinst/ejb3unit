/*
 * Created on Sep 25, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@i.cz>
 */
@Entity
@Table(name = "testcodebook")
public class ItemCodebook {
    
    public static final long serialVersionUID = 1;
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "code")
    String code;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemCodebook other = (ItemCodebook) obj;
        if (this.id != other.id &&
                (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
}
