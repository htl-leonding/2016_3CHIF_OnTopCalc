/*	HTL Leonding	*/
package db.controller;

import db.JpaUtils;
import db.exceptions.NonexistentEntityException;
import entity.ParameterP;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Andreas
 */
public class ParameterController {
    EntityManager em;
    
    public ParameterController(){
        em = JpaUtils.getEntityManager();
    }
    
    public void create(ParameterP parameterP) {
        try {
            em.getTransaction().begin();
            em.persist(parameterP);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(ParameterP parameterP) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            parameterP = em.merge(parameterP);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = parameterP.getId();
                if (findParameterP(id) == null) {
                    throw new NonexistentEntityException("The parameterP with id " + id + " no longer exists.");
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
            ParameterP parameterP;
            try {
                parameterP = em.getReference(ParameterP.class, id);
                parameterP.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The parameterP with id " + id + " no longer exists.", enfe);
            }
            em.remove(parameterP);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public List<ParameterP> findAll() {
        return findParameterPEntities(true, -1, -1);
    }

    public List<ParameterP> findParameterPEntities(int maxResults, int firstResult) {
        return findParameterPEntities(false, maxResults, firstResult);
    }

    private List<ParameterP> findParameterPEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ParameterP.class));
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

    public ParameterP findParameterP(Long id) {
        try {
            return em.find(ParameterP.class, id);
        } finally {
            em.clear();
        }
    }

    public ParameterP findParameterPByShortTerm(String shortTerm) {
        try {
            return (ParameterP) em.createQuery("select p from ParameterP p where p.shortTerm = :shortTerm").
                    setParameter("shortTerm", shortTerm).
                    getSingleResult();
        } catch (Exception ex) {
            return null;
        }
         finally {
            em.clear();
        }
    }

    public List<ParameterP> findEditableParameter() {
        try {
            return em.createNativeQuery("select * from ParameterP p where p.editable = 1", ParameterP.class).getResultList();
        } finally {
            em.clear();
        }
    }

    public int getParameterPCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ParameterP> rt = cq.from(ParameterP.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
