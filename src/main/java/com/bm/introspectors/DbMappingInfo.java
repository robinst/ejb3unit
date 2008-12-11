/*
 * Created on Nov 9, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.introspectors;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
public class DbMappingInfo {

    String name;
    String referencedName;

    public DbMappingInfo(String name, String referencedName) {
        this.name = name;
        this.referencedName = referencedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public void setReferencedName(String referencedName) {
        this.referencedName = referencedName;
    }

}
