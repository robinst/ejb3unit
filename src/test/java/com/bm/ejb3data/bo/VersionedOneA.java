/*
 * Created on Oct 9, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@Table (name = "VERSIONED_ONE_A")
@IdClass(VersionedPk.class)
public class VersionedOneA {
    
    @Id
    Long id;

    @Id
    Long version;
    
    @Column (name = "A_FEATURE")
    String aFeature;
    
    @OneToOne
    @JoinColumns( {
            @JoinColumn(name = "ONE_B_ID", referencedColumnName = "ID"),
            @JoinColumn(name = "ONE_B_VERSION", referencedColumnName = "VERSION") })
    VersionedOneB oneB;
    
    @OneToOne
    @JoinColumns( {
            @JoinColumn(name = "UNI_ONE_B_ID", referencedColumnName = "ID"),
            @JoinColumn(name = "UNI_ONE_B_VERSION", referencedColumnName = "VERSION") })
    VersionedOneB uniOneB;


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

    public String getAFeature() {
        return aFeature;
    }

    public void setAFeature(String aFeature) {
        this.aFeature = aFeature;
    }

    public VersionedOneB getUniOneB() {
        return uniOneB;
    }

    public void setUniOneB(VersionedOneB uniOneB) {
        this.uniOneB = uniOneB;
    }

    public VersionedOneB getOneB() {
        return oneB;
    }

    public void setOneB(VersionedOneB oneB) {
        this.oneB = oneB;
    }
}
