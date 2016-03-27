/*	HTL Leonding	*/
package at.plakolb.calculationlogic.math.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Kepplinger
 */
public class MathUtils {

    /**
     * Rounds a double.
     *
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {

        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
