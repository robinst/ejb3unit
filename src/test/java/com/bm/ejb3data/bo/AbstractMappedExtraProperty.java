/*
 * Created on Nov 5, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Jmeno Prijmeni <jmeno.prijmeni@i.cz> - zmenit v Templates/User Configuration Properties/User.properties
 */
@MappedSuperclass
public abstract class AbstractMappedExtraProperty {

    protected String superfluous;

    @Column (name = "SUPERFLUOUS")
    public String getSuperfluous() {
        return superfluous;
    }

    public void setSuperfluous(String superfluous) {
        this.superfluous = superfluous;
    }

}
