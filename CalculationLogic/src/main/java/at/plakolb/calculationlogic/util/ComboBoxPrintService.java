/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import javax.print.PrintService;

/**
 *
 * Contains a printService and a displayName. Is used to display a better name
 * in ComboBoxes (no "Win32 Printer : " anymore).
 *
 * @author Kepplinger
 */
public class ComboBoxPrintService {

    private String displayName;
    private final PrintService printService;

    public ComboBoxPrintService(PrintService printService) {
        this.printService = printService;
        setDisplayName(printService.getName());
    }

    public final void setDisplayName(String printServiceName) {
        if (printServiceName.contains("Win32 Printer : ")) {
            displayName = printServiceName.replace("Win32 Printer : ", "");
        } else {
            displayName = printServiceName;
        }
    }

    public PrintService getPrintService() {
        return printService;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
