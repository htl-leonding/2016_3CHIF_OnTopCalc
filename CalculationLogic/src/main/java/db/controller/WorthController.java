/*	HTL Leonding	*/
package db.controller;

import db.JpaUtils;
import db.exceptions.NonexistentEntityException;
import entity.ParameterP;
import entity.Project;
import entity.Worth;
import java.math.BigDecimal;
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
public class WorthController {
    EntityManager em;
    
    public WorthController(){
        em = JpaUtils.getEntityManager();
    }
    
    public void create(Worth worth) {
        try {
            em.getTransaction().begin();
            Project project = worth.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                worth.setProject(project);
            }
            em.persist(worth);
            if (project != null) {
                project.getWorths().add(worth);
                project = em.merge(project);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(Worth worth) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            Worth persistentWorth = em.find(Worth.class, worth.getId());
            Project projectOld = persistentWorth.getProject();
            Project projectNew = worth.getProject();
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                worth.setProject(projectNew);
            }
            worth = em.merge(worth);
            if (projectOld != null && !projectOld.equals(projectNew)) {
                projectOld.getWorths().remove(worth);
                projectOld = em.merge(projectOld);
            }
            if (projectNew != null && !projectNew.equals(projectOld)) {
                projectNew.getWorths().add(worth);
                projectNew = em.merge(projectNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = worth.getId();
                if (findWorth(id) == null) {
                    throw new NonexistentEntityException("The worth with id " + id + " no longer exists.");
                }
            }
            //logger.debug(ex.getMessage());    //TODO
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
            Worth worth;
            try {
                worth = em.getReference(Worth.class, id);
                worth.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The worth with id " + id + " no longer exists.", enfe);
            }
            Project project = worth.getProject();
            if (project != null) {
                project.getWorths().remove(worth);
                project = em.merge(project);
            }
            em.remove(worth);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public Worth findWorthByShortTermAndProjectId(String shortTerm, long projectId) {
        try {
//            return (Worth) em
//                    .createNativeQuery("select * from Worth w inner join ParameterP p on(w.parameter_id = p.id) where w.project_id = ? and p.shortterm = ?", Worth.class)
//                    .setParameter(1, projectId)
//                    .setParameter(2, shortTerm)
//                    .getSingleResult();
            return em
                    .createQuery("select w from Worth w where w.shortTerm = :SHORTTERM and w.project.id = :PROJECTID", Worth.class)
                    .setParameter("SHORTTERM", shortTerm)
                    .setParameter("PROJECTID", projectId)
                    .getSingleResult();
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return null;
        } finally {
            em.clear();
        }
    }
    
    public Worth findWorthByShortTermAndProjectIdAndWorthShortTermIsNull(String shortTerm, long projectId) {
        try {
            return (Worth) em.createNativeQuery("select * from Worth w inner join ParameterP p on(w.parameter_id = p.id) where w.project_id = ? and p.shortterm = ? and w.shortterm is null", Worth.class).
                    setParameter(1, projectId).
                    setParameter(2, shortTerm).getSingleResult();
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return null;
        } finally {
            em.clear();
        }
    }

    public Worth findWorthByWhorthShortTermAndProjectId(String shortTerm, long projectId) {
        try {
            return (Worth) em.createNativeQuery("select * from Worth w where w.project_id = ? and w.shortterm = ?", Worth.class).
                    setParameter(1, projectId).
                    setParameter(2, shortTerm).getSingleResult();
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return null;
        } finally {
            em.clear();
        }
    }
    
    public Worth summarizeAllWorths(Project project, ParameterP parameter) {
        BigDecimal worthTotal = (BigDecimal) em
                .createQuery("select sum(w.worth) from Worth w where w.project = :PROJECT and w.shortTerm LIKE :SHORTERM")
                .setParameter("PROJECT", project)
                .setParameter("SHORTTERM", parameter.getShortTerm() + '%')
                .getSingleResult();
        
        Worth w = new Worth(project, parameter, worthTotal.doubleValue());
        return w;
    }

    public int countWorthCountLengthByProject(long projectId) {
        try {
            Object object = em.createNativeQuery("select count(*) from Worth w where w.shortterm like 'l%' and w.project_id = ?").
                    setParameter(1, projectId).
                    getSingleResult();
            return ((BigDecimal) object).intValue();
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return 0;
        } finally {
            em.clear();
        }
    }

    public Worth findWorthByParameterIdAndProjectId(long parameterId, long projectId) {
        try {
            return (Worth) em.createNativeQuery("select * from Worth w where w.parameter_id = ? and w.project_id = ?", Worth.class).
                    setParameter(1, parameterId).
                    setParameter(2, projectId).getSingleResult();
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return null;
        } finally {
            em.clear();
        }
    }
    
    public boolean deletePreCutIndexFromProject(long projectId, int index) {
        try {
            em.getTransaction().begin();
           int i = em.createNativeQuery("delete from worth w where w.shortTerm like '%' || ? and w.project_id = ?").
                    setParameter(1, index).
                    setParameter(2, projectId).executeUpdate();
            em.getTransaction().commit();
           return true;
        } catch (Exception ex) {
            //logger.debug(ex.getMessage());    TODO
            return false;
        } finally {
            em.clear();
        }
    }

    public List<Worth> findAll() {
        return findWorthEntities(true, -1, -1);
    }

    public List<Worth> findWorthEntities(int maxResults, int firstResult) {
        return findWorthEntities(false, maxResults, firstResult);
    }

    private List<Worth> findWorthEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Worth.class));
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

    public Worth findWorth(Long id) {
        try {
            return em.find(Worth.class, id);
        } finally {
            em.clear();
        }
    }

    public int getWorthCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Worth> rt = cq.from(Worth.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }
    
    /**
     * 
     * Wrd kein Index mitgegeben, wird nach einer Property mit Index gesucht, ansonsten ohne.
     * Dies wird auch beim Speichern überprüft
     * 
     * @param project
     * @param parameter
     * @param worthValue
     * @param index
     * @return
     * @throws Exception 
     */
    public Worth insertOrUpdate(Project project, ParameterP parameter, double worthValue, Integer index) throws Exception {
        Worth worth = null;
        if (index == null) {
            worth = this.findWorthByParameterIdAndProjectId(parameter.getId(), project.getId());
        } else {
            worth = this.findWorthByShortTermAndProjectId(parameter.getShortTerm() + index, project.getId());
        }
        if (worth == null) {
            worth = new Worth(project, parameter, worthValue, (index == null ? null : parameter.getShortTerm() + index));
            this.create(worth);
        } else {
            worth.setWorth(worthValue);
            this.edit(worth);
        }
        return worth;
    }
}
