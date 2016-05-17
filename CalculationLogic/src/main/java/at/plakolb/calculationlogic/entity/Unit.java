/*	HTL Leonding	*/
package at.plakolb.calculationlogic.entity;

import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import javax.persistence.Convert;
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
    private String shortTerm;
    private String longTerm;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    public Unit() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return shortTerm;
    }
}
