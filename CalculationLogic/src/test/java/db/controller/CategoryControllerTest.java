/*	HTL Leonding	*/
package db.controller;

import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.entity.Category;
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
public class CategoryControllerTest {
    
    public CategoryControllerTest() {
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
     * Test of create method, of class CategoryController.
     */
    @Test
    public void testCreate() {
        Category category = new Category("TS", "Test");
        CategoryController instance = new CategoryController();
        instance.create(category);
        List<Category> result = instance.findAll();
        assertThat(result.size(), is(1));
    }
    
}
