/*	HTL Leonding	*/
package at.plakolb.settings;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Andreas
 */
public class SettingsController {

    private static Properties prop = new Properties();
    private static final String filename = "src/main/java/at/plakolb/settings/settings.properties";
    private static final String filenameBuild = "settings.properties";

    public static void setProperty(String property, String newValue) {
        OutputStream output = null;
        try {
            if(new File(filename).exists()){
                output = new FileOutputStream(filename);
            }
            else{
                output = new FileOutputStream(filenameBuild);
            }

            // set the properties value
            prop.setProperty(property, newValue);

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String getProperty(String property) {
        InputStream input = null;
        try {
            if(new File(filename).exists()){
                input = new FileInputStream(filename);
            }
            else{
                if(!new File(filenameBuild).exists()){
                    createSettingsFile();
                }
                input = new FileInputStream(filenameBuild);
            }
            // load a properties file
            prop.load(input);
            return prop.getProperty(property);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean getBooleanProperty(String property) {
        return getProperty(property).equals("true");
    }

    public static LocalDate getDateProperty(String property) {
        String[] dateTXT = getProperty(property).split("-");
        return LocalDate.of(Integer.valueOf(dateTXT[2]), Integer.valueOf(dateTXT[1]), Integer.valueOf(dateTXT[0]));
    }

    public static void setDateProperty(String property, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        setProperty(property, sdf.format(date));
    }

    public static void resetProperties(){
        SettingsController.setProperty("remindBackupWeeks", "3");
        SettingsController.setProperty("remindBackup", "false");
        SettingsController.setProperty("lastBackup", "01-01-2016");
        SettingsController.setProperty("backupPath", "");
        SettingsController.setProperty("pdfPath", "");
        SettingsController.setProperty("printCopies", "1");
        SettingsController.setProperty("firstrun", "true");
    }
    public static void createSettingsFile(){
        try {
            new File(filenameBuild).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        resetProperties();
    }
}
