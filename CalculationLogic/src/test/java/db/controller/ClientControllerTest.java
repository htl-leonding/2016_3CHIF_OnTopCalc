/*	HTL Leonding	*/
package db.controller;

import entity.Client;
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
public class ClientControllerTest {
    
    public ClientControllerTest() {
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
     * Test of create method, of class ClientController.
     */
    @Test
    public void testCreate_FindAll() {
        System.out.println("create");
        Client client = new Client(
                "Tester", "Teststraße",
                "Testing", "0815", "0664555555");
        ClientController instance = new ClientController();
        instance.create(client);
        List<Client> result = instance.findAll();
        assertThat(result.size(), is(1));
    }
    
}
