package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.db.entity.Worth;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

/**
 *
 * @author Kepplinger
 */
public class UtilityFormat {

    /**
     * Parses a double into a formatted String.
     *
     * @param number
     * @return
     */
    public static String getStringForTextField(Double number) {
        if (number == null) {
            return "";
        } else if (number.isInfinite()) {
            number = 0.0;
        }

        if (number == 0 || number.isNaN()) {
            return "";
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.#######");
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            return String.valueOf(decimalFormat.format(number));
        }
    }

    /**
     * Parses a worth into a formatted String.
     *
     * @param worth
     * @return
     */
    public static String getStringForTextField(Worth worth) {
        if (worth == null) {
            return "0";
        }
        return getStringForTextField(worth.getWorth());
    }

    /**
     * Parses a double into a formatted String.
     *
     * @param number
     * @return
     */
    public static String getStringForLabel(Double number) {
        if (number == null) {
            return "0";
        } else {
            if (number.isNaN()) {
                return "0";
            }
            if (number.isInfinite()) {
                number = 0.0;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            return String.valueOf(decimalFormat.format(number));
        }
    }

    /**
     * Parses a worth into a formatted String.
     *
     * @param worth
     * @return
     */
    public static String getStringForLabel(Worth worth) {
        if (worth == null) {
            return "0";
        }
        return getStringForLabel(worth.getWorth()) + " " + worth.getParameter().getUnit().getShortTerm();
    }

    /**
     * Parses a double into a formatted String, which has 2 decimals and fits
     * into a table column.
     *
     * @param number
     * @return
     */
    public static String getStringForTableColumn(Double number) {
        if (number == null) {
            return "";
        } else {
            if (number.isNaN()) {
                return "";
            }
            if (number.isInfinite()) {
                number = 0.0;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            return String.valueOf(decimalFormat.format(number));
        }
    }

    /**
     * Formats the path that it can fit into the textfield.
     *
     * @param textField
     * @param original
     */
    public static void setCutTextForTextField(TextField textField, String original) {
        if (!original.isEmpty()) {
            try {
                original = original.replace("\\", "/");
                textField.setTooltip(new Tooltip(original));
                Text text = new Text(original);
                text.setFont(textField.getFont());
                String[] splittedPath = original.split("/");

                double width = textField.getWidth() == 0 ? textField.getPrefWidth() : textField.getWidth();
                if (text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d > width) {
                    text.setText(splittedPath.length > 3 ? String.format("%s/%s/.../%s",
                            splittedPath[0],
                            splittedPath[1],
                            splittedPath[splittedPath.length - 1]) : "../" + splittedPath[splittedPath.length - 1]);
                    if (text.getLayoutBounds().getWidth() + textField.getPadding().getLeft() + textField.getPadding().getRight() + 2d > width) {
                        text.setText("../" + splittedPath[splittedPath.length - 1]);
                    }
                }
                textField.setText(text.getText());
            } catch (Exception e) {
            }
        }
    }

    /**
     * Saves the number of a textfield into a worth object.
     *
     * @param textField
     * @param worth
     * @return
     */
    public static boolean setWorthFromTextField(TextField textField, Worth worth) {

        try {
            if (textField != null) {
                textField.setText(textField.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
                textField.setText(removeUnnecessaryCommas(textField.getText()));

                if (textField.getText().isEmpty()) {
                    worth.setWorth(0);
                } else {
                    worth.setWorth(Double.parseDouble(textField.getText()));
                }
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(UtilityFormat.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }

    /**
     * Saves the price of a textfield into a double.
     *
     * @param textField
     * @param doubleValue
     * @return
     */
    public static double getPriceFromTextField(TextField textField, double doubleValue) {

        try {
            if (textField != null) {
                textField.setText(textField.getText().replaceAll(",", ".").replaceAll("[^\\d.]", ""));
                textField.setText(removeUnnecessaryCommas(textField.getText()));

                if (textField.getText().isEmpty()) {
                    doubleValue = 0;
                } else {
                    doubleValue = Double.parseDouble(textField.getText());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UtilityFormat.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return doubleValue;
    }

    /**
     * Reduces the amount of commas to one.
     *
     * @param numberString
     * @return
     */
    public static String removeUnnecessaryCommas(String numberString) {

        String newString = numberString;

        if ((numberString.length() - numberString.replace(".", "").length()) > 1) {
            int index = numberString.lastIndexOf(".");

            for (int i = 0; i < numberString.length(); i++) {
                if (numberString.charAt(i) == '.' && i != index) {
                    newString = newString.replaceFirst("\\.", "");
                }
            }
        }
        return newString;
    }

    /**
     * Returns a String from a LocalDateTime object containing the current date
     * and the current time.
     *
     * @param dateTime
     * @return
     */
    public static String getDate(LocalDateTime dateTime) {
        return dateTime.getDayOfMonth() + ". "
                + dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " "
                + dateTime.getYear();
    }

    /**
     * Returns a String from a LocalDateTime object containing the current date
     * and the current time.
     *
     * @param dateTime
     * @return
     */
    public static String getDateTime(LocalDateTime dateTime) {
        return dateTime.getDayOfMonth() + "_"
                + String.format("%02d", dateTime.getMonthValue()) + "_"
                + dateTime.getYear() + "_"
                + dateTime.getHour() + "-"
                + dateTime.getMinute() + "-"
                + dateTime.getSecond();
    }

    public static String worthWithTwoDecimalPlaces(double d) {
        if (d <= 0.001 && d != 0) {
            return ("" + d).replace(".", ",");
        }
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return twoDForm.format(d).replaceAll(",", ".");
    }

    public static String worthWithThreeDecimalPlaces(double d) {
        DecimalFormat twoDForm = new DecimalFormat("0.000");
        return twoDForm.format(d).replace(",", ".");
    }

    public static String worthWithDecimalPattern(String pattern, double d) {
        if (d <= 0.001 && d != 0) {
            return ("" + d).replace(",", ".");
        }
        if (pattern == null) {
            return worthWithTwoDecimalPlaces(d);
        }
        DecimalFormat twoDForm = new DecimalFormat(pattern);
        return twoDForm.format(d).replace(",", ".");
    }

    public static String formatValueWithShortTerm(double worth, String shortTerm) {
        String decimalPlaces = "m³".equals(shortTerm) ? "0.000" : "0.00";
        return worthWithDecimalPattern(decimalPlaces, worth);
    }

    public static String formatWorth(Worth worth) {
        String decimalPlaces = "m³".equals(worth.getParameter().getUnit().getShortTerm()) ? "0.000" : "0.00";
        return worthWithDecimalPattern(decimalPlaces, worth.getWorth());
    }

    public static String worthWithUnit(Worth worth) {
        String decimalPlaces = "m³".equals(worth.getParameter().getUnit().getShortTerm()) ? "0.000" : "0.00";
        return worthWithDecimalPattern(decimalPlaces, worth.getWorth()) + " " + worth.getParameter().getUnit().getShortTerm();
    }

    public static String twoDecimalPlaces(double decimal) {
        DecimalFormat twoDForm = new DecimalFormat("0.00");
        return twoDForm.format(decimal);
    }
}
