package com.deathz.chat.application.service;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    public String processPDF(MultipartFile pdf) {

        StringBuilder content = new StringBuilder();

        content.append("\n======== Start of PDF Content ========\n");

        try (PDDocument document = PDDocument.load(pdf.getInputStream())) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            String text = stripper.getText(document);
            content.append(text);
        } catch (IOException e) {

            e.printStackTrace();
        }

        content.append("\n======== End of PDF Content ========\n");
        return content.toString();
    }
}
