/*
 * Created on Oct 9, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@i.cz>
 */
@Entity
@Table (name = "ONE_B")
public class OneB {
    
    @Id
    Long id;
    
    @Column (name = "B_FEATURE")
    String bFeature;
    
    @OneToOne (mappedBy = "oneB")
    OneA oneA;

    public String getBFeature() {
        return bFeature;
    }

    public void setBFeature(String bFeature) {
        this.bFeature = bFeature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
