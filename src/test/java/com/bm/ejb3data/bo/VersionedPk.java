/*
 * Created on Nov 6, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class VersionedPk implements Serializable {

    public static final long serialVersionUID = 1;

    @Id
    @Column (name = "ID")
    private Long id;

    @Id
    @Column (name = "VERSION")
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VersionedPk other = (VersionedPk) obj;
        if (this.id != other.id &&
                (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.version != other.version &&
                (this.version == null ||
                !this.version.equals(other.version))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 11 * hash +
                (this.version != null ? this.version.hashCode() : 0);
        return hash;
    }

}
