package at.plakolb.calculationlogic.util;

/**
 * Created by Andreas on 06.06.2016.
 */
public class PrintInformationException extends Exception {

    public PrintInformationException(String message) {
        super(message);
    }

    public PrintInformationException(String message,Exception inner) {
        super(message,inner);
    }
}
