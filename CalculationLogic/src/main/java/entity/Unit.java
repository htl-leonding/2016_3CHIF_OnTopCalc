/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

/**
 *
 * @author Kepplinger
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Unit.findAll",
                query = "select u from Unit u")
})
public class Unit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String shortTerm;
    String longTerm;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;


    public Unit() {
        creationDate = new Date();
    }

    public Unit(String longTerm, String shortTerm) {
        this();
        this.shortTerm = shortTerm;
        this.longTerm = longTerm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongTerm() {
        return longTerm;
    }

    public void setLongTerm(String longTerm) {
        this.longTerm = longTerm;
    }

    public String getShortTerm() {
        return shortTerm;
    }

    public void setShortTerm(String shortTerm) {
        this.shortTerm = shortTerm;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    } 
    
    @Override
    public String toString() {
        return shortTerm;
    }
}

