package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @NamedQuery(name = "Client.findAll",
            query = "select c from Client c")
})
@Table(name = "CLIENT")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String name;
    String street;
    String city;
    String zipCode;
    String telephoneNumber;
    String eMail;
    @OneToMany(mappedBy = "client")
    private List<Project> projects = new ArrayList<>();
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    String projectNumbers;  //Project ID String for the Table View
    
    public Client() {
        this.creationDate = new Date();
    }

    public Client(String name, String street, String city, String zipCode, String telephoneNumber, String eMail) {
        this();
        this.name = name;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.telephoneNumber = telephoneNumber;
        this.eMail = eMail;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return eMail;
    }

    public void setEmail(String eMail) {
        this.eMail = eMail;
    }
    
    public String getProjectNumbers() {
        projectNumbers = "";
        for (int i = 0; i < projects.size(); i++) {
            projectNumbers += projects.get(i).getId();
            if (i != projects.size() - 1) {
                projectNumbers += ", ";
            }
        }
        return projectNumbers;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

