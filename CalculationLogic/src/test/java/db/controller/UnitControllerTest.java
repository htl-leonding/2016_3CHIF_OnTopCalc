/*	HTL Leonding	*/
package db.controller;

import at.plakolb.calculationlogic.db.controller.UnitController;
import at.plakolb.calculationlogic.entity.Unit;
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
public class UnitControllerTest {
    
    public UnitControllerTest() {
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
     * Test of create method, of class UnitController.
     */
    @Test
    public void testCreate() {
        Unit unit = new Unit("km","Kilometer");
        UnitController instance = new UnitController();
        instance.create(unit);
        List<Unit> res = instance.findAll();
        assertThat(res.size(), is(1));
    }
    
}
