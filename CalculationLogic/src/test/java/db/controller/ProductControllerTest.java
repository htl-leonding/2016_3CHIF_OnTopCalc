/*	HTL Leonding	*/
package db.controller;

import at.plakolb.calculationlogic.db.controller.ProductController;
import at.plakolb.calculationlogic.entity.Product;
import at.plakolb.calculationlogic.eunmeration.ProductType;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andreas
 */
public class ProductControllerTest {
    
    public ProductControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of create method, of class ProductController.
     */
    @Test
    public void testCreate() {
        System.out.println("create Product");
        Product product = new Product("Test", 10.0, 100.0, 50.0, 5.1, null, ProductType.WOOD);
        ProductController instance = new ProductController();
        instance.create(product);
        List<Product> products = instance.findAll();
        assertThat(products.size(), is(1));
    }

    
}
