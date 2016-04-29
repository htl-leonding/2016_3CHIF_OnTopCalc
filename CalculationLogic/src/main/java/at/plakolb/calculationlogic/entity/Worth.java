package at.plakolb.calculationlogic.entity;

import at.plakolb.calculationlogic.util.UtilityFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
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
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    private String shortTerm;

    public Worth() {
        creationDate = new Date();
    }

    public Worth(ParameterP parameter) {
        this();
        this.parameter = parameter;
        this.shortTerm = parameter.getShortTerm();
    }

    public Worth(Project project, ParameterP parameter, double worth) {
        this(parameter);
        this.worth = worth;
        setProject(project);

    }

    /**
     *
     * @param project
     * @param parameter
     * @param worth
     * @param shortTerm Falls ein Index verwendet wird, ändert sich der
     * shortTerm zB von N zu N1
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

//    public String worthFormat() {
//        return UtilityFormat.formatWorth(this);
//    }
//    
//    public String worthFormatWithUnit() {
//        return UtilityFormat.worthWithUnit(this);
//    }
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
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
