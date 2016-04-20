package at.plakolb.calculationlogic.db.controller;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;
import at.plakolb.calculationlogic.entity.Component;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Kepplinger, Schlömicher
 */
public class ComponentController {
    
    EntityManager em;

    public ComponentController() {
        em = JpaUtils.getEntityManager();
    }
    
//    private EntityManager getEntityManager(){
//        return JpaUtils.getEntityManager();
//    }

    public void create(Component component) {
        try {
            em.getTransaction().begin();
            em.persist(component);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(Component component) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            component = em.merge(component);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = component.getId();
                if (findComponent(id) == null) {
                    throw new NonexistentEntityException("The component with id " + id + " no longer exists.");
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
            Component component;
            try {
                component = em.getReference(Component.class, id);
                component.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The component with id " + id + " no longer exists.", enfe);
            }
            em.remove(component);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }
    
    public List<Component> findAll() {
        return findComponentEntities(true, -1, -1);
    }

    public List<Component> findComponentEntities(int maxResults, int firstResult) {
        return findComponentEntities(false, maxResults, firstResult);
    }

    private List<Component> findComponentEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Component.class));
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

    public List<Component> findComponentsByProjectId(long projectId) {
        try {
            return em.createQuery("select c from Component c where c.project.id = :id order by c.category.shortTerm").
                    setParameter("id", projectId).
                    getResultList();
        } finally {
            em.clear();
        }
    }

    public List<Component> findComponentsByProjectIdAndComponentType(long projectId, String type) {
        try {
            return em.createQuery("select c from Component c where c.project.id = :id and c.componentType = :type order by c.category.longTerm").
                    setParameter("id", projectId).
                    setParameter("type", type).
                    getResultList();
        } finally {
            em.clear();
        }
    }

    public Component findComponentByProjectIdAndComponentTypeAndCategoryId(long projectId, String type, long categoryId) {
        try {
            return (Component) em.createNativeQuery("select * from Component c where c.project_id = ? and c.componentType = ? and c.category_id = ?", Component.class).
                    setParameter(1, projectId).
                    setParameter(2, type).
                    setParameter(3, categoryId).
                    getSingleResult();
        } catch (Exception ex) {
            return null;
        } finally {
            em.clear();
        }
    }

    public List<Component> findComponentsByProjectIdOrderById(long projectId) {
        try {
            return em.createQuery("select c from Component c where c.project.id = :id order by c.id").
                    setParameter("id", projectId).
                    getResultList();
        } finally {
            em.clear();
        }
    }
    
    public List<Component> findComponentsByProjectIdWithAssembly(long projectId) {
        try {
            return em.createNativeQuery("select * from Component c where c.project_id = ? and c.id in (select a.component_id from Assembly a where a.project_id = ?)").
                    setParameter("id", projectId).
                    setParameter("id", projectId).
                    getResultList();
        } finally {
            em.clear();
        }
    }
    
    public List<Component> findComponentsByProjectIdWithoutAssembly(long projectId) {
        try {
            return em.createNativeQuery("select * from Component c where c.project_id = ? and c.id not in (select a.component_id from Assembly a where a.project_id = ?)").
                    setParameter("id", projectId).
                    setParameter("id", projectId).
                    getResultList();
        } finally {
            em.clear();
        }
    }

    public Component findColorByProjectId(long projectId) {
        try {
            return (Component) em.createNativeQuery("select * from Component c where c.project_id = ? and c.componenttype = 'Farbe'", Component.class).
                    setParameter(1, projectId).
                    getSingleResult();
        } catch (Exception ex) {
            return null;
        } finally {
            em.clear();
        }
    }
    
    public Component findComponentColorByProjectId(long projectId) {
        try {
            return (Component) em.createNativeQuery("select * from Component c where c.project_id = ? and c.componenttype = 'Farbe'", Component.class).
                    setParameter(1, projectId).
                    getSingleResult();
        } catch (Exception ex) {
            return null;
        } finally {
            em.clear();
        }
    }

    public Component findComponent(Long id) {
        try {
            return em.find(Component.class, id);
        } finally {
            em.clear();
        }
    }

    public int getComponentCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Component> rt = cq.from(Component.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
