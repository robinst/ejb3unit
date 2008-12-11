/*
 * Created on Oct 18, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

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
@Table (name = "ONE_C")
public class OneC {
    
    @Id
    Long id;
    
    @OneToOne
    @JoinColumn (name = "ONE_A_ID")
    OneA oneA;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OneA getOneA() {
        return oneA;
    }

    public void setOneA(OneA oneA) {
        this.oneA = oneA;
    }
    
}
