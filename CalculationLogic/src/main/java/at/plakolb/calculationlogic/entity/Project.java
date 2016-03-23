package at.plakolb.calculationlogic.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Kepplinger
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Project.findAll",
            query = "select p from Project p")
})
@Table(name = "PROJECT")
public class Project implements Serializable { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long preCalculation;
    private boolean deletion;
    private String description;
    @Size(min = 2, message = "Der Projektname muss aus mindestens 2 Zeichen bestehen")
    private String projectName;
    @NotNull(message = "Bitte geben Sie die Dachform an")
    private String roofForm;
    @NotNull(message = "Bitte geben Sie den Konstrukionstyp an")
    private String constructionType;
    private String modeOfCalculation;
    private String invoiceNumber;
    private String roofMaterial;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @OneToMany(mappedBy = "project")
    private List<Worth> worths = new ArrayList<>();
    @ManyToOne
    private Client client;

    public Project() {
        creationDate = new Date();
        lastUpdate = new Date();
    }

    public Project(
            String projectName,
            String invoiceNumber,
            String description, 
            String constructionType,
            String roofForm,
            Client client) {
        this();
        this.client = client;
        this.description = description;
        this.projectName = projectName;
        this.constructionType = constructionType;
        this.invoiceNumber = invoiceNumber;
        this.roofForm = roofForm;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getConstructionType() {
        return constructionType;
    }

    public void setConstructionType(String constructionType) {
        this.constructionType = constructionType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isDeletion() {
        return deletion;
    }

    public void setDeletion(boolean deletion) {
        this.deletion = deletion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRoofForm() {
        return roofForm;
    }

    public void setRoofForm(String roofForm) {
        this.roofForm = roofForm;
    }

    public List<Worth> getWorths() {
        return worths;
    }

    public void setWorths(List<Worth> worths) {
        this.worths = worths;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getModeOfCalculation() {
        return modeOfCalculation;
    }

    public void setModeOfCalculation(String modeOfCalculation) {
        this.modeOfCalculation = modeOfCalculation;
    }

    public String getRoofMaterial() {
        return roofMaterial;
    }

    public void setRoofMaterial(String roofMaterial) {
        this.roofMaterial = roofMaterial;
    }

    public String getProjectNameWithId() {
        return id + ": " + projectName;
    }

    public Long getPreCalculation() {
        return preCalculation;
    }

    public void setPreCalculation(Long preCalculation) {
        this.preCalculation = preCalculation;
    }

    @Override
    public String toString() {
        return projectName;
    }
}
