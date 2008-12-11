/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author kato
 */
@Entity (name = "MAPPED_CLASS_DOCUMENT")
public class MappedClassDocument extends MappedClass {

    @Column
    int starRating;

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

}
