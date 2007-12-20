package com.bm.ejb3data.bo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NamedQuery;

/**
 * Simple Employee class for testing relations.
 */
@Entity
public class Employee implements Serializable
{
    @Id
    @GeneratedValue
    private int empId;
    
    public String name;
    public String fullname;
    
    //@JoinTable(name="empdept", joinColumns=@JoinColumn(name="empid", nullable=true), inverseJoinColumns=@JoinColumn(name="deptid", nullable=true))    

    // The most simple, straight-forward (bi-directional) many-to-one relation
    @ManyToOne
    public Department department1;
    
    // Same as before, now with a simple join-column annotation
    @ManyToOne()
    @JoinColumn(name="dept2")
    public Department department2;

    // Same as before, now with the annotations in reversed order
    @JoinColumn(name="dept3")
    @ManyToOne()
    public Department department3;

    
    
    // The most simple, straight-forward (unidirectional) many-to-one relation
    @ManyToOne
    private Department department4;
    
    // Same as before, now with a simple join-column annotation
    @ManyToOne()
    @JoinColumn(name="dept5")
    private Department department5;

    // Same as before, now with the annotations in reversed order
    @JoinColumn(name="dept6")
    @ManyToOne()
    private Department department6;

}
