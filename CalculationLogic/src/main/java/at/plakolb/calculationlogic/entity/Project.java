/*	HTL Leonding	*/
package at.plakolb.calculationlogic.entity;

import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
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
    private String projectName;
    private String roofForm;
    private String constructionType;
    private String modeOfCalculation;
    private String invoiceNumber;
    private String roofMaterial;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdate;
    @OneToMany(mappedBy = "project")
    private List<Worth> worths = new ArrayList<>();
    @ManyToOne
    private Client client;

    public Project() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
        lastUpdate = LocalDateTime.now(Clock.systemDefaultZone());
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
        this.modeOfCalculation = "Vorkalkulation";
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
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

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
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
