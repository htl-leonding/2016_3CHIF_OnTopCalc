package at.plakolb.calculationlogic.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
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
    private int numberOfProducts;
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
    @OneToMany(mappedBy = "component")
    private List<Assembly> assemblys = new ArrayList<>();
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;

    public Component() {
        creationDate = new Date();
    }

    public Component(String description, Double widthComponent, Double heightComponent, Double lengthComponent, Double priceComponent, int numberOfProducts, Category category, Unit unit, Product product, Project project) {
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

    public Component(String description, Double widthComponent, Double heightComponent, Double lengthComponent, Double priceComponent, int numberOfProducts, Double tailoringHours, Double tailoringPricePerHour, String componentType, Category category, Unit unit, Product product, Project project) {
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

    public Component(String description, Double priceComponent, int numberOfProducts, Category category, Unit unit, Product product, Project project) {
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

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFullNameProduct() {
        String name = "";
        if (product != null) {
            String nameFull = name + " ";
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            nameFull += (widthComponent == null ? "0" : decimalFormat.format(widthComponent)) + "/";
            nameFull += (heightComponent == null ? "0" : decimalFormat.format(heightComponent)) + "/";
            nameFull += (lengthComponent == null ? "0" : decimalFormat.format(lengthComponent)) + "/";
        }
        return name.substring(0, name.length() - 1);
    }

    @Override
    public String toString() {
        return "Component{" + "id=" + id + ", description=" + description + ", widthComponent=" + widthComponent + ", heightComponent=" + heightComponent + ", lengthComponent=" + lengthComponent + ", priceComponent=" + priceComponent + ", numberOfProducts=" + numberOfProducts + ", category=" + category + ", unit=" + unit + ", product=" + product + ", project=" + project + '}';
    }
}
