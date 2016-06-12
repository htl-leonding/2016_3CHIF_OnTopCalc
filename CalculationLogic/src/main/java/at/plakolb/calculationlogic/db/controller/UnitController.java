package at.plakolb.calculationlogic.db.controller;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.entity.Unit;
import at.plakolb.calculationlogic.db.exceptions.NonexistentEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author Kepplinger, Schlömicher
 */
public class UnitController {
    
    EntityManager em;

    public UnitController() {
        em = JpaUtils.getEntityManager();
    }
    
//    private EntityManager getEntityManager(){
//        return JpaUtils.getEntityManager();
//    }

    public void create(Unit unit) {
        em.getTransaction().begin();
        em.persist(unit);
        em.getTransaction().commit();
        em.clear();
    }
    
    public void edit(Unit unit) throws NonexistentEntityException{
        try {
            em.getTransaction().begin();
            unit = em.merge(unit);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = unit.getId();
                if (findUnit(id) == null) {
                    throw new NonexistentEntityException("The unit with id " + id + " no longer exists.");
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
            Unit unit;
            try {
                unit = em.getReference(Unit.class, id);
                unit.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The unit with id " + id + " no longer exists.", enfe);
            }
            em.remove(unit);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public List<Unit> findAll() {
        return findUnitEntities(true, -1, -1);
    }

    public List<Unit> findUnitEntities(int maxResults, int firstResult) {
        return findUnitEntities(false, maxResults, firstResult);
    }

    private List<Unit> findUnitEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Unit.class));
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
    
    public List<Unit> findAllOrderByShortTerm() {
         try { 
            return em.createQuery("select u from Unit u order by u.shortTerm", Unit.class).getResultList();
        } finally {
            em.clear();
        }
    }
    
    public Unit findUnitByShortTerm(String shortTerm) {
         try { 
            return em.createQuery("select u from Unit u where u.shortTerm = :shortTerm", Unit.class).
                    setParameter("shortTerm", shortTerm).
                    getSingleResult();
        } finally {
            em.clear();
        }
    }

    public Unit findUnit(Long id) {
        try {
            return em.find(Unit.class, id);
        } finally {
            em.clear();
        }
    }

    public int getUnitCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Unit> rt = cq.from(Unit.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
}
