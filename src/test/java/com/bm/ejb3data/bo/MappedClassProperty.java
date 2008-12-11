/*
 * Created on Oct 27, 2008.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@MappedSuperclass
public class MappedClassProperty extends AbstractMappedClassProperty {

    String name;

    @Column (name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
