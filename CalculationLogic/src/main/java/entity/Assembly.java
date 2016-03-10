package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Kepplinger
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Assembly.findAll",
            query = "select a from Assembly a")
})
public class Assembly implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    Product product;
    @ManyToOne
    Component component;
    @ManyToOne
    Project project;
    int numberOfComponents;
    double price;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;

    public Assembly() {
        creationDate = new Date();
    }

    public Assembly(Product product, Component component, Project project, int number, double price) {
        this();
        this.product = product;    
        this.numberOfComponents = number;
        this.price = price;
        setProject(project);
        setComponent(component);
    }  

    public Component getComponent() {
        return component;
    }

    public final void setComponent(Component component) {
        if(component != null) {
            component.getAssemblys().add(this);
        }
        this.component = component;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    public void setNumberOfComponents(int number) {
        this.numberOfComponents = number;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Project getProject() {
        return project;
    }

    public final void setProject(Project project) {
        this.project = project;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
 
    @Override
    public String toString() {
        return "Assembly{" + "id=" + id + ", product=" + product + /*", component=" + component +*/ ", project=" + project + ", number=" + numberOfComponents + '}';
    } 
}
