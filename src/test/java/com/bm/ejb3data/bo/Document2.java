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
@DiscriminatorValue("DOC2")
@Table (name = "DOCUMENT2")
public class Document2 extends Document1 {

    protected String fullReview;

    public String getFullReview() {
        return fullReview;
    }

    public void setFullReview(String fullReview) {
        this.fullReview = fullReview;
    }
    
}
