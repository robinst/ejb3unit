/*
 * Created on Oct 9, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@Table(name = "VERSIONED_ONE_B_PROPERTY")
@IdClass(VersionedPk.class)
public class VersionedOneBProperty {

    Long id;
    Long version;
    String bFeature;
    VersionedOneAProperty oneA;

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

    @Column(name = "B_FEATURE")
    public String getBFeature() {
        return bFeature;
    }

    public void setBFeature(String bFeature) {
        this.bFeature = bFeature;
    }

    @OneToOne(mappedBy = "oneB")
    public VersionedOneAProperty getOneA() {
        return oneA;
    }

    public void setOneA(VersionedOneAProperty oneA) {
        this.oneA = oneA;
    }
}
