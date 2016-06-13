/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.db.controller.AssemblyController;
import at.plakolb.calculationlogic.db.controller.CategoryController;
import at.plakolb.calculationlogic.db.controller.ComponentController;
import at.plakolb.calculationlogic.db.controller.WorthController;
import at.plakolb.calculationlogic.db.entity.*;
import at.plakolb.calculationlogic.eunmeration.ProductType;
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
    private static final Font SUBFONT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD);
    private static final Font TABLE_NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9f);
    private static final Font TABLE_SMALL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8f);

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
        System.out.println(absolutePath);
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
                    + UtilityFormat.getStringForLabel(area), NORMALFONT));
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

        PdfPTable table = new PdfPTable(new float[]{5.5f, 5f, 3f, 3f, 3f, 3f, 3.5f, 3.5f, 3.5f});

        table.setWidthPercentage(100f);

        ComponentController componentJpaController = new ComponentController();
        java.util.List<Component> components = componentJpaController.findComponentsByProjectIdAndComponentType(project.getId(), "Kubikmeter");

        if (components.size() > 0) {

            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Kategorie", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Holzprodukt", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Anzahl", TABLE_HEADER_FONT));
            table.addCell(new Phrase("m³", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Preis m³/€", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Preis €", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Zuschnitt h", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Zuschnitt €/h", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Zuschnitt €", TABLE_HEADER_FONT));

            for (Component component : components) {
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(new Phrase(component.getCategory().getLongAndShortTerm(), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(component.getProduct().getName(), TABLE_NORMAL_FONT));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(new Phrase(UtilityFormat.getStringForLabel(component.getNumberOfProducts()), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getWidthComponent() / 100 * component.getHeightComponent() / 100
                        * component.getLengthComponent() * component.getNumberOfProducts(), "m³"), TABLE_NORMAL_FONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getPriceComponent() / (component.getWidthComponent() / 100 * component.getHeightComponent() / 100
                        * component.getLengthComponent() * component.getNumberOfProducts()), "€/m³"), TABLE_NORMAL_FONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€"), TABLE_NORMAL_FONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours(), "h"), TABLE_NORMAL_FONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringPricePerHour(), "€"), TABLE_NORMAL_FONT));

                table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(component.getTailoringHours() * component.getTailoringPricePerHour(), "€"), TABLE_NORMAL_FONT));
            }
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();


        double cubicMeter = 0;
        double price = 0;
        double duration = 0;
        double tailoringPrice = 0;

        for (Component component : components) {
            cubicMeter += component.getHeightComponent() / 100 * component.getWidthComponent() / 100 * component.getLengthComponent() * component.getNumberOfProducts();
            price += component.getPriceComponent();
            duration += component.getTailoringHours();
            tailoringPrice += component.getTailoringPricePerHour() * component.getTailoringHours();
        }

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(new Phrase("", TABLE_HEADER_FONT));
        table.addCell(new Phrase("", TABLE_HEADER_FONT));
        table.addCell(new Phrase("", TABLE_HEADER_FONT));
        table.addCell(new Phrase(UtilityFormat.formatValueWithShortTerm(cubicMeter, "m³"), TABLE_HEADER_FONT));
        table.addCell(new Phrase("", TABLE_HEADER_FONT));
        table.addCell(new Phrase(UtilityFormat.getStringForLabel(price), TABLE_HEADER_FONT));
        table.addCell(new Phrase(UtilityFormat.getStringForLabel(duration), TABLE_HEADER_FONT));
        table.addCell(new Phrase("", TABLE_HEADER_FONT));
        table.addCell(new Phrase(UtilityFormat.getStringForLabel(tailoringPrice), TABLE_HEADER_FONT));

        Worth priceTotal = worthJpaController.findWorthByShortTermAndProjectId("GKV", project.getId());
        if (priceTotal != null) {
            paragraph.add(new Paragraph("Gesamtpreis: "
                    + UtilityFormat.getStringForLabel(priceTotal), NORMALFONT));
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

        if (component != null && component.getFullNameProduct() != "") {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Schalung", SUBFONT));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("D", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachfläche: "
                    + UtilityFormat.getStringForLabel(roofArea), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€/m²") + " €/m²", NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementPercent), NORMALFONT));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementFormwork), NORMALFONT));
        }

        Worth formwork = worthJpaController.findWorthByShortTermAndProjectId("S", project.getId());
        if (formwork != null) {
            paragraph.add(new Paragraph("Schalung: "
                    + UtilityFormat.getStringForLabel(formwork), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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

        if (component != null && component.getFullNameProduct() != "") {
            paragraph.add(new Paragraph("Sichtbare " + component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Sichtbare Schalung", SUBFONT));
        }

        WorthController worthJpaController = new WorthController();
        Worth roofArea = worthJpaController.findWorthByShortTermAndProjectId("DV", project.getId());
        if (roofArea != null) {
            paragraph.add(new Paragraph("Dachvorsprung: "
                    + UtilityFormat.getStringForLabel(roofArea), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€/m²") + " €/m²", NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VSSP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementPercent), NORMALFONT));
        }

        Worth abatementFormwork = worthJpaController.findWorthByShortTermAndProjectId("VSS", project.getId());
        if (abatementFormwork != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementFormwork), NORMALFONT));
        }

        Worth visableFormwork = worthJpaController.findWorthByShortTermAndProjectId("SS", project.getId());
        if (visableFormwork != null) {
            paragraph.add(new Paragraph("Sichtbare Schalung: "
                    + UtilityFormat.getStringForLabel(visableFormwork), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPSS", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPSS", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("SSPROK", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMSS", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKSS", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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
                    + UtilityFormat.getStringForLabel(roofArea), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€/m²") + " €/m²", NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("FUEP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementPercent), NORMALFONT));
        }

        Worth abatementFoil = worthJpaController.findWorthByShortTermAndProjectId("FUE", project.getId());
        if (abatementFoil != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementFoil), NORMALFONT));
        }

        Worth foil = worthJpaController.findWorthByShortTermAndProjectId("F", project.getId());
        if (foil != null) {
            paragraph.add(new Paragraph("Folie: "
                    + UtilityFormat.getStringForLabel(foil), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPF", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPF", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProF", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMF", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKF", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Schalung: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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

        if (componentJpaController.findAll().size() > 0) {

            table.addCell(new Phrase("Name", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Länge der Dachsparren", TABLE_HEADER_FONT));

            componentJpaController.findAll().stream().filter(comp -> comp.getCategory().getShortTerm().equals("KD")).forEach(comp -> {
                table.addCell(new Phrase(comp.getFullNameProduct(), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTextField(comp.getLengthComponent() * comp.getNumberOfProducts()) + " m", TABLE_NORMAL_FONT));
            });
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();
        Worth total = worthJpaController.findWorthByShortTermAndProjectId("LD", project.getId());
        if (total != null) {
            paragraph.add(new Paragraph("Summe: "
                    + UtilityFormat.getStringForLabel(total), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€/Lfm") + " €/Lfm", NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VDP", project.getId());
        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementPercent), NORMALFONT));
        }

        Worth abatementSealingTape = worthJpaController.findWorthByShortTermAndProjectId("DP", project.getId());
        if (abatementSealingTape != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementSealingTape), NORMALFONT));
        }

        Worth sealingTape = worthJpaController.findWorthByShortTermAndProjectId("ND", project.getId());
        if (sealingTape != null) {
            paragraph.add(new Paragraph("Nageldichtband: "
                    + UtilityFormat.getStringForLabel(sealingTape), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPD", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPD", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProD", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonD", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKND", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Nageldichtband: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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

        if (component != null && component.getFullNameProduct()!=null&&!component.getFullNameProduct().isEmpty()) {
            paragraph.add(new Paragraph(component.getFullNameProduct(), SUBFONT));
        } else {
            paragraph.add(new Paragraph("Konterlattung", SUBFONT));
        }
        addEmptyLine(paragraph, -1);
        PdfPTable table = new PdfPTable(new float[]{6f, 3.5f});
        table.setWidthPercentage(100f);

        if (componentJpaController.findAll().size() > 0) {

            table.addCell(new Phrase("Name", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Länge der Dachsparren", TABLE_HEADER_FONT));

            componentJpaController.findAll().stream().filter(comp -> comp.getCategory().getShortTerm().equals("KD")).forEach(comp -> {
                table.addCell(new Phrase(comp.getFullNameProduct(), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTextField(comp.getLengthComponent() * comp.getNumberOfProducts()) + " m", TABLE_NORMAL_FONT));
            });
        }
        paragraph.add(table);

        WorthController worthJpaController = new WorthController();
        Worth total = worthJpaController.findWorthByShortTermAndProjectId("LD", project.getId());

        if (total != null) {
            paragraph.add(new Paragraph("Summe: "
                    + UtilityFormat.getStringForLabel(total), NORMALFONT));
        }

        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€/Lfm") + " €/Lfm", NORMALFONT));
        }

        Worth abatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VKLP", project.getId());

        if (abatementPercent != null) {
            paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementPercent), NORMALFONT));
        }

        Worth abatementCounterBatten = worthJpaController.findWorthByShortTermAndProjectId("VKL", project.getId());
        if (abatementCounterBatten != null) {
            paragraph.add(new Paragraph("Verschnitt: "
                    + UtilityFormat.getStringForLabel(abatementCounterBatten), NORMALFONT));
        }

        Worth counterBatten = worthJpaController.findWorthByShortTermAndProjectId("KL", project.getId());
        if (counterBatten != null) {
            paragraph.add(new Paragraph("Konterlattung: "
                    + UtilityFormat.getStringForLabel(counterBatten), NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPKL", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPKL", project.getId());
        if (worthTime != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KProKL", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMonKL", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }

        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKKL", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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

        if (component != null && component.getProduct() != null && component.getProduct().getProductType().equals(ProductType.FORMWORK)) {
            paragraph.add(new Paragraph("Voll" + component.getFullNameProduct(), SUBFONT));
        } else if (component != null && !component.getFullNameProduct().isEmpty()) {
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
                        + UtilityFormat.getStringForLabel(worthSlatSpacing), NORMALFONT));
            }

            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt: "
                        + UtilityFormat.getStringForLabel(worthAbatementPercent), NORMALFONT));
            }

            Worth worthLengthOfTheBattens = worthJpaController.findWorthByShortTermAndProjectId("LDOV", project.getId());
            if (worthLengthOfTheBattens != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten ohne Verschnitt: "
                        + UtilityFormat.getStringForLabel(worthLengthOfTheBattens), NORMALFONT));
            }

            Worth worthAbatementBatten = worthJpaController.findWorthByShortTermAndProjectId("VL", project.getId());
            if (worthAbatementBatten != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + UtilityFormat.getStringForLabel(worthAbatementBatten), NORMALFONT));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("LL", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Länge der Dachlatten: "
                        + UtilityFormat.getStringForLabel(worthFullFormwork), NORMALFONT));
            }
        } else {
            Worth worthAbatementPercent = worthJpaController.findWorthByShortTermAndProjectId("VLVP", project.getId());
            if (worthAbatementPercent != null) {
                paragraph.add(new Paragraph("Eingegebener Verschnitt in Prozent: "
                        + UtilityFormat.getStringForLabel(worthAbatementPercent), NORMALFONT));
            }

            Worth worthRoofArea = worthJpaController.findWorthByShortTermAndProjectId("DF", project.getId());
            if (worthRoofArea != null) {
                paragraph.add(new Paragraph("Dachfläche: "
                        + UtilityFormat.getStringForLabel(worthRoofArea), NORMALFONT));
            }

            Worth worthAbatementFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VVS", project.getId());
            if (worthAbatementFullFormwork != null) {
                paragraph.add(new Paragraph("Verschnitt: "
                        + UtilityFormat.getStringForLabel(worthAbatementFullFormwork), NORMALFONT));
            }

            Worth worthFullFormwork = worthJpaController.findWorthByShortTermAndProjectId("VollS", project.getId());
            if (worthFullFormwork != null) {
                paragraph.add(new Paragraph("Vollschalung: "
                        + UtilityFormat.getStringForLabel(worthFullFormwork), NORMALFONT));
            }
        }
        if (component != null && component.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis: "
                    + UtilityFormat.formatValueWithShortTerm(component.getPriceComponent(), "€") + " €", NORMALFONT));
        }

        Worth worthCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLV", project.getId());
        if (worthCosts != null) {
            paragraph.add(new Paragraph("Kosten Montage pro Stunde: "
                    + UtilityFormat.getStringForLabel(worthCosts), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPLatVoll", project.getId());
        if (worthProductCosts
                != null) {
            paragraph.add(new Paragraph("Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }

        Worth worthTime = worthJpaController.findWorthByShortTermAndProjectId("ZPLV", project.getId());
        if (worthTime
                != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(worthTime), NORMALFONT));
        }

        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMLatVoll", project.getId());
        if (worthMontage
                != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKLatVoll", project.getId());
        if (worthTotal
                != null) {
            paragraph.add(new Paragraph("Gesamtkosten Konterlattung: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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
                    + UtilityFormat.getStringForLabel(allAroundPrice), NORMALFONT));
        }
        addEmptyLine(paragraph,-1);

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

            PdfPTable table = new PdfPTable(new float[]{5f, 3.5f, 3.4f, 4, 4.2f});

            table.setWidthPercentage(100f);

            table.addCell(new Phrase("Produkt", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Anzahl", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Preis in €", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Bauteil", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Gesamtpreis Produkt in €", TABLE_HEADER_FONT));

            for (Assembly assembly : listAssembly) {
                table.addCell(new Phrase(assembly.getProduct() == null ? "" : assembly.getProduct().getName(), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(assembly.getNumberOfComponents()), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(assembly.getPrice()), TABLE_NORMAL_FONT));
                table.addCell(new Phrase(assembly.getComponent() == null ? "" : assembly.getComponent().getFullNameProduct(), TABLE_NORMAL_FONT));
                double allAroundPriceD = assembly.getPrice() * assembly.getNumberOfComponents();
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(allAroundPriceD), TABLE_NORMAL_FONT));
            }

            paragraph.add(table);
        } else {
            paragraph.add(new Paragraph("Keine Materialen vorhanden", NORMALFONT));
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
                    + UtilityFormat.getStringForLabel(colorFactor), NORMALFONT));
        }

        Worth requiredColorMeter = worthJpaController.findWorthByShortTermAndProjectId("FMM", project.getId());
        if (requiredColorMeter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in m²: "
                    + UtilityFormat.getStringForLabel(requiredColorMeter), NORMALFONT));
        }

        Worth requiredColorLiter = worthJpaController.findWorthByShortTermAndProjectId("FML", project.getId());
        if (requiredColorLiter != null) {
            paragraph.add(new Paragraph("Benötigte Farbe in l: "
                    + UtilityFormat.getStringForLabel(requiredColorLiter), NORMALFONT));
        }

        addEmptyLine(paragraph, 1);

        if (color != null && color.getPriceComponent() != null) {
            paragraph.add(new Paragraph("Preis Farbe pro Liter: "
                    + UtilityFormat.formatValueWithShortTerm(color.getPriceComponent(), "€/l") + " €/l", NORMALFONT));
        }

        Worth timeColor = worthJpaController.findWorthByShortTermAndProjectId("ZPFA", project.getId());
        if (timeColor != null) {
            paragraph.add(new Paragraph("Zeit für Montage: "
                    + UtilityFormat.getStringForLabel(timeColor), NORMALFONT));
        }

        Worth priceColor = worthJpaController.findWorthByShortTermAndProjectId("PMFP", project.getId());
        if (priceColor != null) {
            paragraph.add(new Paragraph("Preis für Montage: "
                    + UtilityFormat.getStringForLabel(priceColor), NORMALFONT));
        }

        Worth worthProductCosts = worthJpaController.findWorthByShortTermAndProjectId("KPFarbe", project.getId());
        if (worthProductCosts != null) {
            paragraph.add(new Paragraph("Farbe Produkt Kosten: "
                    + UtilityFormat.getStringForLabel(worthProductCosts), NORMALFONT));
        }
        Worth worthMontage = worthJpaController.findWorthByShortTermAndProjectId("KMFarbe", project.getId());
        if (worthMontage != null) {
            paragraph.add(new Paragraph("Montage Kosten: "
                    + UtilityFormat.getStringForLabel(worthMontage), NORMALFONT));
        }
        Worth worthTotal = worthJpaController.findWorthByShortTermAndProjectId("GKFarbe", project.getId());
        if (worthTotal != null) {
            paragraph.add(new Paragraph("Gesamtkosten Farbe: "
                    + UtilityFormat.getStringForLabel(worthTotal), NORMALFONT));
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
                    + UtilityFormat.getStringForLabel(kilometreAllowance), NORMALFONT));
        }

        Worth distanceCarriage = worthJpaController.findWorthByShortTermAndProjectId("ET", project.getId());

        if (distanceCarriage != null) {
            paragraph.add(new Paragraph("Entfernung: "
                    + UtilityFormat.getStringForLabel(distanceCarriage), NORMALFONT));
        }

        Worth worthDays = worthJpaController.findWorthByShortTermAndProjectId("TA", project.getId());
        if (worthDays != null) {
            paragraph.add(new Paragraph("Tage: "
                    + UtilityFormat.getStringForLabel(worthDays), NORMALFONT));
        }

        Worth priceCarriage = worthJpaController.findWorthByShortTermAndProjectId("PLS", project.getId());

        if (priceCarriage != null) {
            paragraph.add(new Paragraph("Preis LKW/Stunde: "
                    + UtilityFormat.getStringForLabel(priceCarriage), NORMALFONT));
        }

        Worth durationCarriage = worthJpaController.findWorthByShortTermAndProjectId("DT", project.getId());

        if (durationCarriage != null) {
            paragraph.add(new Paragraph("Dauer: "
                    + UtilityFormat.getStringForLabel(durationCarriage), NORMALFONT));
        }

        Worth kilometrePriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KT", project.getId());

        if (kilometrePriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Transport: "
                    + UtilityFormat.getStringForLabel(kilometrePriceCarriage), NORMALFONT));
        }

        Worth durationPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("KA", project.getId());

        if (durationPriceCarriage != null) {
            paragraph.add(new Paragraph("Kosten Aufenthalt: "
                    + UtilityFormat.getStringForLabel(durationPriceCarriage), NORMALFONT));
        }

        Worth allRoundPriceCarriage = worthJpaController.findWorthByShortTermAndProjectId("GPT", project.getId());

        if (allRoundPriceCarriage != null) {
            paragraph.add(new Paragraph("Gesamtpreis Transport: "
                    + UtilityFormat.getStringForLabel(allRoundPriceCarriage), NORMALFONT));
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
            Font TABLE_HEADER_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9f, Font.BOLD);
            Font TABLE_NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 9f);

            PdfPTable table = new PdfPTable(new float[]{4.5f, 4.5f, 4, 2, 2, 2, 2.2f, 2.2f, 3.2f, 3.7f});
            table.setWidthPercentage(100f);

            table.addCell(new Phrase("Bauteil", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Produkt", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Kategorie", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Breite in cm", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Höhe in cm", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Länge in m", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Einheit", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Preis/ Einheit", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Anzahl", TABLE_HEADER_FONT));
            table.addCell(new Phrase("Gesamtpreis", TABLE_HEADER_FONT));

            listComponents.stream().filter(component -> component.getProduct() != null).forEach(component -> {

                table.addCell(new Phrase(component.getFullNameProduct(), TABLE_NORMAL_FONT));
                if (component.getProduct() != null)
                    table.addCell(new Phrase(component.getProduct().getName(), TABLE_NORMAL_FONT));
                else
                    table.addCell(new Phrase("-"));

                table.addCell(new Phrase(component.getCategory() == null ? "" : component.getCategory().getLongAndShortTerm(), TABLE_SMALL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(component.getWidthComponent() == null ? 0 : component.getWidthComponent()), TABLE_SMALL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(component.getHeightComponent() == null ? 0 : component.getHeightComponent()), TABLE_SMALL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(component.getLengthComponent() == null ? 0 : component.getLengthComponent()), TABLE_SMALL_FONT));
                table.addCell(new Phrase(component.getUnit() == null ? "" : component.getUnit().getShortTerm(), TABLE_SMALL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(component.getPriceComponent() == null ? 0 : component.getPriceComponent()), TABLE_SMALL_FONT));
                table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(component.getNumberOfProducts() == null ? 0 : component.getNumberOfProducts()), TABLE_SMALL_FONT));

                if (component.getPriceComponent() != null && component.getNumberOfProducts() != null) {
                    double allAroundPrice = component.getPriceComponent() * component.getNumberOfProducts();
                    table.addCell(new Phrase(UtilityFormat.getStringForTableColumn(allAroundPrice), TABLE_SMALL_FONT));
                } else {
                    table.addCell(new Phrase("-", TABLE_NORMAL_FONT));
                }
            });

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

        PdfPCell cellDummy = new PdfPCell(new Phrase("", TABLE_HEADER_FONT));
        cellDummy.setBorder(Rectangle.NO_BORDER);
        cellDummy.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellDummy.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cellDummy);

        PdfPCell cellProductTitle = new PdfPCell(new Phrase("Material", TABLE_HEADER_FONT));
        cellProductTitle.setBorder(Rectangle.NO_BORDER);
        cellProductTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellProductTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellProductTitle);

        PdfPCell cellMontageTitle = new PdfPCell(new Phrase("Lohn", TABLE_HEADER_FONT));
        cellMontageTitle.setBorder(Rectangle.NO_BORDER);
        cellMontageTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellMontageTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellMontageTitle);

        PdfPCell cellTotalTitle = new PdfPCell(new Phrase("Material & Lohn", TABLE_HEADER_FONT));
        cellTotalTitle.setBorder(Rectangle.NO_BORDER);
        cellTotalTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalTitle);

        WorthController worthJpaController = new WorthController();

        double totalCosts = 0;
        for (int i = 0; i < 10; i++) {
            String[] parameterArray = parameterList.get(i);

            PdfPCell cellTitle = new PdfPCell(new Phrase(capitationList.get(i), TABLE_NORMAL_FONT));
            cellTitle.setBorder(Rectangle.NO_BORDER);
            cellTitle.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cellTitle);

            for (int c = 0;c<parameterArray.length;c++) {
                Worth worth = worthJpaController.findWorthByShortTermAndProjectId(parameterArray[c], project.getId());
                PdfPCell cell;
                if (worth != null) {
                    if (c == parameterArray.length-1) {
                        totalCosts += worth.getWorth();
                        System.out.println(worth.getWorth());
                    }
                    cell = new PdfPCell(new Phrase(UtilityFormat.twoDecimalPlaces(worth.getWorth()), TABLE_NORMAL_FONT));
                } else {
                    cell = new PdfPCell(new Phrase("-", TABLE_NORMAL_FONT));
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

        PdfPCell cellTotalExclusive = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts, "€"), TABLE_NORMAL_FONT));
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

        PdfPCell cellTotalValueAddedTax = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts * valueAddedTax, "€"), TABLE_NORMAL_FONT));
        cellTotalValueAddedTax.setBorder(Rectangle.NO_BORDER);
        cellTotalValueAddedTax.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTotalValueAddedTax.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTotalValueAddedTax);

        PdfPCell cellTotal = new PdfPCell(new Phrase("Gesamtpreis", TABLE_HEADER_FONT));
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

        PdfPCell cellTotalInklusive = new PdfPCell(new Phrase(UtilityFormat.formatValueWithShortTerm(totalCosts * (1 + valueAddedTax), "€"), TABLE_HEADER_FONT));
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