/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@Table(name = "USER_ABSTRACT_DOCUMENTS")
@Inheritance(strategy = InheritanceType.JOINED)
@IdClass(ExpertiseAreasPK.class)
public class AbstractUserDocument {


    @Id
    protected Long prodId;

    @Id
    protected Long userId;
    
    @Column (name = "DOCUMENT_NAME")
    protected String name;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
