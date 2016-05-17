/*	HTL Leonding	*/
package at.plakolb.calculationlogic.entity;

import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Kepplinger
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Component.findAll",
            query = "select c from Component c")
})
public class Component implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Double widthComponent;
    private Double heightComponent;
    private Double lengthComponent;
    private Double priceComponent;
    private Double numberOfProducts;
    private Double tailoringHours;
    private Double tailoringPricePerHour;
    private String componentType;
    @OneToOne
    private Category category;
    @OneToOne
    private Unit unit;
    @OneToOne
    private Product product;
    @ManyToOne
    private Project project;
    @OneToMany(mappedBy = "component", cascade = CascadeType.PERSIST)
    private List<Assembly> assemblys = new ArrayList<>();
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    public Component() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public Component(String description, Double widthComponent, Double heightComponent, Double lengthComponent, Double priceComponent, Double numberOfProducts, Category category, Unit unit, Product product, Project project) {
        this();
        this.description = description;
        this.widthComponent = widthComponent;
        this.heightComponent = heightComponent;
        this.lengthComponent = lengthComponent;
        this.priceComponent = priceComponent;
        this.numberOfProducts = numberOfProducts;
        this.category = category;
        this.unit = unit;
        this.product = product;
        setProject(project);
    }

    public Component(String description, Double widthComponent, Double heightComponent, Double lengthComponent, Double priceComponent, Double numberOfProducts, Double tailoringHours, Double tailoringPricePerHour, String componentType, Category category, Unit unit, Product product, Project project) {
        this();
        this.description = description;
        this.widthComponent = widthComponent;
        this.heightComponent = heightComponent;
        this.lengthComponent = lengthComponent;
        this.priceComponent = priceComponent;
        this.numberOfProducts = numberOfProducts;
        this.tailoringHours = tailoringHours;
        this.tailoringPricePerHour = tailoringPricePerHour;
        this.componentType = componentType;
        this.category = category;
        this.unit = unit;
        this.product = product;
        setProject(project);
    }

    public Component(String description, Double priceComponent, Double numberOfProducts, Category category, Unit unit, Product product, Project project) {
        this();
        this.description = description;
        this.priceComponent = priceComponent;
        this.numberOfProducts = numberOfProducts;
        this.category = category;
        this.unit = unit;
        this.product = product;
        setProject(project);
    }

    public List<Assembly> getAssemblys() {
        return assemblys;
    }

    public void setAssemblys(List<Assembly> assemblys) {
        this.assemblys = assemblys;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getHeightComponent() {
        return heightComponent;
    }

    public void setHeightComponent(Double heightComponent) {
        this.heightComponent = heightComponent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLengthComponent() {
        return lengthComponent;
    }

    public void setLengthComponent(Double lengthComponent) {
        this.lengthComponent = lengthComponent;
    }

    public Double getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(Double numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public Double getPriceComponent() {
        return priceComponent;
    }

    public void setPriceComponent(Double priceComponent) {
        this.priceComponent = priceComponent;
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

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Double getWidthComponent() {
        return widthComponent;
    }

    public void setWidthComponent(Double widthComponent) {
        this.widthComponent = widthComponent;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public Double getTailoringHours() {
        return tailoringHours;
    }

    public void setTailoringHours(Double tailoringHours) {
        this.tailoringHours = tailoringHours;
    }

    public Double getTailoringPricePerHour() {
        return tailoringPricePerHour;
    }

    public void setTailoringPricePerHour(Double tailoringPricePerHour) {
        this.tailoringPricePerHour = tailoringPricePerHour;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getFullNameProduct() {
        String name = "";
        if (product != null) {
            name += product.getName() + " ";
            if (lengthComponent != null) {
                name += lengthComponent + "/";
            }
            if (widthComponent != null) {
                name += widthComponent + "/";
            }
            if (heightComponent != null) {
                name += heightComponent + "/";
            }
        }
        return name.substring(0, name.length() - 1);
    }

    @Override
    public String toString() {
        return product.getFullName();
    }
}
