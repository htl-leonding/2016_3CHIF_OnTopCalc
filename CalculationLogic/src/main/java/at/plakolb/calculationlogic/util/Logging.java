/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Kepplinger
 */
public class Logging {

    private static final int MAX_LOGFILES = 3;
    private static final int MAX_LOGFILE_SIZE = 65536; //64KB
    private static final String LOGFILE_NAME = "Logfile";

    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null) {
            try {
                logger = Logger.getLogger("");
                String logFilePath = getLogFilePath();

                System.out.println(logFilePath);
                FileHandler fh = new FileHandler(logFilePath, true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            } catch (IOException | SecurityException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        return logger;
    }

    private static String getLogFilePath() {
        for (int i = 0; i < MAX_LOGFILES; i++) {
            File f = new File(String.format("%s%d.log", LOGFILE_NAME, i));

            if (f.exists()) {
                if (f.length() >= MAX_LOGFILE_SIZE)
                    continue;
                else
                    return String.format("%s%d.log", LOGFILE_NAME, i);
            } else {
                return String.format("%s%d.log", LOGFILE_NAME, i);
            }
        }
        new File(String.format("%s0.log",LOGFILE_NAME)).delete();
        return String.format("%s0.log",LOGFILE_NAME);
    }

}