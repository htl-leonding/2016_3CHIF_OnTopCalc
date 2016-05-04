/*	HTL Leonding	*/
package at.plakolb;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Kepplinger
 */
public class Logging {
    
    private static Logger logger;
    
    public static Logger getLogger(){
        if (logger == null) {
            try {
                logger = Logger.getLogger("OnTopCalc");
                FileHandler fh = new FileHandler("Logfile.log",true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            } catch (IOException | SecurityException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        return logger;
    }
    
}
