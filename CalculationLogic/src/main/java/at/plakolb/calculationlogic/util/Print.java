/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.entity.Assembly;
import at.plakolb.calculationlogic.entity.Category;
import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Component;
import at.plakolb.calculationlogic.entity.Project;
import at.plakolb.calculationlogic.entity.Worth;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.PageRanges;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

/**
 *
 * @author Andreas
 */
public class Print {

    private static Font headFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);
    private static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLDITALIC);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private String path;
    private String absolutePath;

    private Project project;
    private String city;
    private Document document;

    private List<Boolean> listPrint = new ArrayList<>();

    public Print(String path, Project project, String city) {
        this.path = path;
        this.project = project;
        this.city = city;
    }

    public String createPDF() throws DocumentException, FileNotFoundException {
        SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss");
        String fileName = project.getProjectName().replace(" ", "_") + sdf.format(new Date()) + ".pdf";
        File file = new File(path + "/" + fileName);
        absolutePath = file.getAbsolutePath();
        System.out.println("Erstelle: " + file.toPath());
        document = new Document();
        document.setPageSize(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        if (project.getClient() != null) {
            addClient(document, project.getClient());
        }
        addCity(document, city);
        addTitle(document, project);
        //Metadaten
        document.addTitle(project.getProjectName());
        document.addAuthor(System.getProperty("user.name"));
        document.addCreator(System.getProperty("user.name"));

        int index = 0;
        if (listPrint.get(index)) {
            addConstruction(document, project);
        }
        if (listPrint.get(++index)) {
            addPreCut(document, project);
        }
        if (listPrint.get(++index)) {
            addCubicMeter(document, project);
        }
        if (listPrint.get(++index)) {
            addFormwork(document, project);
        }
        if (listPrint.get(++index)) {
            addVisableFormwork(document, project);
        }
        if (listPrint.get(++index)) {
            addFoil(document, project);
        }
        if (listPrint.get(++index)) {
            addSealingBand(document, project);
        }
        if (listPrint.get(++index)) {
            addCounterBatten(document, project);
        }
        if (listPrint.get(++index)) {
            addBattensOrFullFormwork(document, project);
        }
        if (listPrint.get(++index)) {
            addMaterialAssembly(document, project);
        }
        if (listPrint.get(++index)) {
            addColor(document, project);
        }
        if (listPrint.get(++index)) {
            addCarriage(document, project);
        }
        if (listPrint.get(++index)) {
            addMaterialCostList(document, project);
        }
        if (listPrint.get(++index)) {
            addSummary(document, project);
        }
        document.close();
        return absolutePath;
    }

    public void print(PrintService service) throws PrinterException, IOException {
        PDDocument doc = PDDocument.load(new File(absolutePath));

        PrinterJob job = PrinterJob.getPrinterJob();
        double cm = 72.0 / 2.54;
        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(new PageRanges(1, doc.getNumberOfPages()));
        attr.add(new JobName("OnTopCalc - " + project.getProjectNameWithId(), Locale.ROOT));
        attr.add(new MediaPrintableArea(0,0,210, 295, MediaSize.MM));//?

        Paper paper = new Paper();
        paper.setSize(21.0 * cm, 29.5 * cm);
        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight()); // no margins

        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);

        Book book = new Book();
        book.append(new PDFPrintable(doc), pageFormat, doc.getNumberOfPages());
        job.setPageable(book);

        if (job.printDialog(attr)) {
            job.print(attr);
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private static void addClient(Document document, Client client) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(client.getName(), normalFont));
        paragraph.add(new Paragraph(client.getStreet(), normalFont));
        paragraph.add(new Paragraph(client.getZipCode() + " " + client.getCity(), normalFont));
        paragraph.setAlignment(Element.ALIGN_LEFT);
        addEmptyLine(paragraph, 2);
        document.add(paragraph);
    }

    private static void addCity(Document document, String city) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(city, normalFont));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        addEmptyLine(paragraph, 2);
        document.add(paragraph);
    }

    private static void addTitle(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(project.getProjectName(), headFont));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
    }

    private void addConstruction(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Allgemeine Informationen", subFont));

        if (project.getCreationDate() != null) {
            paragraph.add(new Paragraph("Erstellt am: "
                    + sdf.format(project.getCreationDate()), normalFont));
        }
        if (project.getConstructionType() != null) {
            paragraph.add(new Paragraph("Konstruktionstyp: "
                    + project.getConstructionType(), normalFont));
        }
        if (project.getRoofForm() != null) {
            paragraph.add(new Paragraph("Dachform: "
                    + project.getRoofForm(), normalFont));
        }
        if (project.getDescription() != null) {
            paragraph.add(new Paragraph("Notiz:\n"
                    + project.getDescription(), normalFont));
        }
        document.add(paragraph);
    }

    private void addPreCut(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Grund- und Dachflächenermittlung", subFont));

        WorthController worthJpaController = new WorthController();

        Worth area = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("A", project.getId());
        if (area != null) {
            paragraph.add(new Paragraph("Grundfläche: "
                    + area.worthFormatWithUnit(), normalFont));
        }

        Worth roofAreaWithoutRoofOverhang = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("D", project.getId());
        if (roofAreaWithoutRoofOverhang != null) {
            paragraph.add(new Paragraph("Dachfläche "
                    + roofAreaWithoutRoofOverhang.worthFormatWithUnit(), normalFont));
        }

        Worth roofOverhang = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("DV", project.getId());
        if (roofOverhang != null) {
            paragraph.add(new Paragraph("Dachvorsprung "
                    + roofOverhang.worthFormatWithUnit(), normalFont));
        }

        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("DF", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche mit Dachvorsprung: "
                    + roofArea.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addCubicMeter(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Holzmaterial für Konsruktion", subFont));

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f);

        PdfPTable table = new PdfPTable(new float[]{5.5f, 5f, 3f, 3f, 3f, 3f, 3f, 3f, 3f});

        table.setWidthPercentage(100f);

        ComponentController componentJpaController = new ComponentController();
        java.util.List<Component> components = componentJpaController.findComponentsByProjectIdAndComponentType(project.getId(), "Kubikmeter");

        if (components.size() > 0) {

            table.addCell(new Phrase("Kategorie",
                    tableHeaderFont));
            table.addCell(new Phrase("Holzprodukt",
                    tableHeaderFont));
            table.addCell(new Phrase("columnComponentNumberOfProducts",
                    tableHeaderFont));
            table.addCell(new Phrase("Anzahl",
                    tableHeaderFont));
            table.addCell(new Phrase("Preis m³/€",
                    tableHeaderFont));
            table.addCell(new Phrase("Preis €",
                    tableHeaderFont));
            table.addCell(new Phrase("Zuschnitt h",
                    tableHeaderFont));
            table.addCell(new Phrase("Zuschnitt €/h",
                    tableHeaderFont));
            table.addCell(new Phrase("Zuschnitt €",
                    tableHeaderFont));

            for (Component component : components) {
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(component.getCategory().getLongAndShortTerm(), tableNormalFont));
                table.addCell(new Phrase(component.getProduct().getName(), tableNormalFont));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(new Phrase("" + component.getNumberOfProducts(), tableNormalFont));
                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getWidthComponent() / 100 * component.getHeightComponent() / 100
                        * component.getLengthComponent() * component.getNumberOfProducts(), "m³"), normalFont));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(
                        component.getWidthComponent() / 100 * component.getHeightComponent() / 100
                        * component.getLengthComponent() * component.getNumberOfProducts() * component.getPriceComponent(), "€"), normalFont));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours(), "h"), normalFont));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringPricePerHour(), "€"), normalFont));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours() * component.getTailoringPricePerHour(), "€"), normalFont));
            }
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();

        Worth cubicMeter = worthJpaController.findWorthByShortTermAndProjectId("V", project.getId());
        Worth price = worthJpaController.findWorthByShortTermAndProjectId("GP", project.getId());
        Worth duration = worthJpaController.findWorthByShortTermAndProjectId("KG", project.getId());
        Worth tailoringPrice = worthJpaController.findWorthByShortTermAndProjectId("KZPG", project.getId());

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (cubicMeter != null && price != null && duration != null && tailoringPrice != null) {
            table.addCell(new Phrase("", tableHeaderFont));
            table.addCell(new Phrase("", tableHeaderFont));
            table.addCell(new Phrase("", tableHeaderFont));
            table.addCell(new Phrase(UtilityFormat.formatWorth(cubicMeter), tableHeaderFont));
            table.addCell(new Phrase("", tableHeaderFont));
            table.addCell(new Phrase(UtilityFormat.formatWorth(price), tableHeaderFont));
            table.addCell(new Phrase(UtilityFormat.formatWorth(duration), tableHeaderFont));
            table.addCell(new Phrase("", tableHeaderFont));
            table.addCell(new Phrase(UtilityFormat.formatWorth(tailoringPrice), tableHeaderFont));
        }

        Worth priceTotal = worthJpaController.findWorthByShortTermAndProjectId("GKV", project.getId());
        if (priceTotal != null) {
            paragraph.add(new Paragraph("Gesamtpreis: "
                    + priceTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addFormwork(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();
        Category category = categoryJpaController.findCategoryByShortTerm("S");

        Component component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                "Produkt", category.getId());

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph("Schalung", subFont));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("D", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche: "
                    + roofArea.worthFormatWithUnit(), normalFont));
        }

        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), normalFont));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFormwork.worthFormatWithUnit(), normalFont));
        }

        Worth formwork = worthJpaController.findWorthByShortTermAndProjectId("S", project.getId());
        if (formwork != null) {
            paragraph.add(new Paragraph("Schalung: "
                    + formwork.worthFormatWithUnit(), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }
        document.add(paragraph);
    }

    private void addVisableFormwork(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();
        Category category = categoryJpaController.findCategoryByShortTerm("SS");
        Component component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                "Produkt", category.getId());

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph("Sichtbare Schalung", subFont));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("DV", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachvorsprung: "
                    + roofArea.worthFormatWithUnit(), normalFont));
        }

        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), normalFont));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VSS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFormwork.worthFormatWithUnit(), normalFont));
        }

        Worth visableFormwork = worthJpaController.findWorthByShortTermAndProjectId("SS", project.getId());
        if (visableFormwork != null) {
            paragraph.add(new Paragraph("Sichtbare Schalung: "
                    + visableFormwork.worthFormatWithUnit(), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPSS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPSS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SSPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMSS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKSS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }
        document.add(paragraph);
    }

    private void addFoil(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();
        Category category = categoryJpaController.findCategoryByShortTerm("F");
        Component component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                "Produkt", category.getId());

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph("Folie", subFont));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("DF", project.getId());

        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche mit Dachvorsprung: "
                    + roofArea.worthFormatWithUnit(), normalFont));
        }

        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("FUEP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), normalFont));
        }

        Worth abatementFoil = worthJpaController.findWorthByShortTermAndProjectId("FUE", project.getId());
        if (abatementFoil != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFoil.worthFormatWithUnit(), normalFont));
        }

        Worth foil = worthJpaController.findWorthByShortTermAndProjectId("F", project.getId());
        if (foil != null) {
            paragraph.add(new Paragraph("Folie: "
                    + foil.worthFormatWithUnit(), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPF", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPF", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProF", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMF", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKF", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addSealingBand(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();
        Category category = categoryJpaController.findCategoryByShortTerm("ND");
        Component component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                "Produkt", category.getId());

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph(
                    "Nageldichtband", subFont));
        }
        PdfPTable table = new PdfPTable(new float[]{6f, 3.5f});
        table.setWidthPercentage(100f);

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f);

        List<Object[]> lengthRafterSingle = new ArrayList<>();
        for (Component comp : componentJpaController.findAll()) {
            if (comp.getCategory().getShortTerm().equals("KD")) {
                lengthRafterSingle.add(new Object[]{comp.getDescription(), comp.getLengthComponent() * comp.getNumberOfProducts()});
            }
        }

        if (lengthRafterSingle.size() > 0) {

            table.addCell(new Phrase("Name",
                    tableHeaderFont));
            table.addCell(new Phrase("Länge der Dachsparren",
                    tableHeaderFont));

            for (Object[] object : lengthRafterSingle) {
                table.addCell(new Phrase(object[0].toString(), tableNormalFont));
                table.addCell(new Phrase(object[1].toString(), tableNormalFont));
            }
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();
        Worth total = worthJpaController.findWorthByShortTermAndProjectId("LD", project.getId());
        if (total != null) {
            paragraph.add(new Paragraph("Summe: "
                    + total.worthFormatWithUnit(), normalFont));
        }

        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VDP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), normalFont));
        }

        Worth abatementSealingTape = worthJpaController.findWorthByShortTermAndProjectId("DP", project.getId());
        if (abatementSealingTape != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementSealingTape.worthFormatWithUnit(), normalFont));
        }

        Worth sealingTape = worthJpaController.findWorthByShortTermAndProjectId("ND", project.getId());
        if (sealingTape != null) {
            paragraph.add(new Paragraph("Nageldichtband: "
                    + sealingTape.worthFormatWithUnit(), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPD", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPD", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProD", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonD", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKND", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Nageldichtband: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addCounterBatten(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();
        Category category = categoryJpaController.findCategoryByShortTerm("KL");
        Component component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                "Produkt", category.getId());

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph("Konterlattung", subFont));
        }

        PdfPTable table = new PdfPTable(new float[]{6f, 3.5f});
        table.setWidthPercentage(100f);

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f);

        List<Object[]> lengthRafterSingle = new ArrayList<>();
        for (Component comp : componentJpaController.findAll()) {
            if (comp.getCategory().getShortTerm().equals("KD")) {
                lengthRafterSingle.add(new Object[]{comp.getDescription(), comp.getLengthComponent() * comp.getNumberOfProducts()});
            }
        }

        if (lengthRafterSingle.size() > 0) {

            table.addCell(new Phrase("Name", tableHeaderFont));
            table.addCell(new Phrase("Länge der Dachsparren", tableHeaderFont));

            for (Object[] object : lengthRafterSingle) {
                table.addCell(new Phrase(object[0].toString(), tableNormalFont));
                table.addCell(new Phrase(object[1].toString(), tableNormalFont));
            }
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();
        Worth total = worthJpaController.findWorthByShortTermAndProjectId("LD", project.getId());

        if (total != null) {
            paragraph.add(new Paragraph("Summe: "
                    + total.worthFormatWithUnit(), normalFont));
        }

        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VKLP", project.getId());

        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), normalFont));
        }

        Worth abatementCounterBatten = worthJpaController.findWorthByShortTermAndProjectId("VKL", project.getId());
        if (abatementCounterBatten != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementCounterBatten.worthFormatWithUnit(), normalFont));
        }

        Worth counterBatten = worthJpaController.findWorthByShortTermAndProjectId("KL", project.getId());
        if (counterBatten != null) {
            paragraph.add(new Paragraph("Konterlattung: "
                    + counterBatten.worthFormatWithUnit(), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPKL", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPKL", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProKL", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonKL", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKKL", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addBattensOrFullFormwork(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);

        ComponentController componentJpaController = new ComponentController();
        CategoryController categoryJpaController = new CategoryController();

        List<String> roofType = new ArrayList<>();
        roofType.add("Ziegeldach");
        roofType.add("Blechdach");

        int index = roofType.indexOf(project.getRoofMaterial());
        String categoryString = index == 0 ? "L" : "VS";
        Category category = categoryJpaController.findCategoryByShortTerm(categoryString);

        Component component = null;
        if (category != null) {
            component = componentJpaController.findComponentByProjectIdAndComponentTypeAndCategoryId(project.getId(),
                    "Produkt", category.getId());
        }

        if (component != null) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), subFont));
        } else {
            paragraph.add(new Paragraph("Lattung oder Vollschalung", subFont));
        }

        if (project.getRoofMaterial() != null) {
            paragraph.add(new Paragraph("Art: "
                    + project.getRoofMaterial(), normalFont));
        }

        WorthController worthJpaController = new WorthController();
        if (index == 0) {
            Worth worthSlatSpacing = worthJpaController.findWorthByShortTermAndProjectId("LA", project.getId());
            if (worthSlatSpacing != null) {
                paragraph.add(new Paragraph("Lattenabstand in cm: "
                        + worthSlatSpacing.worthFormatWithUnit(), normalFont));
            }

            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                        + worthAbatementPercent.worthFormatWithUnit(), normalFont));
            }

            Worth worthLengthOfTheBattens = worthJpaController.findWorthByShortTermAndProjectId("LDOV", project.getId());
            if (worthLengthOfTheBattens != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten ohne Verschnitt: "
                        + worthLengthOfTheBattens.worthFormatWithUnit(), normalFont));
            }

            Worth worthAbatementBatten = worthJpaController.findWorthByShortTermAndProjectId("VL", project.getId());
            if (worthAbatementBatten != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + worthAbatementBatten.worthFormatWithUnit(), normalFont));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("LL", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten: "
                        + worthFullFormwork.worthFormatWithUnit(), normalFont));
            }
        } else {
            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                        + worthAbatementPercent.worthFormatWithUnit(), normalFont));
            }

            Worth worthRoofArea = worthJpaController.findWorthByShortTermAndProjectIdAndWorthShortTermIsNull("DF", project.getId());
            if (worthRoofArea != null) {
                paragraph.add(new Paragraph("Dachfläche: "
                        + worthRoofArea.worthFormatWithUnit(), normalFont));
            }

            Worth worthAbatementFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VVS", project.getId());
            if (worthAbatementFullFormwork != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + worthAbatementFullFormwork.worthFormatWithUnit(), normalFont));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VollS", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Vollschalung: "
                        + worthFullFormwork.worthFormatWithUnit(), normalFont));
            }
        }
        if (component != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), normalFont));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLV", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId());
        if (worthProductCosts
                != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPLV", project.getId());
        if (worthTime
                != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), normalFont));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId());
        if (worthMontage
                != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId());
        if (worthTotal
                != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addMaterialAssembly(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Material für Montage", subFont));

        WorthController worthJpaController = new WorthController();

        Worth allAroundPrice = worthJpaController.findWorthByShortTermAndProjectId("GMFM", project.getId());

        if (allAroundPrice != null) {
            paragraph.add(new Paragraph("Gesamtpreis: "
                    + allAroundPrice.worthFormatWithUnit(), normalFont));
        }

        AssemblyController assemblyJpaController = new AssemblyController();
        List<Assembly> listAssembly = assemblyJpaController.findAssembliesByProjectId(project.getId());

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 10f);

        PdfPTable table = new PdfPTable(new float[]{5f, 3.5f, 3.5f, 4, 4});

        table.setWidthPercentage(100f);

        table.addCell(
                new Phrase("Produkt", tableHeaderFont));
        table.addCell(
                new Phrase("Anzahl", tableHeaderFont));
        table.addCell(
                new Phrase("Preis in €", tableHeaderFont));
        table.addCell(
                new Phrase("Bauteil", tableHeaderFont));
        table.addCell(
                new Phrase("Gesamtpreis Produkt in €", tableHeaderFont));

        for (Assembly assembly : listAssembly) {
            table.addCell(new Phrase(assembly.getProduct() == null ? "" : assembly.getProduct().getName(), tableNormalFont));
            table.addCell(new Phrase("" + assembly.getNumberOfComponents(), tableNormalFont));
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(assembly.getPrice()), tableNormalFont));
            table.addCell(new Phrase(assembly.getComponent() == null ? "" : assembly.getComponent().getDescription(), tableNormalFont));
            double allAroundPriceD = assembly.getPrice() * assembly.getNumberOfComponents();
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(allAroundPriceD), tableNormalFont));
        }

        paragraph.add(table);
        document.add(paragraph);
    }

    private void addColor(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Farbe", subFont));

        ComponentController componentJpaController = new ComponentController();
        Component color = componentJpaController.findColorByProjectId(project.getId());

        if (color != null) {
            paragraph.add(new Paragraph("Farbe: "
                    + color.getProduct().getName(), normalFont));
        }

        WorthController worthJpaController = new WorthController();

        Worth colorFactor = worthJpaController.findWorthByShortTermAndProjectId("FK", project.getId());
        if (colorFactor != null) {
            paragraph.add(new Paragraph("Farbfaktor: "
                    + colorFactor.worthFormatWithUnit(), normalFont));
        }

        Worth requiredColorMeter = worthJpaController.findWorthByShortTermAndProjectId("FMM", project.getId());
        if (requiredColorMeter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in m²: "
                    + requiredColorMeter.worthFormatWithUnit(), normalFont));
        }

        Worth requiredColorLiter = worthJpaController.findWorthByShortTermAndProjectId("FML", project.getId());
        if (requiredColorLiter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in l: "
                    + requiredColorLiter.worthFormatWithUnit(), normalFont));
        }

        addEmptyLine(paragraph, 1);

        if (color != null) {
            paragraph.add(new Paragraph("Preis Farbe pro Liter: "
                    + UtilityFormat.formatValueWithShortTerm(color.getPriceComponent(), "€"), normalFont));
        }

        Worth timeColor = worthJpaController.findWorthByShortTermAndProjectId("ZPFA", project.getId());
        if (timeColor != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + timeColor.worthFormatWithUnit(), normalFont));
        }

        Worth priceColor = worthJpaController.findWorthByShortTermAndProjectId("PMFP", project.getId());
        if (priceColor != null) {
            paragraph.add(new Paragraph("Preis für Montage: "
                    + priceColor.worthFormatWithUnit(), normalFont));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPFarbe", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Farbe Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), normalFont));
        }
        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMFarbe", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), normalFont));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKFarbe", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Farbe: "
                    + worthTotal.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addCarriage(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Transport", subFont));

        WorthController worthJpaController = new WorthController();

        Worth kilometreAllowance = worthJpaController.findWorthByShortTermAndProjectId("KMG", project.getId());

        if (kilometreAllowance != null) {
            paragraph.add(new Paragraph("Kilometergeld: "
                    + kilometreAllowance.worthFormatWithUnit(), normalFont));
        }

        Worth distanceCarriage = worthJpaController.findWorthByShortTermAndProjectId("ET", project.getId());

        if (distanceCarriage != null) {
            paragraph.add(new Paragraph("Entfernung: "
                    + distanceCarriage.worthFormatWithUnit(), normalFont));
        }

        Worth worthDays = worthJpaController.findWorthByShortTermAndProjectId("TA", project.getId());
        if (worthDays != null) {
            paragraph.add(new Paragraph("Tage: "
                    + worthDays.worthFormatWithUnit(), normalFont));
        }

        Worth priceCarriage = worthJpaController.findWorthByShortTermAndProjectId("PLS", project.getId());

        if (priceCarriage != null) {
            paragraph.add(new Paragraph("Preis LKW/Stunde: "
                    + priceCarriage.worthFormatWithUnit(), normalFont));
        }

        Worth durationCarriage = worthJpaController.findWorthByShortTermAndProjectId("DT", project.getId());

        if (durationCarriage != null) {
            paragraph.add(new Paragraph("Dauer: "
                    + durationCarriage.worthFormatWithUnit(), normalFont));
        }

        Worth kilometrePriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KT", project.getId());

        if (kilometrePriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Transport: "
                    + kilometrePriceCarriage.worthFormatWithUnit(), normalFont));
        }

        Worth durationPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KA", project.getId());

        if (durationPriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Aufenthalt: "
                    + durationPriceCarriage.worthFormatWithUnit(), normalFont));
        }

        Worth allRoundPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("GPT", project.getId());

        if (allRoundPriceCarriage != null) {
            paragraph.add(new Paragraph("Gesamtpreis Transport: "
                    + allRoundPriceCarriage.worthFormatWithUnit(), normalFont));
        }

        document.add(paragraph);
    }

    private void addMaterialCostList(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Material- und Kostenliste", subFont));
        ComponentController componentJpaController = new ComponentController();
        java.util.List<Component> listComponents = componentJpaController.findComponentsByProjectIdOrderById(project.getId());

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 9f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 9f);

        PdfPTable table = new PdfPTable(new float[]{4.5f, 4.5f, 4, 2, 2, 2, 2, 2, 2, 3});
        table.setWidthPercentage(100f);

        table.addCell(
                new Phrase("Bauteil", tableHeaderFont));
        table.addCell(
                new Phrase("Produkt", tableHeaderFont));
        table.addCell(
                new Phrase("Kategorie", tableHeaderFont));
        table.addCell(
                new Phrase("Breite in cm", tableHeaderFont));
        table.addCell(
                new Phrase("Höhe in cm", tableHeaderFont));
        table.addCell(
                new Phrase("Länge in m", tableHeaderFont));
        table.addCell(
                new Phrase("Preis/Einheit", tableHeaderFont));
        table.addCell(
                new Phrase("Einheit", tableHeaderFont));
        table.addCell(
                new Phrase("Anzahl", tableHeaderFont));
        table.addCell(
                new Phrase("Gesamtpreis", tableHeaderFont));

        for (Component component : listComponents) {
            table.addCell(new Phrase(component.getDescription(), tableNormalFont));
            table.addCell(new Phrase(component.getProduct().getName(), tableNormalFont));
            table.addCell(new Phrase(component.getCategory() == null ? "" : component.getCategory().getLongAndShortTerm(), tableNormalFont));
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getWidthComponent() == null ? 0 : component.getWidthComponent()), tableNormalFont));
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getHeightComponent() == null ? 0 : component.getHeightComponent()), tableNormalFont));
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getLengthComponent() == null ? 0 : component.getLengthComponent()), tableNormalFont));
            table.addCell(new Phrase(component.getUnit() == null ? "" : component.getUnit().getShortTerm(), tableNormalFont));
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getPriceComponent() == null ? 0 : component.getPriceComponent()), tableNormalFont));
            table.addCell(new Phrase(component.getNumberOfProducts() == null ? "0" : "" + component.getNumberOfProducts(), tableNormalFont));
            double allAroundPrice = component.getPriceComponent() * component.getNumberOfProducts();
            table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(allAroundPrice), tableNormalFont));
        }

        paragraph.add(table);
        document.add(paragraph);
    }

    private void addSummary(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Kosten Übersicht", subFont));

        PdfPTable table = new PdfPTable(new float[]{6, 4.5f, 4.5f, 4.5f});
        table.setWidthPercentage(100f);

        Font tableHeaderFont = new Font(Font.FontFamily.TIMES_ROMAN, 9.5f, Font.BOLD);
        Font tableNormalFont = new Font(Font.FontFamily.TIMES_ROMAN, 9f);
        Font tableTinyFont = new Font(Font.FontFamily.TIMES_ROMAN, 6f);

        java.util.List<String[]> parameterList = new ArrayList<>();
        parameterList.add(new String[]{"GP", "KZPG", "GKV"});
        parameterList.add(new String[]{"SPROK", "KMS", "GKS"});
        parameterList.add(new String[]{"SSPROK", "KMSS", "GKSS"});
        parameterList.add(new String[]{"KProF", "KMF", "GKF"});
        parameterList.add(new String[]{"KProD", "KMonD", "GKND"});
        parameterList.add(new String[]{"KProKL", "KMonKL", "GKKL"});
        parameterList.add(new String[]{"KPLatVoll", "KMLatVoll", "GKLatVoll"});
        parameterList.add(new String[]{"GMFM", "", "GMFM"});
        parameterList.add(new String[]{"KPFarbe", "KMFarbe", "GKFarbe"});
        parameterList.add(new String[]{"GPT", "", "GPT"});

        java.util.List<String> capitationList = new ArrayList<>();
        capitationList.add("Holzmaterial");
        capitationList.add("Schalung");
        capitationList.add("Sichtbare Schalung");
        capitationList.add("Folie");
        capitationList.add("Nageldichtband");
        capitationList.add("Konterlattung");
        capitationList.add("Lattung oder Vollschalung");
        capitationList.add("Material für Montage");
        capitationList.add("Farbe");
        capitationList.add("Transport");

        PdfPCell cellDummy = new PdfPCell(new Phrase("", tableHeaderFont));
        cellDummy.setBorder(Rectangle.NO_BORDER);
        cellDummy.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellDummy.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cellDummy);

        PdfPCell cellProductTitle = new PdfPCell(new Phrase("Material", tableHeaderFont));
        cellProductTitle.setBorder(Rectangle.NO_BORDER);
        cellProductTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellProductTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellProductTitle);

        PdfPCell cellMontageTitle = new PdfPCell(new Phrase("Lohn", tableHeaderFont));
        cellMontageTitle.setBorder(Rectangle.NO_BORDER);
        cellMontageTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellMontageTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellMontageTitle);

        PdfPCell cellTotalTitle = new PdfPCell(new Phrase("Material & Lohn", tableHeaderFont));
        cellTotalTitle.setBorder(Rectangle.NO_BORDER);
        cellTotalTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalTitle);

        WorthController worthJpaController = new WorthController();

        double totalCosts = 0;
        for (int i = 0; i < 10; i++) {
            String[] parameterArray = parameterList.get(i);

            PdfPCell cellTitle = new PdfPCell(new Phrase(capitationList.get(i), tableNormalFont));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cellTitle);

            for (String parameter : parameterArray) {
                Worth worth = worthJpaController.findWorthByShortTermAndProjectId(parameter, project.getId());
                PdfPCell cell;
                if (worth != null) {
                    if (parameterArray[2].equals(parameter)) {
                        totalCosts += worth.getWorth();
                    }
                    cell = new PdfPCell(new Phrase(UtilityFormat.formatWorth(worth), tableNormalFont));
                } else {
                    cell = new PdfPCell(new Phrase("0.00", tableNormalFont));
                }
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

            }
        }

        double valueAddedTax = Double.valueOf(0.2);//SETTINGS

        table.addCell(cellDummy);
        table.addCell(cellDummy);
        table.addCell(cellDummy);
        table.addCell(cellDummy);
        table.addCell(cellDummy);
        table.addCell(cellDummy);

        PdfPCell cellTotalExclusiveTitle = new PdfPCell(new Phrase("exkl. MwSt.", tableTinyFont));
        cellTotalExclusiveTitle.setBorder(Rectangle.NO_BORDER);
        cellTotalExclusiveTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalExclusiveTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTotalExclusiveTitle);

        PdfPCell cellTotalExclusive = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts, "€"), tableNormalFont));
        cellTotalExclusive.setBorder(Rectangle.TOP);
        cellTotalExclusive.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalExclusive.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalExclusive);

        table.addCell(cellDummy);
        table.addCell(cellDummy);

        PdfPCell cellTotalValueAddedTaxTitle = new PdfPCell(new Phrase("MwSt. 20%", tableTinyFont));
        cellTotalValueAddedTaxTitle.setBorder(Rectangle.NO_BORDER);
        cellTotalValueAddedTaxTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalValueAddedTaxTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTotalValueAddedTaxTitle);

        PdfPCell cellTotalValueAddedTax = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts * valueAddedTax, "€"), tableNormalFont));
        cellTotalValueAddedTax.setBorder(Rectangle.NO_BORDER);
        cellTotalValueAddedTax.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalValueAddedTax.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalValueAddedTax);

        PdfPCell cellTotal = new PdfPCell(new Phrase("Gesamtpreis", tableHeaderFont));
        cellTotal.setBorder(Rectangle.NO_BORDER);
        cellTotal.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cellTotal);

        table.addCell(cellDummy);

        PdfPCell cellTotaInklusiceTitle = new PdfPCell(new Phrase("inkl. MwSt.", tableTinyFont));
        cellTotaInklusiceTitle.setBorder(Rectangle.NO_BORDER);
        cellTotaInklusiceTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotaInklusiceTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTotaInklusiceTitle);

        PdfPCell cellTotalInklusive = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts * (1 + valueAddedTax), "€"), tableHeaderFont));
        cellTotalInklusive.setBorder(Rectangle.NO_BORDER);
        cellTotalInklusive.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalInklusive.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalInklusive);

        paragraph.add(table);
        document.add(paragraph);
    }

    public void setListPrint(java.util.List<Boolean> listPrint) {
        this.listPrint = listPrint;
    }

}
