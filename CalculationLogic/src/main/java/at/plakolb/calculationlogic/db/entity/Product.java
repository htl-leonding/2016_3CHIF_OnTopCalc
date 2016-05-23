/*	HTL Leonding	*/
package at.plakolb.calculationlogic.db.entity;

import at.plakolb.calculationlogic.eunmeration.ProductType;
import at.plakolb.calculationlogic.util.LocalDateTimeAttributeConverter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @NamedQuery(name = "Product.findAll",
            query = "select p from Product p")
})
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double widthProduct;
    private Double heightProduct;
    private Double lengthProduct;
    private Double priceUnit;
    private Double colorFactor;
    @OneToOne
    private Unit unit;
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    public Product() {
        creationDate = LocalDateTime.now(Clock.systemDefaultZone());
    }

    public Product(String name, Double widthProduct, Double heightProduct, Double lengthProduct, Double priceUnit, Unit unit, ProductType productType) {
        this();
        this.name = name;
        this.widthProduct = widthProduct;
        this.heightProduct = heightProduct;
        this.lengthProduct = lengthProduct;
        this.priceUnit = priceUnit;
        this.unit = unit;
        this.productType = productType;
    }

    public Product(String name, Double colorFactor, Double priceUnit, Unit unit, ProductType productType) {
        this();
        this.name = name;
        this.priceUnit = priceUnit;
        this.colorFactor = colorFactor;
        this.unit = unit;
        this.productType = productType;
    }

    public Double getHeightProduct() {
        return heightProduct;
    }

    public void setHeightProduct(Double heightProduct) {
        this.heightProduct = heightProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLengthProduct() {
        return lengthProduct;
    }

    public void setLengthProduct(Double lengthProduct) {
        this.lengthProduct = lengthProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Double priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Double getWidthProduct() {
        return widthProduct;
    }

    public void setWidthProduct(Double widthProduct) {
        this.widthProduct = widthProduct;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Double getColorFactor() {
        return colorFactor;
    }

    public void setColorFactor(Double colorFactor) {
        this.colorFactor = colorFactor;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getFullName() {

        String nameFull = name + " ";
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        if (ProductType.COLOR == productType) {
            return nameFull.substring(0, nameFull.length() - 1);
        } else if (unit.getShortTerm().equals("m")) {
            return nameFull += (lengthProduct == null ? "0" : decimalFormat.format(lengthProduct)) + " " + unit.getShortTerm();
        } else if (productType.equals(ProductType.FORMWORK)) {
            return nameFull + " " + decimalFormat.format(widthProduct * 100) + " mm";
        } else if (productType.equals(ProductType.WOOD)) {

            nameFull += (widthProduct == null ? "0" : decimalFormat.format(widthProduct)) + "/";
            nameFull += (heightProduct == null ? "0" : decimalFormat.format(heightProduct)) + "/";
            nameFull += (lengthProduct == null ? "0" : decimalFormat.format(lengthProduct));

            return nameFull;
        } else {
            return nameFull + " [" + unit.getShortTerm() + "]";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.widthProduct, other.widthProduct)) {
            return false;
        }
        if (!Objects.equals(this.heightProduct, other.heightProduct)) {
            return false;
        }
        if (!Objects.equals(this.lengthProduct, other.lengthProduct)) {
            return false;
        }
        if (!Objects.equals(this.priceUnit, other.priceUnit)) {
            return false;
        }
        if (!Objects.equals(this.colorFactor, other.colorFactor)) {
            return false;
        }
        if (!Objects.equals(this.unit.getId(), other.unit.getId())) {
            return false;
        }
        if (this.productType != other.productType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
