package main.utils;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import main.models.Log;

public class RepairStatementPrinter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float FONT_SIZE = 10;

    public static void print(String repairCode, List<Log> logs, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float y = page.getMediaBox().getHeight() - MARGIN;

            content.beginText();
            content.setFont(bold, 14);
            content.newLineAtOffset(MARGIN, y);
            content.showText("Repair Statement - " + repairCode);
            content.endText();
            y -= LINE_HEIGHT * 2;

            content.setFont(font, FONT_SIZE);

            for (Log log : logs) {
                String line = "[" + log.getCreatedAt().format(DATE_FORMAT) + "] "
                    + log.getUserName() + " (" + log.getAction() + "): " + log.getDetails();

                for (String wrapped : wrap(line, 100)) {
                    if (y < MARGIN) {
                        content.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        content.setFont(font, FONT_SIZE);
                        y = page.getMediaBox().getHeight() - MARGIN;
                    }

                    content.beginText();
                    content.newLineAtOffset(MARGIN, y);
                    content.showText(wrapped);
                    content.endText();
                    y -= LINE_HEIGHT;
                }
            }

            content.close();
            document.save(outputFile);
        }
    }

    private static List<String> wrap(String text, int maxChars) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String word : text.split(" ")) {
            if (current.length() + word.length() + 1 > maxChars) {
                lines.add(current.toString());
                current = new StringBuilder();
            }
            if (current.length() > 0) current.append(" ");
            current.append(word);
        }
        if (current.length() > 0) lines.add(current.toString());

        return lines;
    }
}