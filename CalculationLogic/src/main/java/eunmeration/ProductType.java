package eunmeration;

/**
 *
 * @author Kepplinger
 */
public enum ProductType {
    
    WOOD("Holz"),
    COLOR("Farbe"), 
    MISCELLANEOUS("Diverses"),  
    FOIL("Folie"),
    FORMWORK("Schalung"),
    SEALINGBAND("Nageldichtband"),
    BATTEN("Lattung"),
    COUNTERBATTEN("Konterlattung");
    
    private final String type;
    
    private ProductType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return type;
    }

    private ProductType() {
        this.type = null;
    }

    public static ProductType getProductType(String type){
        for (ProductType productType : ProductType.values()) {
            if (productType.getType().toLowerCase().equals(type.toLowerCase())) {
                return productType;
            }
        }
        return null;
    }
}
