/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.entity.Worth;
import java.text.DecimalFormat;

/**
 *
 * @author Elisabeth
 */
public class UtilityFormat {

    public static String worthWithTwoDecimalPlaces(double d) {
        if (d <= 0.001 && d != 0) {
            return ("" + d).replace(".", ",");
        }
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return twoDForm.format(d).replace(",", ".");
    }

    public static String worthWithThreeDecimalPlaces(double d) {
        DecimalFormat twoDForm = new DecimalFormat("0.000");
        return twoDForm.format(d).replace(",", ".");
    }

    public static String worthWithDecimalPattern(String pattern, double d) {
        if (d <= 0.001  && d != 0) {
            return ("" + d).replace(",", ".");
        }
        if (pattern == null) {
            return worthWithTwoDecimalPlaces(d);
        }
        DecimalFormat twoDForm = new DecimalFormat(pattern);
        return twoDForm.format(d).replace(",", ".");
    }

    public static String formatValueWithShortTerm(double worth, String shortTerm) {
        String decimalPlaces = "m³".equals(shortTerm)?"0.000":"0.00";
        return worthWithDecimalPattern(decimalPlaces, worth);
    }

    public static String formatWorth(Worth worth) {
        String decimalPlaces = "m³".equals(worth.getParameter().getUnit().getShortTerm())?"0.000":"0.00";
        return worthWithDecimalPattern(decimalPlaces, worth.getWorth());
    }

    public static String worthWithUnit(Worth worth) {
        String decimalPlaces = "m³".equals(worth.getParameter().getUnit().getShortTerm())?"0.000":"0.00";
        return worthWithDecimalPattern(decimalPlaces, worth.getWorth()) + " " + worth.getParameter().getUnit().getShortTerm();
    }
}
