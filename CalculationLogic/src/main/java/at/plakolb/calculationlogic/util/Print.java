/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.print.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Andreas
 */
public class Print {

    private static final Font HEADFONT = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static final Font NORMALFONT = new Font(Font.FontFamily.TIMES_ROMAN, 10);
    private static final Font BOLDFONT = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLDITALIC);
    private static final Font SUBFONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private final String path;
    private final Project project;
    private final String city;

    private String absolutePath;
    private Document document;

    private List<Boolean> listPrint = new ArrayList<>();

    public Print(String path, Project project, String city) {
        this.path = path;
        this.project = project;
        this.city = city;
    }

    /**
     * Creates a PDF file of a project.
     *
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public String createPDF() throws DocumentException, FileNotFoundException, PrintInformationException {
        String fileName = project.getProjectName().replace(" ", "_") + "_" + UtilityFormat.getDateTime(LocalDateTime.now()) + ".pdf";
        File file = new File(path + "/" + fileName);
        absolutePath = file.getAbsolutePath();
        System.out.println("Erstelle: " + file.toPath());
        document = new Document();
        document.setPageSize(PageSize.A4);
        float cm = 72.0f / 2.54f;
        document.setMargins(2.54f * cm, 2.54f * cm, 2.54f * cm, 2.54f * cm);
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

    public void print(PrintService service, int copies) throws PrinterException, IOException {
        PDDocument doc = PDDocument.load(new File(absolutePath));

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(service);
        double cm = 72.0 / 2.54;
        PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        attr.add(Chromaticity.MONOCHROME);
        attr.add(new JobName("OnTopCalc - " + project.getProjectName(), Locale.ROOT));
        attr.add(MediaSizeName.ISO_A4);

        Paper paper = new Paper();
        paper.setSize(21.0 * cm, 29.5 * cm);
        paper.setImageableArea(0, 0, paper.getWidth(), paper.getHeight()); // no margins

        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);

        Book book = new Book();
        book.append(new PDFPrintable(doc), pageFormat, doc.getNumberOfPages());
        job.setPageable(book);

        job.print(attr);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        if (number < 0) {
            paragraph.add(new Paragraph(" ", new Font(Font.FontFamily.TIMES_ROMAN, 5)));
        } else {
            for (int i = 0; i < number; i++) {
                paragraph.add(new Paragraph(" "));
            }
        }
    }

    private static void addClient(Document document, Client client) throws DocumentException, PrintInformationException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        if (client.getName() != null && client.getStreet() != null && client.getZipCode() != null && client.getCity() != null) {
            paragraph.add(new Paragraph(client.getName(), NORMALFONT));
            paragraph.add(new Paragraph(client.getStreet(), NORMALFONT));
            paragraph.add(new Paragraph(client.getZipCode() + " " + client.getCity(), NORMALFONT));
            paragraph.setAlignment(Element.ALIGN_LEFT);
            addEmptyLine(paragraph, 2);
            document.add(paragraph);
        } else
            throw new PrintInformationException("Auftraggeber hat keinen Namen");

    }

    private static void addCity(Document document, String city) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph(city, NORMALFONT));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        addEmptyLine(paragraph, 2);
        document.add(paragraph);
    }

    private static void addTitle(Document document, Project project) throws DocumentException, PrintInformationException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        if (project.getProjectName() != null) {
            paragraph.add(new Paragraph(project.getProjectName(), HEADFONT));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
        } else {
            throw new PrintInformationException("Kein Projektname vorhanden");
        }
    }

    private void addConstruction(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();

        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Allgemeine Informationen", SUBFONT));

        if (project.getCreationDate() != null) {
            paragraph.add(new Paragraph("Erstellt am: "
                    + UtilityFormat.getDate(project.getCreationDate()), NORMALFONT));
        }
        if (project.getConstructionType() != null) {
            paragraph.add(new Paragraph("Konstruktionstyp: "
                    + project.getConstructionType(), NORMALFONT));
        }
        if (project.getRoofForm() != null) {
            paragraph.add(new Paragraph("Dachform: "
                    + project.getRoofForm(), NORMALFONT));
        }
        if (project.getDescription() != null) {
            paragraph.add(new Paragraph("Notiz:\n"
                    + project.getDescription(), NORMALFONT));
        }
        document.add(paragraph);
    }

    private void addPreCut(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Grund- und Dachflächenermittlung", SUBFONT));

        WorthController worthJpaController = new WorthController();

        Worth area = worthJpaController.findWorthByShortTermAndProjectId("A", project.getId());
        if (area != null) {
            paragraph.add(new Paragraph("Grundfläche: "
                    + area.worthFormatWithUnit(), NORMALFONT));
        }

        Worth roofAreaWithoutRoofOverhang = worthJpaController.findWorthByShortTermAndProjectId("D", project.getId());
        if (roofAreaWithoutRoofOverhang != null) {
            paragraph.add(new Paragraph("Dachfläche "
                    + UtilityFormat.getStringForLabel(roofAreaWithoutRoofOverhang), NORMALFONT));
        }

        Worth roofOverhang = worthJpaController.findWorthByShortTermAndProjectId("DV", project.getId());
        if (roofOverhang != null) {
            paragraph.add(new Paragraph("Dachvorsprung "
                    + UtilityFormat.getStringForLabel(roofOverhang), NORMALFONT));
        }

        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("DF", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche mit Dachvorsprung: "
                    + UtilityFormat.getStringForLabel(roofArea), NORMALFONT));
        }

        document.add(paragraph);
    }

    private void addCubicMeter(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Holzmaterial für Konsruktion", SUBFONT));
        addEmptyLine(paragraph, -1);

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
            table.addCell(new Phrase("Anzahl",
                    tableHeaderFont));
            table.addCell(new Phrase("m³",
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
                        * component.getLengthComponent() * component.getNumberOfProducts(), "m³"), NORMALFONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(
                        component.getWidthComponent() / 100 * component.getHeightComponent() / 100
                                * component.getLengthComponent() * component.getNumberOfProducts() * component.getPriceComponent(), "€"), NORMALFONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours(), "h"), NORMALFONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringPricePerHour(), "€"), NORMALFONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours() * component.getTailoringPricePerHour(), "€"), NORMALFONT));
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
                    + priceTotal.worthFormatWithUnit(), NORMALFONT));
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
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Schalung", SUBFONT));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("D", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche: "
                    + roofArea.worthFormatWithUnit(), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), NORMALFONT));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFormwork.worthFormatWithUnit(), NORMALFONT));
        }

        Worth formwork = worthJpaController.findWorthByShortTermAndProjectId("S", project.getId());
        if (formwork != null) {
            paragraph.add(new Paragraph("Schalung: "
                    + formwork.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
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
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Sichtbare Schalung", SUBFONT));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("DV", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachvorsprung: "
                    + roofArea.worthFormatWithUnit(), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), NORMALFONT));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VSS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFormwork.worthFormatWithUnit(), NORMALFONT));
        }

        Worth visableFormwork = worthJpaController.findWorthByShortTermAndProjectId("SS", project.getId());
        if (visableFormwork != null) {
            paragraph.add(new Paragraph("Sichtbare Schalung: "
                    + visableFormwork.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPSS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPSS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SSPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMSS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKSS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
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

        if (component != null && !component.getFullNameProduct().isEmpty()) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Folie", SUBFONT));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("DF", project.getId());

        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche mit Dachvorsprung: "
                    + roofArea.worthFormatWithUnit(), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("FUEP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), NORMALFONT));
        }

        Worth abatementFoil = worthJpaController.findWorthByShortTermAndProjectId("FUE", project.getId());
        if (abatementFoil != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementFoil.worthFormatWithUnit(), NORMALFONT));
        }

        Worth foil = worthJpaController.findWorthByShortTermAndProjectId("F", project.getId());
        if (foil != null) {
            paragraph.add(new Paragraph("Folie: "
                    + foil.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPF", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPF", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProF", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMF", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKF", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
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

        if (component != null && !component.getFullNameProduct().isEmpty()) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph(
                    "Nageldichtband", SUBFONT));
        }
        addEmptyLine(paragraph, -1);
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
                    + total.worthFormatWithUnit(), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VDP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), NORMALFONT));
        }

        Worth abatementSealingTape = worthJpaController.findWorthByShortTermAndProjectId("DP", project.getId());
        if (abatementSealingTape != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementSealingTape.worthFormatWithUnit(), NORMALFONT));
        }

        Worth sealingTape = worthJpaController.findWorthByShortTermAndProjectId("ND", project.getId());
        if (sealingTape != null) {
            paragraph.add(new Paragraph("Nageldichtband: "
                    + sealingTape.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPD", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPD", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProD", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonD", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKND", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Nageldichtband: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
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

        if (component != null && !component.getFullNameProduct().isEmpty()) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Konterlattung", SUBFONT));
        }
        addEmptyLine(paragraph, -1);
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
                    + total.worthFormatWithUnit(), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VKLP", project.getId());

        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                    + abatementPercent.worthFormatWithUnit(), NORMALFONT));
        }

        Worth abatementCounterBatten = worthJpaController.findWorthByShortTermAndProjectId("VKL", project.getId());
        if (abatementCounterBatten != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + abatementCounterBatten.worthFormatWithUnit(), NORMALFONT));
        }

        Worth counterBatten = worthJpaController.findWorthByShortTermAndProjectId("KL", project.getId());
        if (counterBatten != null) {
            paragraph.add(new Paragraph("Konterlattung: "
                    + counterBatten.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPKL", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPKL", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProKL", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonKL", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKKL", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
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

        if (component != null && !component.getFullNameProduct().isEmpty()) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Lattung oder Vollschalung", SUBFONT));
        }

        if (project.getRoofMaterial() != null) {
            paragraph.add(new Paragraph("Art: "
                    + project.getRoofMaterial(), NORMALFONT));
        }

        WorthController worthJpaController = new WorthController();
        if (index == 0) {
            Worth worthSlatSpacing = worthJpaController.findWorthByShortTermAndProjectId("LA", project.getId());
            if (worthSlatSpacing != null) {
                paragraph.add(new Paragraph("Lattenabstand in cm: "
                        + worthSlatSpacing.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                        + worthAbatementPercent.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthLengthOfTheBattens = worthJpaController.findWorthByShortTermAndProjectId("LDOV", project.getId());
            if (worthLengthOfTheBattens != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten ohne Verschnitt: "
                        + worthLengthOfTheBattens.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthAbatementBatten = worthJpaController.findWorthByShortTermAndProjectId("VL", project.getId());
            if (worthAbatementBatten != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + worthAbatementBatten.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("LL", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten: "
                        + worthFullFormwork.worthFormatWithUnit(), NORMALFONT));
            }
        } else {
            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                        + worthAbatementPercent.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthRoofArea = worthJpaController.findWorthByShortTermAndProjectId("DF", project.getId());
            if (worthRoofArea != null) {
                paragraph.add(new Paragraph("Dachfläche: "
                        + worthRoofArea.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthAbatementFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VVS", project.getId());
            if (worthAbatementFullFormwork != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + worthAbatementFullFormwork.worthFormatWithUnit(), NORMALFONT));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VollS", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Vollschalung: "
                        + worthFullFormwork.worthFormatWithUnit(), NORMALFONT));
            }
        }
        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLV", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + worthCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId());
        if (worthProductCosts
                != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPLV", project.getId());
        if (worthTime
                != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + worthTime.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId());
        if (worthMontage
                != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId());
        if (worthTotal
                != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
        }

        document.add(paragraph);
    }

    private void addMaterialAssembly(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Material für Montage", SUBFONT));
        addEmptyLine(paragraph, -1);
        WorthController worthJpaController = new WorthController();

        Worth allAroundPrice = worthJpaController.findWorthByShortTermAndProjectId("GMFM", project.getId());

        if (allAroundPrice != null) {
            paragraph.add(new Paragraph("Gesamtpreis: "
                    + allAroundPrice.worthFormatWithUnit(), NORMALFONT));
        }

        AssemblyController assemblyJpaController = new AssemblyController();
        List<Assembly> listAssembly = assemblyJpaController.findAssembliesByProjectId(project.getId());

        boolean hasContent = false;
        for (Assembly assembly : listAssembly) {
            if (assembly.getProduct() != null) {
                hasContent = true;
                break;
            }
        }

        if (hasContent) {

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
        } else {
            paragraph.add(new Paragraph("Keine Materialen vorhanden",NORMALFONT));
        }
        document.add(paragraph);
    }

    private void addColor(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Farbe", SUBFONT));

        ComponentController componentJpaController = new ComponentController();
        Component color = componentJpaController.findColorByProjectId(project.getId());

        if (color != null && color.getProduct() != null) {
            paragraph.add(new Paragraph("Farbe: "
                    + color.getProduct().getName(), NORMALFONT));
        }

        WorthController worthJpaController = new WorthController();

        Worth colorFactor = worthJpaController.findWorthByShortTermAndProjectId("FK", project.getId());
        if (colorFactor != null) {
            paragraph.add(new Paragraph("Farbfaktor: "
                    + colorFactor.worthFormatWithUnit(), NORMALFONT));
        }

        Worth requiredColorMeter = worthJpaController.findWorthByShortTermAndProjectId("FMM", project.getId());
        if (requiredColorMeter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in m²: "
                    + requiredColorMeter.worthFormatWithUnit(), NORMALFONT));
        }

        Worth requiredColorLiter = worthJpaController.findWorthByShortTermAndProjectId("FML", project.getId());
        if (requiredColorLiter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in l: "
                    + requiredColorLiter.worthFormatWithUnit(), NORMALFONT));
        }

        addEmptyLine(paragraph, 1);

        if (color != null && color.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis Farbe pro Liter: "
                    + UtilityFormat.formatValueWithShortTerm(color.getPriceComponent(), "€"), NORMALFONT));
        }

        Worth timeColor = worthJpaController.findWorthByShortTermAndProjectId("ZPFA", project.getId());
        if (timeColor != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + timeColor.worthFormatWithUnit(), NORMALFONT));
        }

        Worth priceColor = worthJpaController.findWorthByShortTermAndProjectId("PMFP", project.getId());
        if (priceColor != null) {
            paragraph.add(new Paragraph("Preis für Montage: "
                    + priceColor.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPFarbe", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Farbe Produkt Kosten: "
                    + worthProductCosts.worthFormatWithUnit(), NORMALFONT));
        }
        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMFarbe", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + worthMontage.worthFormatWithUnit(), NORMALFONT));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKFarbe", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Farbe: "
                    + worthTotal.worthFormatWithUnit(), NORMALFONT));
        }

        document.add(paragraph);
    }

    private void addCarriage(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Transport", SUBFONT));

        WorthController worthJpaController = new WorthController();

        Worth kilometreAllowance = worthJpaController.findWorthByShortTermAndProjectId("KMG", project.getId());

        if (kilometreAllowance != null) {
            paragraph.add(new Paragraph("Kilometergeld: "
                    + kilometreAllowance.worthFormatWithUnit(), NORMALFONT));
        }

        Worth distanceCarriage = worthJpaController.findWorthByShortTermAndProjectId("ET", project.getId());

        if (distanceCarriage != null) {
            paragraph.add(new Paragraph("Entfernung: "
                    + distanceCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth worthDays = worthJpaController.findWorthByShortTermAndProjectId("TA", project.getId());
        if (worthDays != null) {
            paragraph.add(new Paragraph("Tage: "
                    + worthDays.worthFormatWithUnit(), NORMALFONT));
        }

        Worth priceCarriage = worthJpaController.findWorthByShortTermAndProjectId("PLS", project.getId());

        if (priceCarriage != null) {
            paragraph.add(new Paragraph("Preis LKW/Stunde: "
                    + priceCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth durationCarriage = worthJpaController.findWorthByShortTermAndProjectId("DT", project.getId());

        if (durationCarriage != null) {
            paragraph.add(new Paragraph("Dauer: "
                    + durationCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth kilometrePriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KT", project.getId());

        if (kilometrePriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Transport: "
                    + kilometrePriceCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth durationPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KA", project.getId());

        if (durationPriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Aufenthalt: "
                    + durationPriceCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        Worth allRoundPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("GPT", project.getId());

        if (allRoundPriceCarriage != null) {
            paragraph.add(new Paragraph("Gesamtpreis Transport: "
                    + allRoundPriceCarriage.worthFormatWithUnit(), NORMALFONT));
        }

        document.add(paragraph);
    }

    private void addMaterialCostList(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Material- und Kostenliste", SUBFONT));
        addEmptyLine(paragraph, -1);
        ComponentController componentJpaController = new ComponentController();
        java.util.List<Component> listComponents = componentJpaController.findComponentsByProjectIdOrderById(project.getId());

        boolean hasContent = false;
        for (Component component : listComponents) {
            if (component.getProduct() != null) {
                hasContent = true;
                break;
            }
        }

        if (hasContent) {
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
                if (component.getProduct() != null) {
                    table.addCell(new Phrase(component.getDescription(), tableNormalFont));
                    if (component.getProduct() != null)
                        table.addCell(new Phrase(component.getProduct().getName(), tableNormalFont));
                    else
                        table.addCell(new Phrase("-"));
                    table.addCell(new Phrase(component.getCategory() == null ? "" : component.getCategory().getLongAndShortTerm(), tableNormalFont));
                    table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getWidthComponent() == null ? 0 : component.getWidthComponent()), tableNormalFont));
                    table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getHeightComponent() == null ? 0 : component.getHeightComponent()), tableNormalFont));
                    table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getLengthComponent() == null ? 0 : component.getLengthComponent()), tableNormalFont));
                    table.addCell(new Phrase(component.getUnit() == null ? "" : component.getUnit().getShortTerm(), tableNormalFont));
                    table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(component.getPriceComponent() == null ? 0 : component.getPriceComponent()), tableNormalFont));
                    table.addCell(new Phrase(component.getNumberOfProducts() == null ? "0" : "" + component.getNumberOfProducts(), tableNormalFont));
                    if (component.getPriceComponent() != null && component.getNumberOfProducts() != null) {
                        double allAroundPrice = component.getPriceComponent() * component.getNumberOfProducts();
                        table.addCell(new Phrase(UtilityFormat.twoDecimalPlaces(allAroundPrice), tableNormalFont));
                    } else {
                        table.addCell(new Phrase("-", tableNormalFont));
                    }
                }
            }

            paragraph.add(table);
        } else {
            paragraph.add(new Paragraph("Keine Produkte vorhanden", NORMALFONT));
        }
        document.add(paragraph);
    }

    private void addSummary(Document document, Project project) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        addEmptyLine(paragraph, 1);
        paragraph.add(new Paragraph("Kosten Übersicht", SUBFONT));
        addEmptyLine(paragraph, -1);
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
