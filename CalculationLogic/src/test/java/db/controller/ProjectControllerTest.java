/*	HTL Leonding	*/
package db.controller;

import db.exceptions.NonexistentEntityException;
import entity.Client;
import entity.Project;
import java.util.ArrayList;
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
 * @author Kepplinger
 */
public class ProjectControllerTest {

    public ProjectControllerTest() {
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
     * Test of create method, of class ProjectController.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Client client = new Client("Hans Berger", "Oberhauserweg 3", "Linz", "5020", "+4366098484231");
        new ClientController().create(client);

        Project project1 = new Project("TestprojektCreate", "047583", "Notiz", "Haus", "Walmdach", client);
        ProjectController instance = new ProjectController();
        instance.create(project1);
        assertThat(instance.findLastProject().getLastUpdate(), is(project1.getLastUpdate()));
    }

    /**
     * Test of create method, of class ProjectController.
     *
     * @throws db.exceptions.NonexistentEntityException
     */
//    @Test
//    public void testEdit() throws NonexistentEntityException {
//        System.out.println("edit Project Test");
//        ClientController clientController = new ClientController();
//        Client client = new Client("Hans Berger", "Oberhauserweg 3", "Linz", "5020", "+4366098484231");
//        Client client1 = new Client("Musterman", "Feldstraße", "Linz", "4020", "123456789");
//        Client client2 = new Client("Musterfrau", "Landstraße", "Linz", "4020", "987654321");
//        clientController.create(client1);
//        clientController.create(client2);
//        clientController.create(client);
//
//        Project project1 = new Project("TestprojektEdit", "047583", "Notiz", "Haus", "Walmdach", client);
//        ProjectController instance = new ProjectController();
//        instance.create(new Project("Testprojekt1", "8467389", "Notiz", "Haus", "Pultdach", client1));
//        instance.create(new Project("Testprojekt2", "9827245", "Notiz", "Haus", "Pultdach", client2));
//        instance.create(project1);
//        project1.setDescription("Einfaches Kommentar!");
//        int oldCount = instance.getCount();
//        instance.edit(project1);
//
//        assertThat("Projekt wurde nich editiert sondern neu erstellt!", instance.getCount(), is(oldCount));
//        System.out.println(instance.findLastProject().getDescription());
//        assertThat("Projekt wurde nicht geupdatet!", project1.getDescription(), is(instance.findLastProject().getDescription()));
//    }

    /**
     * Test of create method, of class ProjectController.
     */
    @Test
    public void testLastFiveProjects() {
        System.out.println("last five Projects Test");
        Client client = new Client("Hans Berger", "Oberhauserweg 3", "Linz", "5020", "+4366098484231");
        new ClientController().create(client);

        List<Project> pros = new ArrayList<>();
        pros.add(new Project("LVP_01", "48961", "Testkommentar", "Haus", "Walmdach", client));
        pros.add(new Project("LVP_02", "48404", "Testkommentar", "Haus", "Walmdach", client));
        pros.add(new Project("LVP_03", "84841", "Testkommentar", "Haus", "Walmdach", client));
        pros.add(new Project("LVP_04", "78615", "Testkommentar", "Haus", "Walmdach", client));
        pros.add(new Project("LVP_05", "25015", "Testkommentar", "Haus", "Walmdach", client));

        ProjectController pc = new ProjectController();

        pc.create(pros.get(0));
        pc.create(pros.get(2));
        pc.create(pros.get(1));

        assertThat(pc.findLastFiveProjects().size(), is(3));

        pc.create(pros.get(4));
        pc.create(pros.get(3));

        assertThat(pc.findLastFiveProjects().size(), is(5));

        List<Project> last = pc.findLastFiveProjects();

        assertThat("LVP_04 ist nicht an 0. Stelle!", last.get(0).getProjectName(), is("LVP_04"));
        assertThat("LVP_05 ist nicht an 1. Stelle!", last.get(1).getProjectName(), is("LVP_05"));
        assertThat("LVP_02 ist nicht an 2. Stelle!", last.get(2).getProjectName(), is("LVP_02"));
        assertThat("LVP_03 ist nicht an 3. Stelle!", last.get(3).getProjectName(), is("LVP_03"));
        assertThat("LVP_01 ist nicht an 4. Stelle!", last.get(4).getProjectName(), is("LVP_01"));
    }

    /**
     * Test of create method, of class ProjectController.
     */
    @Test
    public void testFindProject() {
        System.out.println("findProject Test");
        Client client = new Client("Hans Berger", "Oberhauserweg 3", "Linz", "5020", "+4366098484231");
        new ClientController().create(client);

        List<Project> pros = new ArrayList<>();
        pros.add(new Project("FP_01", "48961", "Testkommentar", "Haus", "Walmdach", client));
        pros.add(new Project("FP_02", "48404", "Testkommentar", "Haus", "Walmdach", client));

        ProjectController pc = new ProjectController();

        pc.create(pros.get(0));

        assertNotNull("Projekt kann nicht gefunden werden", pc.findProject(pros.get(0).getId()));
        assertThat("Falsches Projekt wurde gefunden", pc.findProject(pros.get(0).getId()).getProjectName(), is(pros.get(0).getProjectName()));

        pc.create(pros.get(1));
        assertNotNull("Projekt kann nicht gefunden werden", pc.findProject(pros.get(1).getId()));
        assertThat("Falsches Projekt wurde gefunden", pc.findProject(pros.get(1).getId()).getProjectName(), is(pros.get(1).getProjectName()));
    }
}
