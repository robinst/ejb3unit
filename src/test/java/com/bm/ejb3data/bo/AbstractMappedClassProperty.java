/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@MappedSuperclass
public abstract class AbstractMappedClassProperty {

    Long id;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
