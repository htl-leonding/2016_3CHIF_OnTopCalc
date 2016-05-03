/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andreas
 */
public class BackUpDatabase extends Observable {

    String filename;
    String endpath;
    private static final String FILETYPE = ".OTCdb";

    public BackUpDatabase(String path) {
        SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss");
        filename = "backup" + sdf.format(new Date());
        endpath = path;
    }

    public int exp() {
        try {
            String user = "app";
            String password = "app";
            String jdbcURL = "jdbc:derby://localhost:1527/db";

            String dir = System.getProperty("user.dir");

            Connection connection = DriverManager.getConnection(jdbcURL, user, password);

            setChanged();
            notifyObservers(5);

            PreparedStatement ps1 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps1.setString(1, null);
            ps1.setString(2, "ASSEMBLY");
            ps1.setString(3, dir + "/ASSEMBLY"+FILETYPE);
            ps1.setString(4, "%");
            ps1.setString(5, null);
            ps1.setString(6, null);
            ps1.execute();
            setChanged();
            notifyObservers(15);

            PreparedStatement ps2 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps2.setString(1, null);
            ps2.setString(2, "CATEGORY");
            ps2.setString(3, dir + "/CATEGORY"+FILETYPE);
            ps2.setString(4, "%");
            ps2.setString(5, null);
            ps2.setString(6, null);
            ps2.execute();
            setChanged();
            notifyObservers(25);
            PreparedStatement ps3 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps3.setString(1, null);
            ps3.setString(2, "COMPONENT");
            ps3.setString(3, dir + "/COMPONENT"+FILETYPE);
            ps3.setString(4, "%");
            ps3.setString(5, null);
            ps3.setString(6, null);
            ps3.execute();
            setChanged();
            notifyObservers(35);
            PreparedStatement ps4 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps4.setString(1, null);
            ps4.setString(2, "PARAMETERP");
            ps4.setString(3, dir + "/PARAMETERP"+FILETYPE);
            ps4.setString(4, "%");
            ps4.setString(5, null);
            ps4.setString(6, null);
            ps4.execute();
            setChanged();
            notifyObservers(45);
            PreparedStatement ps5 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps5.setString(1, null);
            ps5.setString(2, "PRODUCT");
            ps5.setString(3, dir + "/PRODUCT"+FILETYPE);
            ps5.setString(4, "%");
            ps5.setString(5, null);
            ps5.setString(6, null);
            ps5.execute();
            setChanged();
            notifyObservers(55);
            PreparedStatement ps6 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps6.setString(1, null);
            ps6.setString(2, "PROJECT");
            ps6.setString(3, dir + "/PROJECT"+FILETYPE);
            ps6.setString(4, "%");
            ps6.setString(5, null);
            ps6.setString(6, null);
            ps6.execute();
            setChanged();
            notifyObservers(65);
            PreparedStatement ps7 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps7.setString(1, null);
            ps7.setString(2, "UNIT");
            ps7.setString(3, dir + "/UNIT"+FILETYPE);
            ps7.setString(4, "%");
            ps7.setString(5, null);
            ps7.setString(6, null);
            ps7.execute();
            setChanged();
            notifyObservers(75);
            PreparedStatement ps8 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps8.setString(1, null);
            ps8.setString(2, "WORTH");
            ps8.setString(3, dir + "/WORTH"+FILETYPE);
            ps8.setString(4, "%");
            ps8.setString(5, null);
            ps8.setString(6, null);
            ps8.execute();
            setChanged();
            notifyObservers(85);
            PreparedStatement ps9 = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            ps9.setString(1, null);
            ps9.setString(2, "CLIENT");
            ps9.setString(3, dir + "/CLIENT"+FILETYPE);
            ps9.setString(4, "%");
            ps9.setString(5, null);
            ps9.setString(6, null);
            ps9.execute();
            setChanged();
            notifyObservers(95);

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
        }
    }

    private void move() throws IOException {
        String line = "";
        String dir = System.getProperty("user.dir");

        File result = new File(endpath + "/" + filename);
        if (!result.exists()) {
            result.mkdir();

            File assembly = new File(dir + "/ASSEMBLY"+FILETYPE);
            File category = new File(dir + "/CATEGORY"+FILETYPE);
            File client = new File(dir + "/CLIENT"+FILETYPE);
            File component = new File(dir + "/COMPONENT"+FILETYPE);
            File parameterp = new File(dir + "/PARAMETERP"+FILETYPE);
            File product = new File(dir + "/PRODUCT"+FILETYPE);
            File project = new File(dir + "/PROJECT"+FILETYPE);
            File unit = new File(dir + "/UNIT"+FILETYPE);
            File worth = new File(dir + "/WORTH"+FILETYPE);

            assembly.renameTo(new File(result.getAbsolutePath()+"/ASSEMBLY"+FILETYPE));
            category.renameTo(new File(result.getAbsolutePath()+"/CATEGORY"+FILETYPE));
            client.renameTo(new File(result.getAbsolutePath()+"/CLIENT"+FILETYPE));
            component.renameTo(new File(result.getAbsolutePath()+"/COMPONENT"+FILETYPE));
            parameterp.renameTo(new File(result.getAbsolutePath()+"/PARAMETERP"+FILETYPE));
            product.renameTo(new File(result.getAbsolutePath()+"/PRODUCT"+FILETYPE));
            project.renameTo(new File(result.getAbsolutePath()+"/PROJECT"+FILETYPE));
            unit.renameTo(new File(result.getAbsolutePath()+"/UNIT"+FILETYPE));
            worth.renameTo(new File(result.getAbsolutePath()+"/WORTH"+FILETYPE));
        }
    }

