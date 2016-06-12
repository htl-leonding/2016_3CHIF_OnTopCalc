package at.plakolb.calculationlogic.db.controller;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kepplinger
 */
public class ProjectController {

    EntityManager em;

    public ProjectController() {
    }

//    private EntityManager getEntityManager(){
//        return JpaUtils.getEntityManager();
//    }
    public void create(Project project) {
        try {
            em = JpaUtils.getEntityManager();
            em.getTransaction().begin();
            Client client = project.getClient();
            if (client != null) {
                client = em.getReference(client.getClass(), client.getId());
                project.setClient(client);
            }
            em.persist(project);
            if (client != null) {
                client.getProjects().add(project);
                client = em.merge(client);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    //TODO
    //Auf Korrektheit überprüfen
    public void createCosting(Project project, long originalProjectId) {
        try {
            create(project);

            em = JpaUtils.getEntityManager();
            em.getTransaction().begin();

            if (project.getWorths().size() > 0) {
                List<Worth> worths = project.getWorths();
                project.setWorths(new ArrayList<Worth>());
                for (Worth worth : worths) {
                    em.persist(new Worth(project, worth.getParameter(), worth.getWorth()));
                }
            }
            ComponentController componentJpaController = new ComponentController();
            List<Component> listComponents = componentJpaController.findComponentsByProjectId(originalProjectId);
            if (listComponents.size() > 0) {
                for (Component component : listComponents) {
                    Component componentNew = new Component(component.getDescription(), component.getWidthComponent(),
                            component.getHeightComponent(), component.getLengthComponent(), component.getPriceComponent(),
                            component.getNumberOfProducts(), component.getTailoringHours(), component.getTailoringPricePerHour(),
                            component.getComponentType(), component.getCategory(), component.getUnit(), component.getProduct(),
                            project);
                    em.persist(componentNew);
                    for (Assembly assembly : component.getAssemblys()) {
                        Assembly assemblyNew = new Assembly(assembly.getProduct(),
                                componentNew, project, assembly.getNumberOfComponents(), assembly.getPrice());
                        em.persist(assemblyNew);
                    }

                }
            }
            em.merge(project);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Copy a project
     *
     * @param p
     * @param originalProjectId
     */
    public void copy(Project p, long originalProjectId) {
        createCosting(p, originalProjectId);
    }

    public List<Project> findAll() {
        try {
            em = JpaUtils.getEntityManager();
            TypedQuery<Project> query = em.createNamedQuery("Project.findAll", Project.class);
            List<Project> projects = query.getResultList();
            return projects;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project project) throws NonexistentEntityException {
        try {
            em = JpaUtils.getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, project.getId());
            Client clientOld = persistentProject.getClient();
            Client clientNew = project.getClient();
            List<Worth> worthsOld = persistentProject.getWorths();
            List<Worth> worthsNew = project.getWorths();
            if (clientNew != null) {
                clientNew = em.getReference(clientNew.getClass(), clientNew.getId());
                project.setClient(clientNew);
            }
            List<Worth> attachedWorthsNew = new ArrayList<>();
            for (Worth worthsNewWorthToAttach : worthsNew) {
                worthsNewWorthToAttach = em.getReference(worthsNewWorthToAttach.getClass(), worthsNewWorthToAttach.getId());
                attachedWorthsNew.add(worthsNewWorthToAttach);
            }
            worthsNew = attachedWorthsNew;
            project.setWorths(worthsNew);
            project.setLastUpdate(LocalDateTime.now(Clock.systemDefaultZone()));
            project = em.merge(project);
            if (clientOld != null && !clientOld.equals(clientNew)) {
                clientOld.getProjects().remove(project);
                clientOld = em.merge(clientOld);
            }
            if (clientNew != null && !clientNew.equals(clientOld)) {
                clientNew.getProjects().add(project);
                clientNew = em.merge(clientNew);
            }
            for (Worth worthsOldWorth : worthsOld) {
                if (!worthsNew.contains(worthsOldWorth)) {
                    worthsOldWorth.setProject(null);
                    worthsOldWorth = em.merge(worthsOldWorth);
                }
            }
            for (Worth worthsNewWorth : worthsNew) {
                if (!worthsOld.contains(worthsNewWorth)) {
                    Project oldProjectOfWorthsNewWorth = worthsNewWorth.getProject();
                    worthsNewWorth.setProject(project);
                    worthsNewWorth = em.merge(worthsNewWorth);
                    if (oldProjectOfWorthsNewWorth != null && !oldProjectOfWorthsNewWorth.equals(project)) {
                        oldProjectOfWorthsNewWorth.getWorths().remove(worthsNewWorth);
                        oldProjectOfWorthsNewWorth = em.merge(oldProjectOfWorthsNewWorth);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = project.getId();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void sendToRecyclebin(long projectId) throws NonexistentEntityException {
        Project p = findProject(projectId);
        p.setDeletion(true);
        edit(p);
    }

    public boolean delete(long projectId) {
        try {
            WorthController worthController = new WorthController();
            for (Worth worth : findProject(projectId).getWorths()) {
                if (worth.getId() != null) {
                    worthController.destroy(worth.getId());
                }
            }

            AssemblyController assemblyController = new AssemblyController();
            for (Assembly assembly : assemblyController.findAssembliesByProjectId(projectId)) {
                if (assembly.getId() != null) {
                    assemblyController.destroy(assembly.getId());
                }
            }

            ComponentController componentController = new ComponentController();
            for (Component component : componentController.findComponentsByProjectId(projectId)) {
                if (component.getId() != null) {
                    componentController.destroy(component.getId());
                }
            }

            em = JpaUtils.getEntityManager();
            em.getTransaction().begin();

            int i = em.createNativeQuery("delete from Project p where p.id = ?").
                    setParameter(1, projectId).executeUpdate();
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjects() {
        return findProjects(true, -1, -1);
    }

    public List<Project> findProjects(int maxResults, int firstResult) {
        return findProjects(false, maxResults, firstResult);
    }

    public List<Project> findProjects(boolean all, int maxResults, int firstResult) {
        em = JpaUtils.getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Project findProject(long id) {
        em = JpaUtils.getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectsByDeletion(boolean deletion) {
        em = JpaUtils.getEntityManager();
        try {
            return em.createNativeQuery("select * from Project where deletion = ? order by lastUpdate desc", Project.class).
                    setParameter(1, deletion).getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findLastFiveProjects() {
        try {
            em = JpaUtils.getEntityManager();
            return em.createNativeQuery("select * from Project p where deletion = 0 order by p.lastUpdate desc", Project.class).
                    setFirstResult(0).setMaxResults(5).getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Project findLastProject() {
        em = JpaUtils.getEntityManager();
        try {
            return (Project) em.createNativeQuery("select * from Project where id = (select max(id) from Project p where p.lastupdate = (select  max(pp.lastupdate) from Project pp))",
                    Project.class).
                    getSingleResult();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    //TODO
    public int getCount() {
        try {
            em = JpaUtils.getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
