package db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kepplinger
 */
public class JpaUtils {
    
    private static final String defautlPuName = "calculationPU";
    private static EntityManagerFactory emf;
    private static String puUnitName;
    
    public static EntityManager getEntityManager() {
        if (emf == null) {
            if (puUnitName == null) {
                puUnitName = defautlPuName;  
            }
            emf = Persistence.createEntityManagerFactory(puUnitName);
            System.out.println("PUunit: " + puUnitName);
        }
        return emf.createEntityManager();
    }

    public static EntityManager getEntityManager(String puName) {
        if (emf == null) {
            puUnitName = puName;
            emf = Persistence.createEntityManagerFactory(puName);
        }
        return emf.createEntityManager();
    }

    public static void shutdown() {
        if (emf != null) {
            emf.close();
        }
    }
}
