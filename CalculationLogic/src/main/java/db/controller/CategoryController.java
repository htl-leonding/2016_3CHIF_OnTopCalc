package db.controller;

import db.JpaUtils;
import db.exceptions.NonexistentEntityException;
import entity.Category;
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
public class CategoryController {
    
    EntityManager em;

    public CategoryController() {
        em = JpaUtils.getEntityManager();
    }
    
//    private EntityManager getEntityManager(){
//        return JpaUtils.getEntityManager();
//    }

    public void create(Category category) {
        try {
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(Category category) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            category = em.merge(category);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = category.getId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
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
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            em.remove(category);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public List<Category> findAll() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
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

    public Category findCategory(Long id) {
        try {
            return em.find(Category.class, id);
        } finally {
            em.clear();
        }
    }

    public List<Category> findAllOrderByShortTerm() {
        try {
            return em.createQuery("select c from Category c order by c.shortTerm", Category.class).getResultList();
        } finally {
            em.clear();
        }
    }
    
    public Category findCategoryByShortTerm(String shortTerm) {
        try {
            return em.createQuery("select c from Category c where c.shortTerm = :shortTerm", Category.class).
                    setParameter("shortTerm", shortTerm).getSingleResult();
        } catch(Exception ex) {
            return null;
        }finally {
            em.clear();
        }
    }

    public int getCategoryCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
