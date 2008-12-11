/*
 * Created on Oct 9, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@i.cz>
 */
@Entity
@Table (name = "ONE_A")
public class OneA {
    
    @Id
    Long id;
    
    @Column (name = "A_FEATURE")
    String aFeature;
    
    @OneToOne
    @JoinColumn (name = "ONE_B_ID")
    OneB oneB;
    
    @OneToOne
    @JoinColumn (name = "ONE_B_UNI_ID")
    OneB uniOneB;

    public String getAFeature() {
        return aFeature;
    }

    public void setAFeature(String aFeature) {
        this.aFeature = aFeature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OneB getOneB() {
        return oneB;
    }

    public void setOneB(OneB oneB) {
        this.oneB = oneB;
    }
    
}
