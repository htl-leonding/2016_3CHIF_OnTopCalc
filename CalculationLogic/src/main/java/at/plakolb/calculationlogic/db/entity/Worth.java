/*	HTL Leonding	*/
package at.plakolb.calculationlogic.db.entity;

import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;
import at.plakolb.calculationlogic.util.UtilityFormat;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * @author Kepplinger
 */
@Entity
public class Worth implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private ParameterP parameter;
    private double worth;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;
    private String shortTerm;

    public Worth() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public Worth(ParameterP parameter) {
        this();
        this.parameter = parameter;
        if (parameter != null) {
            this.shortTerm = parameter.getShortTerm();
            if (parameter.isEditable()) {
                this.worth = parameter.getDefaultValue();
            }
        }
    }

    public Worth(Project project, ParameterP parameter, double worth) {
        this(parameter);
        this.worth = worth;
        setProject(project);
    }

    /**
     * @param project
     * @param parameter
     * @param worth
     * @param shortTerm Falls ein Index verwendet wird, ändert sich der
     *                  shortTerm zB von N zu N1
     */
    public Worth(Project project, ParameterP parameter, double worth, String shortTerm) {
        this();
        this.parameter = parameter;
        this.worth = worth;
        this.shortTerm = (shortTerm == null ? parameter.getShortTerm() : shortTerm);
        setProject(project);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParameterP getParameter() {
        return parameter;
    }

    public void setParameter(ParameterP parameter) {
        this.parameter = parameter;
    }

    public Project getProject() {
        return project;
    }

    public final void setProject(Project project) {
        if (project != null) {
            project.getWorths().add(this);
        }
        this.project = project;
    }

    public double getWorth() {
        return worth;
    }

    public void setWorth(double worth) {
        if (Double.isNaN(worth) || Double.isInfinite(worth)) {
            this.worth = 0;
        } else {
            this.worth = worth;
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getShortTerm() {
        return shortTerm;
    }

    public void setShortTerm(String shortTerm) {
        this.shortTerm = shortTerm;
    }

    @Override
    public String toString() {
        return "Worth{" + "id=" + id + ", project=" + project + ", parameter=" + parameter + ", worth=" + worth + '}';
    }

    public String worthFormatWithUnit() {
        return UtilityFormat.worthWithUnit(this);
    }
}
