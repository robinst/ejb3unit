/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@DiscriminatorValue("DOC3")
public class Document3 extends AbstractDocumentProperty {

    protected int starRating;

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }
    
}
