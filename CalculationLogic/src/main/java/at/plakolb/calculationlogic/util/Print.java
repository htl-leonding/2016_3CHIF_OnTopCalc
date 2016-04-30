/*	HTL Leonding	*/
package at.plakolb.calculationlogic.util;

import at.plakolb.calculationlogic.entity.Client;
import at.plakolb.calculationlogic.entity.Project;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    private java.util.List<Boolean> listPrint = new ArrayList<>();

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

    public void getPage(int idx) throws IOException {

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

    private void addConstruction(Document document, Project project) {
    }

    private void addPreCut(Document document, Project project) {
    }

    private void addCubicMeter(Document document, Project project) {
    }

    private void addFormwork(Document document, Project project) {
    }

    private void addVisableFormwork(Document document, Project project) {
    }

    private void addFoil(Document document, Project project) {
    }

    private void addSealingBand(Document document, Project project) {
    }

    private void addCounterBatten(Document document, Project project) {
    }

    private void addBattensOrFullFormwork(Document document, Project project) {
    }

    private void addMaterialAssembly(Document document, Project project) {
    }

    private void addColor(Document document, Project project) {
    }

    private void addCarriage(Document document, Project project) {
    }

    private void addMaterialCostList(Document document, Project project) {
    }

    private void addSummary(Document document, Project project) {
    }

    public void setListPrint(java.util.List<Boolean> listPrint) {
        this.listPrint = listPrint;
    }
    
}
