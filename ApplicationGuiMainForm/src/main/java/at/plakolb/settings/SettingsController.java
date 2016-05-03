/*	HTL Leonding	*/
package at.plakolb.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Andreas
 */
public class SettingsController {

    private static Properties prop = new Properties();
    private static final String filename = "src/main/java/at/plakolb/settings/settings.properties";

    public static void setProperty(String property, String newValue) {
        OutputStream output = null;
        try {

            output = new FileOutputStream(filename);

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
            input = new FileInputStream(filename);
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

    public static Date getDateProperty(String property) {
        String[] dateTXT = getProperty("lastBackup").split("-");
        return new Date(Integer.valueOf(dateTXT[2])-1900, Integer.valueOf(dateTXT[1]), Integer.valueOf(dateTXT[0]));
    }

    public static void setDateProperty(String property, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        setProperty(property, sdf.format(date));
    }
}
