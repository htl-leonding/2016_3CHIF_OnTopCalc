/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.db.JpaUtils;
import at.plakolb.calculationlogic.db.entity.Assembly;
import at.plakolb.calculationlogic.db.entity.Category;
import at.plakolb.calculationlogic.db.entity.Client;
import at.plakolb.calculationlogic.db.entity.Component;
import at.plakolb.calculationlogic.db.entity.ParameterP;
import at.plakolb.calculationlogic.db.entity.Product;
import at.plakolb.calculationlogic.db.entity.Project;
import at.plakolb.calculationlogic.db.entity.Unit;
import at.plakolb.calculationlogic.db.entity.Worth;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Andreas
 */
public class BackUpDatabase extends Observable {

    public static final String CRYPTOKEY = "L1FvSx1XCwAadVR4";

    private EntityManager em;
    private String filename;
    private String endpath;
    private static final String FILETYPE = ".OTCdb";

    public BackUpDatabase(String path) {
        SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss");
        filename = "backup" + sdf.format(new Date());
        if (path.isEmpty()) {
            path = System.getProperty("user.home");
        }
        endpath = path;
        em = JpaUtils.getEntityManager();
    }

    public int exp() {
        try {
            String dir = System.getProperty("user.dir");

            setChanged();
            notifyObservers(5);

            em.getTransaction().begin();

            Query query1 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query1.setParameter(1, null);
            query1.setParameter(2, "ASSEMBLY");
            query1.setParameter(3, dir + "/ASSEMBLY" + FILETYPE);
            query1.setParameter(4, "%");
            query1.setParameter(5, null);
            query1.setParameter(6, null);
            query1.executeUpdate();

            setChanged();
            notifyObservers(9);

            Query query2 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query2.setParameter(1, null);
            query2.setParameter(2, "CATEGORY");
            query2.setParameter(3, dir + "/CATEGORY" + FILETYPE);
            query2.setParameter(4, "%");
            query2.setParameter(5, null);
            query2.setParameter(6, null);
            query2.executeUpdate();

            setChanged();
            notifyObservers(18);

            Query query3 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query3.setParameter(1, null);
            query3.setParameter(2, "COMPONENT");
            query3.setParameter(3, dir + "/COMPONENT" + FILETYPE);
            query3.setParameter(4, "%");
            query3.setParameter(5, null);
            query3.setParameter(6, null);
            query3.executeUpdate();

            setChanged();
            notifyObservers(27);

            Query query4 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query4.setParameter(1, null);
            query4.setParameter(2, "PARAMETERP");
            query4.setParameter(3, dir + "/PARAMETERP" + FILETYPE);
            query4.setParameter(4, "%");
            query4.setParameter(5, null);
            query4.setParameter(6, null);
            query4.executeUpdate();

            setChanged();
            notifyObservers(36);

            Query query5 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query5.setParameter(1, null);
            query5.setParameter(2, "PRODUCT");
            query5.setParameter(3, dir + "/PRODUCT" + FILETYPE);
            query5.setParameter(4, "%");
            query5.setParameter(5, null);
            query5.setParameter(6, null);
            query5.executeUpdate();

            setChanged();
            notifyObservers(45);

            Query query6 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query6.setParameter(1, null);
            query6.setParameter(2, "PROJECT");
            query6.setParameter(3, dir + "/PROJECT" + FILETYPE);
            query6.setParameter(4, "%");
            query6.setParameter(5, null);
            query6.setParameter(6, null);
            query6.executeUpdate();

            setChanged();
            notifyObservers(54);

            Query query7 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query7.setParameter(1, null);
            query7.setParameter(2, "UNIT");
            query7.setParameter(3, dir + "/UNIT" + FILETYPE);
            query7.setParameter(4, "%");
            query7.setParameter(5, null);
            query7.setParameter(6, null);
            query7.executeUpdate();

            setChanged();
            notifyObservers(63);

            Query query8 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query8.setParameter(1, null);
            query8.setParameter(2, "WORTH");
            query8.setParameter(3, dir + "/WORTH" + FILETYPE);
            query8.setParameter(4, "%");
            query8.setParameter(5, null);
            query8.setParameter(6, null);
            query8.executeUpdate();

            setChanged();
            notifyObservers(72);

            Query query9 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            query9.setParameter(1, null);
            query9.setParameter(2, "CLIENT");
            query9.setParameter(3, dir + "/CLIENT" + FILETYPE);
            query9.setParameter(4, "%");
            query9.setParameter(5, null);
            query9.setParameter(6, null);
            query9.executeUpdate();

            setChanged();
            notifyObservers(81);

            em.getTransaction().commit();

            move();

            setChanged();
            notifyObservers(100);
            return 3;
        } catch (SQLException ex) {
            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (IOException ex) {
            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        } catch (Exception ex) {
            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return -3;
        } finally {
            if (em != null) {
                em.clear();
            }
        }
    }

    private void move() throws IOException, Exception {
        String dir = System.getProperty("user.dir");

        File result = new File(endpath + "/" + filename);
        if (!result.exists()) {
            result.mkdir();

            File assembly = new File(dir + "/ASSEMBLY" + FILETYPE);
            File category = new File(dir + "/CATEGORY" + FILETYPE);
            File client = new File(dir + "/CLIENT" + FILETYPE);
            File component = new File(dir + "/COMPONENT" + FILETYPE);
            File parameterp = new File(dir + "/PARAMETERP" + FILETYPE);
            File product = new File(dir + "/PRODUCT" + FILETYPE);
            File project = new File(dir + "/PROJECT" + FILETYPE);
            File unit = new File(dir + "/UNIT" + FILETYPE);
            File worth = new File(dir + "/WORTH" + FILETYPE);

//            encrypt(CRYPTOKEY, assembly, new File(dir + "/crypto" + assembly.getName()));
//            encrypt(CRYPTOKEY, category, new File(dir + "/crypto" + category.getName()));
//            encrypt(CRYPTOKEY, client, new File(dir + "/crypto" + client.getName()));
//            encrypt(CRYPTOKEY, component, new File(dir + "/crypto" + component.getName()));
//            encrypt(CRYPTOKEY, parameterp, new File(dir + "/crypto" + parameterp.getName()));
//            encrypt(CRYPTOKEY, product, new File(dir + "/crypto" + product.getName()));
//            encrypt(CRYPTOKEY, project, new File(dir + "/crypto" + project.getName()));
//            encrypt(CRYPTOKEY, unit, new File(dir + "/crypto" + unit.getName()));
//            encrypt(CRYPTOKEY, worth, new File(dir + "/crypto" + worth.getName()));
//            assembly = new File(dir + "/cryptoASSEMBLY" + FILETYPE);
//            category = new File(dir + "/cryptoCATEGORY" + FILETYPE);
//            client = new File(dir + "/cryptoCLIENT" + FILETYPE);
//            component = new File(dir + "/cryptoCOMPONENT" + FILETYPE);
//            parameterp = new File(dir + "/cryptoPARAMETERP" + FILETYPE);
//            product = new File(dir + "/cryptoPRODUCT" + FILETYPE);
//            project = new File(dir + "/cryptoPROJECT" + FILETYPE);
//            unit = new File(dir + "/cryptoUNIT" + FILETYPE);
//            worth = new File(dir + "/cryptoWORTH" + FILETYPE);
//
            assembly.renameTo(new File(result.getAbsolutePath() + "/ASSEMBLY" + FILETYPE));
            category.renameTo(new File(result.getAbsolutePath() + "/CATEGORY" + FILETYPE));
            client.renameTo(new File(result.getAbsolutePath() + "/CLIENT" + FILETYPE));
            component.renameTo(new File(result.getAbsolutePath() + "/COMPONENT" + FILETYPE));
            parameterp.renameTo(new File(result.getAbsolutePath() + "/PARAMETERP" + FILETYPE));
            product.renameTo(new File(result.getAbsolutePath() + "/PRODUCT" + FILETYPE));
            project.renameTo(new File(result.getAbsolutePath() + "/PROJECT" + FILETYPE));
            unit.renameTo(new File(result.getAbsolutePath() + "/UNIT" + FILETYPE));
            worth.renameTo(new File(result.getAbsolutePath() + "/WORTH" + FILETYPE));
        }
    }

    public int imp(String path) {
        try {
            String dir = System.getProperty("user.dir");

            File assembly = new File(dir + "/ASSEMBLY" + FILETYPE);
            File category = new File(dir + "/CATEGORY" + FILETYPE);
            File client = new File(dir + "/CLIENT" + FILETYPE);
            File component = new File(dir + "/COMPONENT" + FILETYPE);
            File parameterp = new File(dir + "/PARAMETERP" + FILETYPE);
            File product = new File(dir + "/PRODUCT" + FILETYPE);
            File project = new File(dir + "/PROJECT" + FILETYPE);
            File unit = new File(dir + "/UNIT" + FILETYPE);
            File worth = new File(dir + "/WORTH" + FILETYPE);

            assembly.deleteOnExit();
            category.deleteOnExit();
            client.deleteOnExit();
            component.deleteOnExit();
            parameterp.deleteOnExit();
            product.deleteOnExit();
            project.deleteOnExit();
            unit.deleteOnExit();
            worth.deleteOnExit();

            em.getTransaction().begin();

//            decrypt(CRYPTOKEY, new File(path + "/" + assembly.getName()), assembly);
//            decrypt(CRYPTOKEY, new File(path + "/" + category.getName()), category);
//            decrypt(CRYPTOKEY, new File(path + "/" + client.getName()), client);
//            decrypt(CRYPTOKEY, new File(path + "/" + component.getName()), component);
//            decrypt(CRYPTOKEY, new File(path + "/" + parameterp.getName()), parameterp);
//            decrypt(CRYPTOKEY, new File(path + "/" + product.getName()), product);
//            decrypt(CRYPTOKEY, new File(path + "/" + project.getName()), project);
//            decrypt(CRYPTOKEY, new File(path + "/" + unit.getName()), unit);
//            decrypt(CRYPTOKEY, new File(path + "/" + worth.getName()), worth);

            setChanged();
            notifyObservers(9);

            if (new File(path + "/" + assembly.getName()).length() != 0) {
                Query query1 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query1.setParameter(1, null);
                query1.setParameter(2, "ASSEMBLY");
                query1.setParameter(3, path + "/ASSEMBLY" + FILETYPE);
                query1.setParameter(4, "%");
                query1.setParameter(5, null);
                query1.setParameter(6, null);
                query1.executeUpdate();
            }

            setChanged();
            notifyObservers(18);

            if (new File(path + "/" + category.getName()).length() != 0) {
                Query query2 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query2.setParameter(1, null);
                query2.setParameter(2, "CATEGORY");
                query2.setParameter(3, path + "/CATEGORY" + FILETYPE);
                query2.setParameter(4, "%");
                query2.setParameter(5, null);
                query2.setParameter(6, null);
                query2.executeUpdate();
            }

            setChanged();
            notifyObservers(27);

            if (new File(path + "/" + component.getName()).length() != 0) {
                Query query3 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query3.setParameter(1, null);
                query3.setParameter(2, "COMPONENT");
                query3.setParameter(3, path + "/COMPONENT" + FILETYPE);
                query3.setParameter(4, "%");
                query3.setParameter(5, null);
                query3.setParameter(6, null);
                query3.executeUpdate();
            }

            setChanged();
            notifyObservers(36);

            if (new File(path + "/" + parameterp.getName()).length() != 0) {
                Query query4 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query4.setParameter(1, null);
                query4.setParameter(2, "PARAMETERP");
                query4.setParameter(3, path + "/PARAMETERP" + FILETYPE);
                query4.setParameter(4, "%");
                query4.setParameter(5, null);
                query4.setParameter(6, null);
                query4.executeUpdate();
            }

            setChanged();
            notifyObservers(45);

            if (new File(path + "/" + product.getName()).length() != 0) {
                Query query5 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query5.setParameter(1, null);
                query5.setParameter(2, "PRODUCT");
                query5.setParameter(3, path + "/PRODUCT" + FILETYPE);
                query5.setParameter(4, "%");
                query5.setParameter(5, null);
                query5.setParameter(6, null);
                query5.executeUpdate();
            }

            setChanged();
            notifyObservers(54);

            if (new File(path + "/" + project.getName()).length() != 0) {
                Query query6 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query6.setParameter(1, null);
                query6.setParameter(2, "PROJECT");
                query6.setParameter(3, path + "/PROJECT" + FILETYPE);
                query6.setParameter(4, "%");
                query6.setParameter(5, null);
                query6.setParameter(6, null);
                query6.executeUpdate();
            }

            setChanged();
            notifyObservers(63);

            if (new File(path + "/" + unit.getName()).length() != 0) {
                Query query7 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query7.setParameter(1, null);
                query7.setParameter(2, "UNIT");
                query7.setParameter(3, path + "/UNIT" + FILETYPE);
                query7.setParameter(4, "%");
                query7.setParameter(5, null);
                query7.setParameter(6, null);
                query7.executeUpdate();
            }

            setChanged();
            notifyObservers(72);

            if (new File(path + "/" + worth.getName()).length() != 0) {
                Query query8 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query8.setParameter(1, null);
                query8.setParameter(2, "WORTH");
                query8.setParameter(3, path + "/WORTH" + FILETYPE);
                query8.setParameter(4, "%");
                query8.setParameter(5, null);
                query8.setParameter(6, null);
                query8.executeUpdate();
            }

            setChanged();
            notifyObservers(81);

            if (new File(path + "/" + client.getName()).length() != 0) {
                Query query9 = em.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,1)");
                query9.setParameter(1, null);
                query9.setParameter(2, "CLIENT");
                query9.setParameter(3, path + "/CLIENT" + FILETYPE);
                query9.setParameter(4, "%");
                query9.setParameter(5, null);
                query9.setParameter(6, null);
                query9.executeUpdate();
            }

            setChanged();
            notifyObservers(90);

            em.getTransaction().commit();

            setChanged();
            notifyObservers(100);

            return 0;
        } catch (Exception ex) {
            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            refreshEntities();
            if (em != null) {
                em.clear();
            }
        }
    }

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws Exception {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileOutputStream outputStream;
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {
                byte[] inputBytes = new byte[(int) inputFile.length()];
                inputStream.read(inputBytes);
                byte[] outputBytes = cipher.doFinal(inputBytes);
                outputStream = new FileOutputStream(outputFile);
                outputStream.write(outputBytes);
            }
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            throw new Exception("Error encrypting/decrypting file", ex);
        }
    }

    private void refreshEntities() {

        em.createQuery("select a from Assembly a", Assembly.class).getResultList().stream().forEach((assembly) -> {
            em.refresh(assembly);
        });

        em.createQuery("select c from Category c", Category.class).getResultList().stream().forEach((category) -> {
            em.refresh(category);
        });

        em.createQuery("select c from Client c", Client.class).getResultList().stream().forEach((client) -> {
            em.refresh(client);
        });

        em.createQuery("select c from Component c", Component.class).getResultList().stream().forEach((component) -> {
            em.refresh(component);
        });

        em.createQuery("select p from ParameterP p", ParameterP.class).getResultList().stream().forEach((parameter) -> {
            em.refresh(parameter);
        });

        em.createQuery("select p from Product p", Product.class).getResultList().stream().forEach((product) -> {
            em.refresh(product);
        });

        em.createQuery("select p from Project p", Project.class).getResultList().stream().forEach((project) -> {
            em.refresh(project);
        });

        em.createQuery("select u from Unit u", Unit.class).getResultList().stream().forEach((unit) -> {
            em.refresh(unit);
        });

        em.createQuery("select w from Worth w", Worth.class).getResultList().stream().forEach((worth) -> {
            em.refresh(worth);
        });
    }
}
