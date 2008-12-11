/*
 * Created on Dec 8, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@Table(name = "INTER_ITEM")
public class InterDepItem {

    @Id
    private Long id;

    @ManyToOne
    private InterDepOrder order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterDepOrder getOrder() {
        return order;
    }

    public void setOrder(InterDepOrder order) {
        this.order = order;
    }
}
