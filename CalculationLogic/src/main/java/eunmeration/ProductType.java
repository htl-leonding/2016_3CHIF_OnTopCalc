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

    @Override
    public String toString() {
        return type;
    }    
}
