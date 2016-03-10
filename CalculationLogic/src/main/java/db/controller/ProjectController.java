package db.controller;

import db.JpaUtils;
import db.exceptions.NonexistentEntityException;
import entity.Client;
import entity.Project;
import entity.Worth;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Kepplinger
 */
public class ProjectController {

    EntityManager em;

    public ProjectController() {
        em = JpaUtils.getEntityManager();
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

    public List<Project> findAll() {
        try {
            em = JpaUtils.getEntityManager();
            TypedQuery<Project> query = em.createNamedQuery("Project.findAll", Project.class);
            List<Project> projects = query.getResultList();
            System.out.println(projects);
            return projects;
        } finally {
            em.close();
        }
    }

    //TODO
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
        }
    }

    //TODO
    public void sendToRecyclebin(long projectId) {

    }

    //TODO
    public void delete(long projectId) {

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
            em.close();
        }
    }

    public Project findProject(long id) {
        em = JpaUtils.getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public List<Project> findProjectsByDeletion(boolean deletion) {
        em = JpaUtils.getEntityManager();
        try {
            return em.createNativeQuery("select * from Project where deletion = ? order by lastUpdate desc", Project.class).
                    setParameter(1, deletion).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Project> findLastFiveProjects() {
        try {
            em = JpaUtils.getEntityManager();
            return em.createNativeQuery("select * from Project p where deletion = 0 order by p.lastUpdate desc", Project.class).
                    setFirstResult(0).setMaxResults(5).getResultList();
        } finally {
            em.close();
        }
    }

    public Project findLastProject() {
        em = JpaUtils.getEntityManager();
        try {
            return (Project) em.createNativeQuery("select * from Project where id = (select max(id) from Project p where p.lastupdate = (select  max(pp.lastupdate) from Project pp))",
                    Project.class).
                    getSingleResult();
        } finally {
            em.close();
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
            em.close();
        }
    }
}
