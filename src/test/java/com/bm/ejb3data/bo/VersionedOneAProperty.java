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
@Table(name = "VERSIONED_ONE_A_PROPERTY")
@IdClass(VersionedPk.class)
public class VersionedOneAProperty {

    Long id;
    Long version;
    String aFeature;
    VersionedOneBProperty oneB;
    VersionedOneBProperty uniOneB;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "A_FEATURE")
    public String getAFeature() {
        return aFeature;
    }

    public void setAFeature(String aFeature) {
        this.aFeature = aFeature;
    }

    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "UNI_ONE_B_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "UNI_ONE_B_VERSION", referencedColumnName = "VERSION")})
    public VersionedOneBProperty getUniOneB() {
        return uniOneB;
    }

    public void setUniOneB(VersionedOneBProperty uniOneB) {
        this.uniOneB = uniOneB;
    }

    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "ONE_B_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ONE_B_VERSION", referencedColumnName = "VERSION")})
    public VersionedOneBProperty getOneB() {
        return oneB;
    }

    public void setOneB(VersionedOneBProperty oneB) {
        this.oneB = oneB;
    }
}
