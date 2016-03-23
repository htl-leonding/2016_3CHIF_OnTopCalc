package at.plakolb.calculationlogic.db.controller;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Project;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Schlömicher
 */
public class ClientController {
    
    EntityManager em;

    public ClientController() {
        em = JpaUtils.getEntityManager();
    }

    /***
     * Create a new Client
     * @param client 
     */
    public void create(Client client) {
        if (client.getProjects() == null) {
            client.setProjects(new ArrayList<Project>());
        }
        try {
            em.getTransaction().begin();
            List<Project> attachedProjects = new ArrayList<>();
            for (Project projectsProjectToAttach : client.getProjects()) {
                projectsProjectToAttach = em.getReference(projectsProjectToAttach.getClass(), projectsProjectToAttach.getId());
                attachedProjects.add(projectsProjectToAttach);
            }
            client.setProjects(attachedProjects);
            em.persist(client);
            for (Project projectsProject : client.getProjects()) {
                Client oldClientOfProjectsProject = projectsProject.getClient();
                projectsProject.setClient(client);
                projectsProject = em.merge(projectsProject);
                if (oldClientOfProjectsProject != null) {
                    oldClientOfProjectsProject.getProjects().remove(projectsProject);
                    oldClientOfProjectsProject = em.merge(oldClientOfProjectsProject);
                }
            }
            em.getTransaction().commit();
        }
        finally{
           em.clear(); 
        }
    }
    
    public List<Client> findAll() {
        return findClientEntities(true, -1, -1);
    }

    public List<Client> findClientEntities(int maxResults, int firstResult) {
        return findClientEntities(false, maxResults, firstResult);
    }

    private List<Client> findClientEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Client.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.clear();
        }
    }

    /***
     * Delete a Client
     * @param id
     * @throws NonexistentEntityException 
     */
    public void delete(Long id) throws NonexistentEntityException{
        try {
            em.getTransaction().begin();
            Client client;
            try {
                client = em.getReference(Client.class, id);
                client.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The client with id " + id + " no longer exists.", enfe);
            }
            List<Project> projects = client.getProjects();
            for (Project projectsProject : projects) {
                projectsProject.setClient(null);
                projectsProject = em.merge(projectsProject);
            }
            em.remove(client);
            em.getTransaction().commit();
        } finally {
            em.clear();
        }
    }
    
    public void edit(Client client) throws NonexistentEntityException{
        try {
            em.getTransaction().begin();
            Client persistentClient = em.find(Client.class, client.getId());
            List<Project> projectsOld = persistentClient.getProjects();
            List<Project> projectsNew = client.getProjects();
            List<Project> attachedProjectsNew = new ArrayList<>();
            for (Project projectsNewProjectToAttach : projectsNew) {
                projectsNewProjectToAttach = em.getReference(projectsNewProjectToAttach.getClass(), projectsNewProjectToAttach.getId());
                attachedProjectsNew.add(projectsNewProjectToAttach);
            }
            projectsNew = attachedProjectsNew;
            client.setProjects(projectsNew);
            client = em.merge(client);
            for (Project projectsOldProject : projectsOld) {
                if (!projectsNew.contains(projectsOldProject)) {
                    projectsOldProject.setClient(null);
                    projectsOldProject = em.merge(projectsOldProject);
                }
            }
            for (Project projectsNewProject : projectsNew) {
                if (!projectsOld.contains(projectsNewProject)) {
                    Client oldClientOfProjectsNewProject = projectsNewProject.getClient();
                    projectsNewProject.setClient(client);
                    projectsNewProject = em.merge(projectsNewProject);
                    if (oldClientOfProjectsNewProject != null && !oldClientOfProjectsNewProject.equals(client)) {
                        oldClientOfProjectsNewProject.getProjects().remove(projectsNewProject);
                        oldClientOfProjectsNewProject = em.merge(oldClientOfProjectsNewProject);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = client.getId();
                if (findClient(id) == null) {
                    throw new NonexistentEntityException("The client with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }
    
    public Client findClient(Long id) {
        try {
            return em.find(Client.class, id);
        } finally {
            em.clear();
        }
    }

    public int getClientCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Client> rt = cq.from(Client.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
