package db.controller;

import db.JpaUtils;
import db.exceptions.NonexistentEntityException;
import entity.Product;
import eunmeration.ProductType;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Kepplinger
 */
public class ProductController {
    
    EntityManager em;

    public ProductController() {
        em = JpaUtils.getEntityManager();
    }

    public void create(Product product) {
        try {
            em.getTransaction().begin();
            em.persist(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public void edit(Product product) throws NonexistentEntityException, Exception {
        try {
            em.getTransaction().begin();
            product = em.merge(product);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = product.getId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
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
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    public List<Product> findAll() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Long id) {
        try {
            return em.find(Product.class, id);
        } finally {
            em.clear();
        }
    }

    public List<Product> findAllOrderByName() {
        try {
            return em.createQuery("select p from Product p order by p.name", Product.class).getResultList();
        } finally {
            em.clear();
        }
    }
    
    public List<Product> findByProductTypeOrderByName(ProductType productType) {
        try {
            //Noch von Pilz Elisabeth
            /*
            return em.createNativeQuery("select * from Product p where p.productType = ?  order by case when regexp_instr(name,'[0-9]+') = 1 "
                    + "then to_number(regexp_substr(name,'[0-9]+')) else null end nulls last, name", Product.class).
                    setParameter(1, productType).
                    getResultList();
            */
            
            
            if (productType == null) {
                return null;
            }
            return em
                    .createQuery("select p from Product p where p.productType = ?1 order by p.name", Product.class)
                    .setParameter(1, productType)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Product findByName(String name) {
        try {
            return  (Product) em.createNativeQuery("select * from Product p where p.name = ?", Product.class).
                    setParameter(1, name).
                    getSingleResult();
        } finally {
            em.clear();
        }
    }

    public int getProductCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.clear();
        }
    }

    public int getPositionProductByName(String name, String type) {
        try {
            return ((BigDecimal) em.createNativeQuery("select count(*) from Product p where lower(name) <= ?  and p.producttype = ?").
                    setParameter(1, name.toLowerCase()).
                    setParameter(2, type).
                    getSingleResult()).intValue() - 1;
        } catch (Exception ex) {
            System.err.println(ex);
            return -1;
        } finally {
            em.clear();
        }
    }
}
