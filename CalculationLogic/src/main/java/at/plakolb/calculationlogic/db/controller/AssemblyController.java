package at.plakolb.calculationlogic.db.controller;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Assembly;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Project;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Kepplinger, Schlömicher
 */
public class AssemblyController {
    
    EntityManager em;

    public AssemblyController() {
        em = JpaUtils.getEntityManager();
    }
    
//    private EntityManager getEntityManager(){
//        return JpaUtils.getEntityManager();
//    }

     public void create(Assembly assembly) {
        try {
            em.getTransaction().begin();
            Project project = assembly.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                assembly.setProject(project);
            }
            em.persist(assembly);
//            if (project != null) {
//                project.getAssemblys().add(assembly);
//                project = em.merge(project);
//            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(Assembly assembly) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            Assembly persistentAssembly = em.find(Assembly.class, assembly.getId());
            Project projectOld = persistentAssembly.getProject();
            Project projectNew = assembly.getProject();
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                assembly.setProject(projectNew);
            }
            assembly = em.merge(assembly);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = assembly.getId();
                if (findAssembly(id) == null) {
                    throw new NonexistentEntityException("The assembly with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        try {
            em.getTransaction().begin();
            Assembly assembly;
            try {
                assembly = em.getReference(Assembly.class, id);
                assembly.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The assembly with id " + id + " no longer exists.", enfe);
            }
            Component component = assembly.getComponent();
            if(component != null) {
                component.getAssemblys().remove(assembly);
                component = em.merge(component);
            }
            em.remove(assembly);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public List<Assembly> findAll() {
        return findAssemblyEntities(true, -1, -1);
    }

    public List<Assembly> findAssemblyEntities(int maxResults, int firstResult) {
        return findAssemblyEntities(false, maxResults, firstResult);
    }

    private List<Assembly> findAssemblyEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Assembly.class));
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

    public Assembly findAssembly(Long id) {
        try {
            return em.find(Assembly.class, id);
        } finally {
            em.clear();
        }
    }
    
    public List<Assembly> findAssembliesByProjectId(long projectId) {
        try {
            return em.createQuery("select a from Assembly a where a.project.id = :id").
                    setParameter("id", projectId).
                    getResultList();
        } finally {
            em.clear();
        }
    }

    public int getAssemblyCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Assembly> rt = cq.from(Assembly.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
