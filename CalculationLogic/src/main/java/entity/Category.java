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
    @NamedQuery(name = "Category.findAll",
                query = "select c from Category c")
})
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String shortTerm;
    String longTerm;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;


    public Category() {
        creationDate = new Date();
    }

    public Category(String shortTerm, String longTerm) {
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
    
    public String getLongAndShortTerm() {
        return longTerm + " (" + shortTerm + ")"; 
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", shortTerm=" + shortTerm + ", longTerm=" + longTerm + '}';
    }
}

