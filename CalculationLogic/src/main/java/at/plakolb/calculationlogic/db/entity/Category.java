/*	HTL Leonding	*/
package at.plakolb.calculationlogic.db.entity;

import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

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
    private String shortTerm;
    private String longTerm;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    public Category() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public Category(String longTerm, String shortTerm) {
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return longTerm;
    }
}
