/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Andreas
 */
public class BackUpDatabase {
    public int exp() {
        String userPassword = "app/app";

        SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss");
        Date date = new Date();
        String path = "./KalkulationsAssistent";
        String directoryName = "/backup";
        new File(path + "/" + directoryName).mkdir();
        String fileName = path + directoryName + "/exp"
                + sdf.format(date) + ".dmp";
        String logFile = path + directoryName + "/log"
                + sdf.format(date) + ".log";

        Process p = null;
        ProcessBuilder pb = new ProcessBuilder("exp", userPassword, "file=" + fileName, "log=" + logFile);
        try {
            p = pb.start();
            p.waitFor();
            System.out.println("exp: " + p.exitValue());
            return p.exitValue();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error exp: " + p.exitValue());
        }
        return -1;
    }

    public void imp() {
        Process p = null;
        ProcessBuilder pb = new ProcessBuilder("imp", "calculation/calc@XE",
                "file=c:/tmp/pr04.dmp", "log=c:/tmp/imp.log", "full=yes", "ignore=yes");
        try {
            p = pb.start();
            Thread.sleep(1000);
            System.out.println("imp" + p.exitValue());
        } catch (Exception e) {
        }
    }
}
