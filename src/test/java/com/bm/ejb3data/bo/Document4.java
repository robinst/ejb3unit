/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@DiscriminatorValue("DOC4")
@Table (name = "DOCUMENT4")
public class Document4 extends Document3 {

    protected String fullReview;

    public String getFullReview() {
        return fullReview;
    }

    public void setFullReview(String fullReview) {
        this.fullReview = fullReview;
    }
    
}
