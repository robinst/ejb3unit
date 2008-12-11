/*
 * Created on Dec 8, 2008.
 * (C) 2008 ICZ, a.s.
 */
package com.bm.ejb3data.bo;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Kamil Toman <kamil.toman@gmail.com>
 */
@Entity
@Table(name = "INTER_ORDER")
public class InterDepOrder {

    @Id
    private Long id;

    @OneToMany(mappedBy = "order")
    private List<InterDepItem> items;

    @OneToOne
    private InterDepItem mainItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InterDepItem> getItems() {
        return items;
    }

    public void setItems(List<InterDepItem> items) {
        this.items = items;
    }

    public InterDepItem getMainItem() {
        return mainItem;
    }

    public void setMainItem(InterDepItem mainItem) {
        this.mainItem = mainItem;
    }
}