    public void imp() throws SQLException {
//        //CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE(null,'STAFF','myfile.del',null,null,null,0);
//        String user = "app";
//            String password = "app";
//            String jdbcURL = "jdbc:derby://localhost:1527/db";
//
//            String dir = System.getProperty("user.dir");
//
//            Connection connection = DriverManager.getConnection(jdbcURL, user, password);
//
//            setChanged();
//            notifyObservers(5);
//
//            PreparedStatement ps1 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?,0)");
//            ps1.setString(1, null);
//            ps1.setString(2, "ASSEMBLY");
//            ps1.setString(3, dir + "/ASSEMBLY"+FILETYPE);
//            ps1.setString(4, "%");
//            ps1.setString(5, null);
//            ps1.setString(6, null);
//            ps1.execute();
//            setChanged();
//            notifyObservers(15);
//
//            PreparedStatement ps2 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps2.setString(1, null);
//            ps2.setString(2, "CATEGORY");
//            ps2.setString(3, dir + "/CATEGORY"+FILETYPE);
//            ps2.setString(4, "%");
//            ps2.setString(5, null);
//            ps2.setString(6, null);
//            ps2.execute();
//            setChanged();
//            notifyObservers(25);
//            PreparedStatement ps3 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps3.setString(1, null);
//            ps3.setString(2, "COMPONENT");
//            ps3.setString(3, dir + "/COMPONENT"+FILETYPE);
//            ps3.setString(4, "%");
//            ps3.setString(5, null);
//            ps3.setString(6, null);
//            ps3.execute();
//            setChanged();
//            notifyObservers(35);
//            PreparedStatement ps4 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps4.setString(1, null);
//            ps4.setString(2, "PARAMETERP");
//            ps4.setString(3, dir + "/PARAMETERP"+FILETYPE);
//            ps4.setString(4, "%");
//            ps4.setString(5, null);
//            ps4.setString(6, null);
//            ps4.execute();
//            setChanged();
//            notifyObservers(45);
//            PreparedStatement ps5 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps5.setString(1, null);
//            ps5.setString(2, "PRODUCT");
//            ps5.setString(3, dir + "/PRODUCT"+FILETYPE);
//            ps5.setString(4, "%");
//            ps5.setString(5, null);
//            ps5.setString(6, null);
//            ps5.execute();
//            setChanged();
//            notifyObservers(55);
//            PreparedStatement ps6 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps6.setString(1, null);
//            ps6.setString(2, "PROJECT");
//            ps6.setString(3, dir + "/PROJECT"+FILETYPE);
//            ps6.setString(4, "%");
//            ps6.setString(5, null);
//            ps6.setString(6, null);
//            ps6.execute();
//            setChanged();
//            notifyObservers(65);
//            PreparedStatement ps7 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps7.setString(1, null);
//            ps7.setString(2, "UNIT");
//            ps7.setString(3, dir + "/UNIT"+FILETYPE);
//            ps7.setString(4, "%");
//            ps7.setString(5, null);
//            ps7.setString(6, null);
//            ps7.execute();
//            setChanged();
//            notifyObservers(75);
//            PreparedStatement ps8 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps8.setString(1, null);
//            ps8.setString(2, "WORTH");
//            ps8.setString(3, dir + "/WORTH"+FILETYPE);
//            ps8.setString(4, "%");
//            ps8.setString(5, null);
//            ps8.setString(6, null);
//            ps8.execute();
//            setChanged();
//            notifyObservers(85);
//            PreparedStatement ps9 = connection.prepareStatement(
//                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
//            ps9.setString(1, null);
//            ps9.setString(2, "CLIENT");
//            ps9.setString(3, dir + "/CLIENT"+FILETYPE);
//            ps9.setString(4, "%");
//            ps9.setString(5, null);
//            ps9.setString(6, null);
//            ps9.execute();
//            setChanged();
//            notifyObservers(95);
//
//            move();
//
//            setChanged();
//            notifyObservers(100);
//            return 3;
//        } catch (SQLException ex) {
//            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
//            return -1;
//        } catch (IOException ex) {
//            Logger.getLogger(BackUpDatabase.class.getName()).log(Level.SEVERE, null, ex);
//            return -2;
//        }
    }
}
