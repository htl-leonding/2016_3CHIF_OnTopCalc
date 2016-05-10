/*	HTL Leonding	*/
package db.controller;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.entity.Assembly;
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
public class AssemblyControllerTest {
    
    public AssemblyControllerTest() {
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
     * Test of create method, of class AssemblyController.
     */
    @Test
    public void testCreate() {
        Assembly assembly = new Assembly(null, null, null, 10d, 100d);
        AssemblyController instance = new AssemblyController();
        instance.create(assembly);
        List<Assembly> result = instance.findAll();
        assertThat(result.size(), is(1));
    }
}
